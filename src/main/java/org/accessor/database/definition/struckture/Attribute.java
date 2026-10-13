package org.accessor.database.definition.struckture;

import java.sql.Timestamp;

public class Attribute {

    private final String name;
    private final DataType type;
    private boolean isPrimaryKey;

    public Attribute(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public enum DataType {
        STRING(String.class),
        INT(Integer.class),
        FLOAT(Float.class),
        BOOLEAN(Boolean.class),
        DOUBLE(java.util.UUID.class),
        TIME_STAMP(Timestamp.class);

        private final Class<?> javaClass;

        DataType(Class<?> javaClass) {
            this.javaClass = javaClass;
        }

        public Class<?> getJavaClass() {
            return javaClass;
        }
    }
}
