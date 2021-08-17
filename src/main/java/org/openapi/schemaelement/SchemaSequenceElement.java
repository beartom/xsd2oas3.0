package org.openapi.schemaelement;

import java.util.List;
import java.util.stream.Collectors;

public class SchemaSequenceElement extends SchemaAbstractElement {

    @Override
    public String getFullName() {
        return null;
    }

    public SchemaSequenceElement(String prefix) {
        super(prefix);
    }

    @Override
    public boolean isContainer(){
        return true;
    }

    @Override
    public boolean isRequired() {
        return getSubElements().stream().anyMatch(SchemaAbstractElement::isRequired);
    }

    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();

        //Should be choice in side sequence.
        List<SchemaAbstractElement> containerElementList = getSubElements().stream().filter(SchemaAbstractElement::isContainer).collect(Collectors.toList());

        List<SchemaNamedRefElement> propertyElementList = getSubElements().stream().filter(subElement -> subElement instanceof SchemaNamedRefElement).map(subElement -> (SchemaNamedRefElement)subElement).collect(Collectors.toList());

        List<SchemaTypeRefElement> refTypeElementList = getSubElements().stream().filter(subElement -> subElement instanceof SchemaTypeRefElement).map(subElement -> (SchemaTypeRefElement)subElement).collect(Collectors.toList());

        if(!containerElementList.isEmpty()) {
            containerElementList.forEach(containerElement -> {
                sb.append(containerElement.convertSchema()).append(",\n");
            });

            if(propertyElementList.isEmpty()) {
                sb.setLength(sb.length() - 2);
            }
            return sb;
        }

        if(!refTypeElementList.isEmpty()) {
            if(sb.length()>0){
                sb.append(",\n");
            }
            sb.append("{\n")
                    //.append("  'type': 'object',\n")
                    .append("  'anyOf': [\n");
            refTypeElementList.forEach(element -> {
                sb.append("{").append((element).getRefName(this.getPrefix())).append("},\n");
            });
            sb.setLength(sb.length() - 2);
            sb.append("\n  ]");
            sb.append("\n}");
        }

        if(!propertyElementList.isEmpty()) {
            if(sb.length()>0){
                sb.append(",\n");
            }
            sb.append("{\n")
                    .append("  'type': 'object',\n")
                    .append("  'properties': {\n");
            propertyElementList.stream().filter(element -> element instanceof SchemaNamedRefElement).forEach(element -> {
                sb.append(retract(element.convertSchema(), 2)).append(",\n");
            });
            sb.setLength(sb.length() - 2);
            sb.append("\n  }");
            List<String> requiredPropertyNames = propertyElementList.stream().filter(element -> element instanceof SchemaNamedRefElement).map(element -> (SchemaNamedRefElement) element).filter(SchemaAbstractElement::isRequired).map(SchemaNamedElement::getRequired).collect(Collectors.toList());

            if (requiredPropertyNames.size() > 0) {
                sb.append(",\n  'required': [");
                sb.append(requiredPropertyNames.stream().collect(Collectors.joining(", ")));
                sb.append("]");
            }
            sb.append("\n}");
        }

        return sb;
    }
}
