package org.openapi.schemaelement;


import org.openapi.core.ConvertConfig;

public class SchemaNamedRefElement extends SchemaNamedElement {

    private SchemaTypeElement ref;

    private SchemaPrivilegeElement privilegeType;

    public SchemaNamedRefElement(String prefix, String name) {
        super(prefix, name);
    }

    public SchemaTypeElement getRef() {
        return ref;
    }

    @Override
    public void addSubElement(SchemaAbstractElement element) {
        if(this.ref!=null){
            throw new UnsupportedOperationException("Unsupported ref more than one element");
        }
        if(element instanceof SchemaTypeElement){
            this.ref = (SchemaTypeElement)element;
        }else if(element instanceof SchemaPrivilegeElement){
            this.privilegeType = (SchemaPrivilegeElement) element;
        }else{
            throw new UnsupportedOperationException("Unsupported ref type " +element.getClass().getName());
        }
    }

    /**
     *  Should be property of an object
     * 1,.m 4p7-0@return
     */
    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(getPropertyName()).append("': {\n");
        if(this.isArray()){
            if(ConvertConfig.ALLOW_SINGLE_OBJECT_IN_ARRAY && this.getMinOccurs()<=1){
                sb.append("  'oneOf': [\n    {\n").append(retract(ref.getRefName(this.getPrefix()),3)).append("\n    },\n    {\n");
            }
            StringBuilder arrayType = new StringBuilder();
            arrayType.append("'type': 'array',\n");
            if(this.getMinOccurs()>0){
                arrayType.append("'minItems': ").append(this.getMinOccurs()).append(",\n");
            }
            if(!this.getUnbounds()){
                arrayType.append("'maxItems': ").append(this.getMaxOccors()).append(",\n");
            }
            arrayType.append("'items': {\n")
                    .append(getRefTypeSchema()).append("\n  }\n");

            if(ConvertConfig.ALLOW_SINGLE_OBJECT_IN_ARRAY  && this.getMinOccurs()<=1) {
                sb.append(retract(arrayType, 3)).append("    }\n  ]\n");
            }else{
                sb.append(retract(arrayType, 1));
            }
        }else {
            sb.append(getRefTypeSchema()).append("\n");
        }
        sb.append("}");
        return sb;
    }

    private StringBuilder getRefTypeSchema(){
        if(this.ref!=null){
            StringBuilder refTypeSchema = retract(ref.getRefName(this.getPrefix()), 1);
            if(ref instanceof SchemaAnyRefElement){
                refTypeSchema.insert(0,"'anyOf': [\n  {\n").append("\n  }\n]");
                return retract(refTypeSchema,1);
            }else{
                return refTypeSchema;
            }
        }else if(privilegeType!=null){
            return retract(privilegeType.convertSchema(),1);
        }else{
            throw new UnsupportedOperationException("None of type found in SchemaNamedRefElement");
        }
    }
}
