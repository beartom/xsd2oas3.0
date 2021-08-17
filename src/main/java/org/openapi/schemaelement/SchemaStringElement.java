package org.openapi.schemaelement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaStringElement extends  SchemaPrivilegeElement {

    private List<String> enumeration = new ArrayList<>();;

    private Integer minLength;

    private Integer maxLength;

    private String pattern;

    private String format=null;

    public List<String> getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(List<String> enumeration) {
        this.enumeration = enumeration;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int length) {
        this.minLength = length;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public SchemaStringElement(String prefix) {
        super(prefix);
    }

    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();
        sb.append("'type': 'string'");
        if(this.enumeration!=null&&!this.enumeration.isEmpty()){
            sb.append(
                    ",\n'enum': [");
            String enumStr = this.enumeration.stream().collect(Collectors.joining("','","'","'"));
            sb.append(enumStr).append("]");
        }else {
            if(minLength!=null){
                sb.append(",\n'minLength': ").append(minLength);
            }
            if(maxLength!=null){
                sb.append(",\n'maxLength': ").append(maxLength);
            }
            if(pattern!=null){
                sb.append(",\n'pattern': '^").append(pattern.replaceAll("\\\\","\\\\\\\\")).append("$'");
            }
            if(format!=null){
                sb.append(",\n'format': '").append(format).append("'");
            }
        }
        return sb;
    }
}
