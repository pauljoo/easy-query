package info.xpanda.easy.query.dsl;

import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Set;

public class XpandaSchema implements Schema {
    @Override
    public @Nullable Table getTable(String s) {
        return null;
    }

    @Override
    public Set<String> getTableNames() {
        return null;
    }

    @Override
    public @Nullable RelProtoDataType getType(String s) {
        return null;
    }

    @Override
    public Set<String> getTypeNames() {
        return null;
    }

    @Override
    public Collection<Function> getFunctions(String s) {
        return null;
    }

    @Override
    public Set<String> getFunctionNames() {
        return null;
    }

    @Override
    public @Nullable Schema getSubSchema(String s) {
        return null;
    }

    @Override
    public Set<String> getSubSchemaNames() {
        return null;
    }

    @Override
    public Expression getExpression(@Nullable SchemaPlus schemaPlus, String s) {
        return null;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Schema snapshot(SchemaVersion schemaVersion) {
        return null;
    }
}
