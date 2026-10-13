package org.accessor.database.definition.data;

import org.accessor.database.definition.struckture.Attribute;
import org.accessor.database.definition.struckture.Table;

import java.util.HashMap;

public class RowBuilder {

    private final HashMap<String, Field<?>> fieldHashMap = new HashMap<>();
    private final Table table;

    public RowBuilder(Table table) {
        this.table = table;
    }

    public RowBuilder addField(Attribute attribute, Object fieldValue) {
        fieldHashMap.put(attribute.getName(), new Field<>(attribute, fieldValue));
        return this;
    }

    public Row build() {
        return new Row(table, fieldHashMap);
    }
}
