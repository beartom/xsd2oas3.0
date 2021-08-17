package org.openapi.schemaelement;

public class SchemaBooleanElement extends SchemaPrivilegeElement {

    public SchemaBooleanElement(String prefix) {
        super(prefix);
    }

    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();
        sb.append("'type': 'boolean'");
        return sb;
    }
}
