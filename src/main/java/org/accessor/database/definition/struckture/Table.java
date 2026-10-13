package org.accessor.database.definition.struckture;

import java.util.ArrayList;
import java.util.Arrays;

public class Table {

    private final String name;
    private final Attribute primaryKey;
    private final ArrayList<Attribute> attributes;

    public Table(String name, Attribute primaryKey, ArrayList<Attribute> attributes) {
        this.name = name;
        this.primaryKey = primaryKey;
        primaryKey.setPrimaryKey(true);
        attributes.addFirst(primaryKey);
        this.attributes = attributes;
    }

    public Table(String name, Attribute primaryKey, Attribute... attributes) {
        this.name = name;
        this.primaryKey = primaryKey;
        primaryKey.setPrimaryKey(true);
        ArrayList<Attribute> attributes1 = new ArrayList<>(Arrays.stream(attributes).toList());
        attributes1.addFirst(primaryKey);
        this.attributes = attributes1;
    }

    public String getName() {
        return name;
    }

    public int getTotalAttributeSize() {
        return attributes.size();
    }

    public Attribute getPrimaryKeyAtt() {
        return primaryKey;
    }

    public Attribute getAttribute(String attName) {
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(attName)) return attribute;
        }

        return null;
    }

    public ArrayList<Attribute> getAttribute(String... attName) {
        ArrayList<Attribute> attributes = new ArrayList<>();

        for (Attribute attribute : this.attributes) {
            if (Arrays.stream(attName).toList().contains(attribute.getName())) attributes.add(attribute);
        }

        return attributes;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public ArrayList<String> getAttributeNames() {
        ArrayList<String> attributeName = new ArrayList<>();
        for (Attribute attribute : this.attributes) attributeName.add(attribute.getName());
        return attributeName;
    }
}
