/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.fasten.analyzer.graphplugin.db;

import static eu.fasten.core.data.KnowledgeBase.bfsperm;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.google.common.primitives.Longs;
import eu.fasten.core.data.KnowledgeBase;
import eu.fasten.core.index.BVGraphSerializer;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.NullInputStream;
import it.unimi.dsi.lang.MutableString;
import it.unimi.dsi.sux4j.util.EliasFanoMonotoneLongBigList;
import it.unimi.dsi.webgraph.ArrayListMutableGraph;
import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.Transform;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.CompressionType;
import org.rocksdb.DBOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocksDao implements Closeable {

    private final RocksDB rocksDb;
    private final ColumnFamilyHandle defaultHandle;
    private Kryo kryo;
    private SoftReference<KnowledgeBase.CallGraphData> graphData;
    private final Logger logger = LoggerFactory.getLogger(RocksDao.class.getName());

    /**
     * Constructor of RocksDao (Database Access Object).
     *
     * @param dbDir Directory where RocksDB data will be stored
     * @throws RocksDBException if there is an error loading or opening RocksDB instance
     */
    public RocksDao(final String dbDir) throws RocksDBException {
        RocksDB.loadLibrary();
        final ColumnFamilyOptions cfOptions = new ColumnFamilyOptions()
                .setCompressionType(CompressionType.LZ4_COMPRESSION);
        final DBOptions dbOptions = new DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true);
        final List<ColumnFamilyDescriptor> cfDescriptors = Collections.singletonList(
                new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOptions));
        final List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>();
        this.rocksDb = RocksDB.open(dbOptions, dbDir, cfDescriptors, columnFamilyHandles);
        this.defaultHandle = columnFamilyHandles.get(0);
        initKryo();
    }

    private void initKryo() {
        kryo = new Kryo();
        kryo.register(BVGraph.class, new BVGraphSerializer(kryo));
        kryo.register(byte[].class);
        kryo.register(InputBitStream.class);
        kryo.register(NullInputStream.class);
        kryo.register(EliasFanoMonotoneLongBigList.class, new JavaSerializer());
        kryo.register(MutableString.class, new FieldSerializer<>(kryo, MutableString.class));
        kryo.register(Properties.class);
        kryo.register(long[].class);
        kryo.register(Long2IntOpenHashMap.class);
    }

    /**
     * Inserts graph (nodes and edges) into RocksDB database.
     *
     * @param index       Index of the graph (ID from postgres)
     * @param nodes       List of GID nodes (first internal nodes, then external nodes)
     * @param numInternal Number of internal nodes in nodes list
     * @param edges       List of edges (pairs of GIDs)
     * @throws IOException      if there was a problem writing to files
     * @throws RocksDBException if there was a problem inserting in the database
     */
    public void saveToRocksDb(long index, List<Long> nodes, int numInternal, List<List<Long>> edges)
            throws IOException, RocksDBException {
        final long[] temporary2GID = new long[nodes.size()];
        var nodesList = new LongArrayList(nodes);
        LongIterators.unwrap(nodesList.iterator(), temporary2GID);
        // Compute the reverse map
        final Long2IntOpenHashMap GID2Temporary = new Long2IntOpenHashMap();
        GID2Temporary.defaultReturnValue(-1);
        for (int i = 0; i < temporary2GID.length; i++) {
            GID2Temporary.put(temporary2GID[i], i);
            //final long result = GID2Temporary.put(temporary2GID[i], i);
            //assert result == -1; // Internal and external GIDs should be
            // disjoint by construction
        }
        // Create, store and load compressed versions of the graph and of the transpose.
        // First create the graph as an ArrayListMutableGraph
        final ArrayListMutableGraph mutableGraph = new ArrayListMutableGraph(temporary2GID.length);
        // Add arcs between internal nodes
        for (final List<Long> edge : edges) {
            // TODO: Change to long
            final int sourceId = Math.toIntExact(edge.get(0));
            final int targetId = Math.toIntExact(edge.get(1));
            try {
                mutableGraph.addArc(sourceId, targetId);
            } catch (final IllegalArgumentException e) {
                logger.error("Duplicate arc (" + sourceId + " -> " + targetId + ")", e);
            }
        }
        final var file = File.createTempFile(KnowledgeBase.class.getSimpleName(), ".tmpgraph");
        final var graphProperties = new Properties();
        final var transposeProperties = new Properties();
        FileInputStream propertyFile;
        // Compress, load and serialize graph
        final int[] bfsPerm = bfsperm(mutableGraph.immutableView(), -1, numInternal);
        final ImmutableGraph graph = Transform.map(mutableGraph.immutableView(), bfsPerm);
        BVGraph.store(graph, file.toString());
        propertyFile = new FileInputStream(file + BVGraph.PROPERTIES_EXTENSION);
        graphProperties.load(propertyFile);
        propertyFile.close();
        final FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
        final ByteBufferOutput bbo = new ByteBufferOutput(fbaos);
        kryo.writeObject(bbo, BVGraph.load(file.toString()));
        // Compute LIDs according to the current node renumbering based on BFS
        final long[] LID2GID = new long[temporary2GID.length];
        final Long2IntOpenHashMap GID2LID = new Long2IntOpenHashMap();
        GID2LID.defaultReturnValue(-1);
        for (int x = 0; x < temporary2GID.length; x++) {
            LID2GID[bfsPerm[x]] = temporary2GID[x];
        }
        for (int i = 0; i < temporary2GID.length; i++) {
            GID2LID.put(LID2GID[i], i);
        }
        // Compress, load and serialize transpose graph
        BVGraph.store(Transform.transpose(graph), file.toString());
        propertyFile = new FileInputStream(file + BVGraph.PROPERTIES_EXTENSION);
        transposeProperties.load(propertyFile);
        propertyFile.close();
        kryo.writeObject(bbo, BVGraph.load(file.toString()));
        // Write out properties
        kryo.writeObject(bbo, graphProperties);
        kryo.writeObject(bbo, transposeProperties);
        // Write out maps
        kryo.writeObject(bbo, LID2GID);
        kryo.writeObject(bbo, GID2LID);
        bbo.flush();
        // Write to DB
        rocksDb.put(defaultHandle, Longs.toByteArray(index), 0, 8, fbaos.array, 0, fbaos.length);
        new File(file.toString() + BVGraph.PROPERTIES_EXTENSION).delete();
        new File(file.toString() + BVGraph.OFFSETS_EXTENSION).delete();
        new File(file.toString() + BVGraph.GRAPH_EXTENSION).delete();
        file.delete();
    }

    /**
     * Returns the graph and its transpose in a 2-element array. The
     * graphs are cached, and read from the database if needed.
     *
     * @return an array containing the call graph and its transpose.
     * @throws RocksDBException if could not retrieve data from the database
     */
    public KnowledgeBase.CallGraphData getGraphData(long index, int numInternal)
            throws RocksDBException {
        if (graphData != null) {
            final var graphData = this.graphData.get();
            if (graphData != null) {
                return graphData;
            }
        }
        final byte[] buffer = rocksDb.get(Longs.toByteArray(index));
        final Input input = new Input(buffer);
        assert kryo != null;
        final var graphs = new ImmutableGraph[] {
                kryo.readObject(input, BVGraph.class),
                kryo.readObject(input, BVGraph.class)
        };
        final Properties[] properties = new Properties[] {
                kryo.readObject(input, Properties.class),
                kryo.readObject(input, Properties.class)
        };
        final long[] LID2GID = kryo.readObject(input, long[].class);
        final Long2IntOpenHashMap GID2LID = kryo.readObject(input, Long2IntOpenHashMap.class);
        final KnowledgeBase.CallGraphData graphData = new KnowledgeBase.CallGraphData(
                graphs[0], graphs[1], properties[0], properties[1], LID2GID, GID2LID, numInternal);
        this.graphData = new SoftReference<>(graphData);
        return graphData;
    }

    @Override
    public void close() {
        if (defaultHandle != null) {
            defaultHandle.close();
        }
        if (rocksDb != null) {
            rocksDb.close();
        }
    }
}
