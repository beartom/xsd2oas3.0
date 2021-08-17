package org.openapi.schemaelement;

public class SchemaExtensionElement extends SchemaAbstractElement {

    public SchemaExtensionElement(String prefix) {
        super(prefix);
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();
        //Handle sub elements
        SchemaAbstractElement base = getSubElements().get(0);
        if(base instanceof SchemaSimpleTypeElement){
            sb.append("{\n")
                    .append("  'type': 'object',\n")
                    .append("  'properties': {\n");

            getSubElements().forEach(element -> {
                if(element==base){
                    sb.append("    'Value': {\n").append(retract(((SchemaSimpleTypeElement) base).getRefName(this.getPrefix()),3)).append("\n    },\n");
                }else if(element instanceof SchemaNamedRefElement){
                    sb.append(retract(element.convertSchema(), 2)).append(",\n");
                }else{
                    throw new UnsupportedOperationException("Unknown Element Type"+element.getClass().getName()+" to build Schema");
                }
            });
        }else{
            throw new UnsupportedOperationException("Unknown Type of subElement " + base.getClass().getName() +" to build Extension");
        }
        sb.setLength(sb.length()-2);

        sb.append("\n  }\n}");
        return sb;
    }
}
