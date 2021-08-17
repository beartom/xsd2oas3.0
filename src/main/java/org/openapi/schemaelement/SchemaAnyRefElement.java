package org.openapi.schemaelement;

public class SchemaAnyRefElement extends SchemaTypeRefElement {

    public SchemaAnyRefElement(String prefix, String name) {
        super(prefix, name);
    }

    @Override
    public String getFullName() {
        String fullName = super.getFullName();
        return fullName;
    }

    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(this.getFullName()).append("': {\n")
                .append("  'type': 'object'\n")
                .append("}");
        return sb;
    }
}
