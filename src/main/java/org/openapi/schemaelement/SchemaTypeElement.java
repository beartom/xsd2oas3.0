package org.openapi.schemaelement;

import org.openapi.core.ConvertConfig;

public abstract class SchemaTypeElement extends SchemaNamedElement {

    public SchemaTypeElement(String prefix, String name) {
        super(prefix, name);
    }

    @Override
    public String getFullName() {
        if(this.getPrefix()==null||this.getPrefix().trim().equals("")){
            return this.getPropertyName();
        }
        return this.getPrefix()+"_"+this.getPropertyName();
    }

    public StringBuilder getRefName(String prefix){
        StringBuilder sb = new StringBuilder();
        sb.append("'$ref':'");
        if (ConvertConfig.SEPARATED_FILE && !prefix.equals(this.getPrefix())) {
            sb.append(this.getPrefix()).append(".yaml");
        }
        sb.append(ConvertConfig.REF_PREFIX).append(this.getFullName()).append("'");
        return sb;
    }
}
