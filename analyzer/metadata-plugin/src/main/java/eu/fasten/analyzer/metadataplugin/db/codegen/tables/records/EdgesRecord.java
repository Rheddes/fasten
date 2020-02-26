/*
 * This file is generated by jOOQ.
 */
package eu.fasten.analyzer.metadataplugin.db.codegen.tables.records;


import eu.fasten.analyzer.metadataplugin.db.codegen.tables.Edges;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.TableRecordImpl;

import javax.annotation.processing.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EdgesRecord extends TableRecordImpl<EdgesRecord> implements Record3<Long, Long, JSONB> {

    private static final long serialVersionUID = -603317677;

    /**
     * Setter for <code>public.edges.source_id</code>.
     */
    public void setSourceId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.edges.source_id</code>.
     */
    public Long getSourceId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.edges.target_id</code>.
     */
    public void setTargetId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.edges.target_id</code>.
     */
    public Long getTargetId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.edges.metadata</code>.
     */
    public void setMetadata(JSONB value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.edges.metadata</code>.
     */
    public JSONB getMetadata() {
        return (JSONB) get(2);
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, JSONB> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Long, Long, JSONB> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Edges.EDGES.SOURCE_ID;
    }

    @Override
    public Field<Long> field2() {
        return Edges.EDGES.TARGET_ID;
    }

    @Override
    public Field<JSONB> field3() {
        return Edges.EDGES.METADATA;
    }

    @Override
    public Long component1() {
        return getSourceId();
    }

    @Override
    public Long component2() {
        return getTargetId();
    }

    @Override
    public JSONB component3() {
        return getMetadata();
    }

    @Override
    public Long value1() {
        return getSourceId();
    }

    @Override
    public Long value2() {
        return getTargetId();
    }

    @Override
    public JSONB value3() {
        return getMetadata();
    }

    @Override
    public EdgesRecord value1(Long value) {
        setSourceId(value);
        return this;
    }

    @Override
    public EdgesRecord value2(Long value) {
        setTargetId(value);
        return this;
    }

    @Override
    public EdgesRecord value3(JSONB value) {
        setMetadata(value);
        return this;
    }

    @Override
    public EdgesRecord values(Long value1, Long value2, JSONB value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EdgesRecord
     */
    public EdgesRecord() {
        super(Edges.EDGES);
    }

    /**
     * Create a detached, initialised EdgesRecord
     */
    public EdgesRecord(Long sourceId, Long targetId, JSONB metadata) {
        super(Edges.EDGES);

        set(0, sourceId);
        set(1, targetId);
        set(2, metadata);
    }
}
