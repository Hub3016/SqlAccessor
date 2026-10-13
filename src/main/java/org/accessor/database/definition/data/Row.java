package org.accessor.database.definition.data;

import org.accessor.database.definition.struckture.Attribute;
import org.accessor.database.definition.struckture.Table;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Row {

    //String: Attribute name | Field: Row data for attribute
    private final HashMap<String, Field<?>> rowData;
    private final Table table;

    public Row(Table table, HashMap<String, Field<?>> rowData) {
        this.rowData = rowData;
        this.table = table;
    }

    public Row(Table table, ArrayList<Field<?>> fields) {
        this.table = table;
        HashMap<String, Field<?>> rowData = new HashMap<>();
        fields.forEach(field -> rowData.put(field.getAttribute().getName(), field));
        this.rowData = rowData;
    }

    public Table getTable() {
        return table;
    }

    public HashMap<String, Field<?>> getFields() {
        return rowData;
    }

    public ArrayList<Field<?>> getFieldsAsArrayList() {
        ArrayList<Field<?>> fields = new ArrayList<>();
        for (Map.Entry<String, Field<?>> entry : rowData.entrySet()) fields.add(entry.getValue());
        return fields;
    }

    public @Nullable Field<?> getField(Attribute attribute) {
        for (Map.Entry<String, Field<?>> entry : rowData.entrySet()) {
            if (entry.getKey().equals(attribute.getName())) return entry.getValue();
        }

        return null;
    }

    public @Nullable Field<?> getField(String attName) {
        for (Map.Entry<String, Field<?>> entry : rowData.entrySet()) {
            if (entry.getKey().equals(attName)) return entry.getValue();
        }

        return null;
    }

    public void removeField(String attributeName) {
        rowData.remove(attributeName);
    }

    public boolean isSameTable(Table compareTable) {
        return table.getName().equals(compareTable.getName());
    }

    public boolean isCompleteRow() {
        ArrayList<String> attributes = new ArrayList<>(table.getAttributeNames());
        boolean containsInvalidFields = false;

        for (Map.Entry<String, Field<?>> entry : rowData.entrySet()) {
            String attributeName = entry.getKey();

            if (attributes.contains(attributeName)) attributes.remove(attributeName);
            else {
                containsInvalidFields = true;
                break;
            }
        }

        return !containsInvalidFields && attributes.isEmpty();
    }
}
