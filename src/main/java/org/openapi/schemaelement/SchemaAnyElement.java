package org.openapi.schemaelement;

import org.openapi.core.ConvertConfig;

public class SchemaAnyElement extends SchemaPrivilegeElement {

    public SchemaAnyElement(String prefix) {
        super(prefix);
    }

    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();
        if(ConvertConfig.REF_ANYTYPE == ConvertConfig.ANYTYPE_OPTION.COMPLICATED) {
            sb.append("{\n  'anyOf': [\n")
                    .append("    {\n")
                    .append("      'type': 'string'")
                    .append("\n    },\n")
                    .append("    {\n")
                    .append("      'type': 'number'")
                    .append("\n    },\n")
                    .append("    {\n")
                    .append("      'type': 'integer'")
                    .append("\n    },\n")
                    .append("    {\n")
                    .append("      'type': 'boolean'")
                    .append("\n    },\n")
                    .append("    {\n")
                    .append("      'type': 'array',\n      'items': {}")
                    .append("\n    },\n")
                    .append("    {\n")
                    .append("      'type': 'object'")
                    .append("\n    }\n")
                    .append("  ]\n}");
        }else{
            sb.append("{}");
        }
        return sb;
    }

}
