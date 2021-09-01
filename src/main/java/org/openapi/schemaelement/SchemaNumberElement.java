package org.openapi.schemaelement;

import org.openapi.core.ConvertConfig;

public class SchemaNumberElement extends SchemaPrivilegeElement {

    public SchemaNumberElement(String prefix) {
        super(prefix);
    }

    public Integer totalDigits;

    public Integer fractionDigits;

    public Boolean exclusiveMinimum;

    public Boolean exclusiveMaximum;

    public String minimum;

    public String maximum;

    public Boolean getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    public void setExclusiveMinimum(Boolean exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public Boolean getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    public void setExclusiveMaximum(Boolean exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public Integer getTotalDigits() {
        return totalDigits;
    }

    public void setTotalDigits(Integer totalDigits) {
        this.totalDigits = totalDigits;
    }


    public Integer getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(Integer fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();
        if (ConvertConfig.MULTI_TYPE_SUPPORT.equals(ConvertConfig.MULTITYPE_OPTION.BOTH)) {
            sb.append("'oneOf': [\n")
                    .append("  {\n")
                    .append("    'type': 'number'");
            if (this.minimum != null) {
                sb.append(",\n")
                        .append("    'minimum': ").append(minimum);
            }
            if (this.maximum != null) {
                sb.append(",\n")
                        .append("    'maximum': ").append(maximum);
            }
            if (this.exclusiveMaximum != null) {
                sb.append(",\n")
                        .append("    'exclusiveMaximum': ").append(exclusiveMaximum);
            }
            if (this.exclusiveMinimum != null) {
                sb.append(",\n")
                        .append("    'exclusiveMinimum': ").append(exclusiveMinimum);
            }
            sb.append("\n  },\n")
                    .append("  {\n")
                    .append("    'type': 'string'");
            if (hasDigitalLimit()) {
                sb.append(",\n");
                sb.append("'pattern': '^").append(getPatternFromDigitalLimit()).append("$'");
            }
            sb.append("\n  }")
                    .append("\n]");
        } else {
            if (toNumber()) {
                sb.append("'type': 'number'");
                if (this.minimum != null) {
                    sb.append(",\n")
                            .append("'minimum': ").append(minimum);
                }
                if (this.maximum != null) {
                    sb.append(",\n")
                            .append("'maximum': ").append(maximum);
                }
                if (this.exclusiveMaximum != null) {
                    sb.append(",\n")
                            .append("'exclusiveMaximum': ").append(exclusiveMaximum);
                }
                if (this.exclusiveMinimum != null) {
                    sb.append(",\n")
                            .append("'exclusiveMinimum': ").append(exclusiveMinimum);
                }
            } else if (hasDigitalLimit()) {
                sb.append("'type': 'string',\n");
                sb.append("'pattern': '^").append(getPatternFromDigitalLimit()).append("$'");
            } else {
                sb.append("'type': 'string'");
            }
        }
        return sb;
    }

    private boolean hasDigitalLimit() {
        return !(this.fractionDigits == null && this.totalDigits == null);
    }

    private boolean toNumber() {
        return !ConvertConfig.MULTI_TYPE_SUPPORT.equals(ConvertConfig.MULTITYPE_OPTION.FORCE_TO_STRING) &&
                (ConvertConfig.MULTI_TYPE_SUPPORT.equals(ConvertConfig.MULTITYPE_OPTION.FORCE_TO_NUMBER) ||
                        !hasDigitalLimit());
    }

    private String getPatternFromDigitalLimit() {
        String result;
        if (this.totalDigits != null) {
            result = "\\\\d{0," + this.totalDigits.intValue() + "}";
        } else {
            result = "\\\\d+";
        }

        if (this.fractionDigits != null) {
            result += "(\\\\.\\\\d{0," + this.fractionDigits.intValue() + "})?";
        }
        if (this.minimum != null) {
            try {
                //Support negative value.
                double minValue = Double.parseDouble(minimum);
                if (minValue < 0) {
                    result = "-?" + result;
                }
            } catch (Exception e) {
                //Ignore
            }
        }

        return result;
    }
}
