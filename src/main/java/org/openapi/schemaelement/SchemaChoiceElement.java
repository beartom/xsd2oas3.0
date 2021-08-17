package org.openapi.schemaelement;

import java.util.List;
import java.util.stream.Collectors;

import static org.openapi.core.ConvertConfig.EVERY_CHOICE_REF_REQUIRED;

public class SchemaChoiceElement extends SchemaAbstractElement {

    public SchemaChoiceElement(String prefix) {
        super(prefix);
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();
        //Should be sequence inside  choice.
        List<SchemaAbstractElement> containerElementList = getSubElements().stream().filter(SchemaAbstractElement::isContainer).collect(Collectors.toList());

        List<SchemaAbstractElement> propertyElementList = getSubElements().stream().filter(subElement -> !subElement.isContainer()).collect(Collectors.toList());

        sb.append("{\n  'oneOf': [\n");

        if (!containerElementList.isEmpty()) {
            containerElementList.forEach(containerElement -> {
                sb.append(retract(containerElement.convertSchema(), 2)).append(",\n");
            });

            if (propertyElementList.isEmpty()) {
                sb.setLength(sb.length() - 2);
            }
            return sb;
        }

        if (!propertyElementList.isEmpty()) {
            propertyElementList.forEach(element -> {
                if (!(element instanceof SchemaNamedRefElement)) {
                    throw new UnsupportedOperationException("Unexpected type  inside Choice: " + element.getClass().getName());
                }
                sb.append("    {\n      'type': 'object',\n")
                        .append("      'properties': {\n");
                sb.append(retract(element.convertSchema(), 4));
                sb.append("\n      }");
                if(EVERY_CHOICE_REF_REQUIRED){
                    sb.append(",\n      'required': ['").append(((SchemaNamedRefElement) element).getPropertyName()).append("']");
                }
                sb.append("\n    },\n");
            });
            sb.setLength(sb.length() - 2);
        }
        sb.append("\n  ]\n");
        sb.append("}");
        return sb;
    }

    @Override
    public String getFullName() {
        return null;
    }
}
