package org.accessor.database.definition.data;

import org.accessor.database.definition.struckture.Attribute;

public class Field<T> {

    private final Attribute attribute;
    private final T rawFieldData;

    public Field(Attribute attribute, T rawFieldData) {
        this.attribute = attribute;
        this.rawFieldData = rawFieldData;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public T getFieldData() {
        return rawFieldData;
    }
}
