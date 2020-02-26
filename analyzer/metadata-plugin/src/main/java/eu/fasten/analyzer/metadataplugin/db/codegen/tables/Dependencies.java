/*
 * This file is generated by jOOQ.
 */
package eu.fasten.analyzer.metadataplugin.db.codegen.tables;


import eu.fasten.analyzer.metadataplugin.db.codegen.Keys;
import eu.fasten.analyzer.metadataplugin.db.codegen.Public;
import eu.fasten.analyzer.metadataplugin.db.codegen.tables.records.DependenciesRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import javax.annotation.processing.Generated;
import java.util.Arrays;
import java.util.List;


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
public class Dependencies extends TableImpl<DependenciesRecord> {

    private static final long serialVersionUID = -731074850;

    /**
     * The reference instance of <code>public.dependencies</code>
     */
    public static final Dependencies DEPENDENCIES = new Dependencies();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DependenciesRecord> getRecordType() {
        return DependenciesRecord.class;
    }

    /**
     * The column <code>public.dependencies.package_id</code>.
     */
    public final TableField<DependenciesRecord, Long> PACKAGE_ID = createField(DSL.name("package_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.dependencies.dependency_id</code>.
     */
    public final TableField<DependenciesRecord, Long> DEPENDENCY_ID = createField(DSL.name("dependency_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.dependencies.version_range</code>.
     */
    public final TableField<DependenciesRecord, String> VERSION_RANGE = createField(DSL.name("version_range"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>public.dependencies</code> table reference
     */
    public Dependencies() {
        this(DSL.name("dependencies"), null);
    }

    /**
     * Create an aliased <code>public.dependencies</code> table reference
     */
    public Dependencies(String alias) {
        this(DSL.name(alias), DEPENDENCIES);
    }

    /**
     * Create an aliased <code>public.dependencies</code> table reference
     */
    public Dependencies(Name alias) {
        this(alias, DEPENDENCIES);
    }

    private Dependencies(Name alias, Table<DependenciesRecord> aliased) {
        this(alias, aliased, null);
    }

    private Dependencies(Name alias, Table<DependenciesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Dependencies(Table<O> child, ForeignKey<O, DependenciesRecord> key) {
        super(child, key, DEPENDENCIES);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<ForeignKey<DependenciesRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DependenciesRecord, ?>>asList(Keys.DEPENDENCIES__DEPENDENCIES_PACKAGE_ID_FKEY, Keys.DEPENDENCIES__DEPENDENCIES_DEPENDENCY_ID_FKEY);
    }

    public PackageVersions dependencies_DependenciesPackageIdFkey() {
        return new PackageVersions(this, Keys.DEPENDENCIES__DEPENDENCIES_PACKAGE_ID_FKEY);
    }

    public PackageVersions dependencies_DependenciesDependencyIdFkey() {
        return new PackageVersions(this, Keys.DEPENDENCIES__DEPENDENCIES_DEPENDENCY_ID_FKEY);
    }

    @Override
    public Dependencies as(String alias) {
        return new Dependencies(DSL.name(alias), this);
    }

    @Override
    public Dependencies as(Name alias) {
        return new Dependencies(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Dependencies rename(String name) {
        return new Dependencies(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dependencies rename(Name name) {
        return new Dependencies(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
