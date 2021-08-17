package org.openapi.schemaelement;

public abstract class SchemaNamedElement extends SchemaAbstractElement {


    private final String propertyName;

    public SchemaNamedElement(String prefix, String name) {
        super(prefix);
        this.propertyName=name;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getRequired() {
        return "'"+propertyName+"'";
    }

}
