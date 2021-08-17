package org.openapi.schemaelement;

public class SchemaComplexElement extends SchemaTypeElement {

    public SchemaComplexElement(String prefix, String name) {
        super(prefix,name);
    }

    @Override
    public void addSubElement(SchemaAbstractElement element) {
        if(!element.isContainer()){
            throw new UnsupportedOperationException("Suppose to be a Container.");
        }
        super.addSubElement(element);
    }

    /**
     *  Should be top level object schema instance
     */
    @Override
    public StringBuilder convertSchema() {
        int subElementSize= this.getSubElements().size();
        if(subElementSize<1){
            throw new UnsupportedOperationException("Suppose to have a Container.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(getFullName()).append("': \n  ");

        if(subElementSize>1){
            sb.append("{\n");
            sb.append("  'allOf':[\n");
        }

        this.getSubElements().forEach(subElement->{
            sb.append(retract(subElement.convertSchema(),1)).append(",\n");
        });
        //Remove delimiter at theend
        sb.setLength(sb.length()-2);

        if(subElementSize>1){
            sb.append("  ]\n");
            sb.append("\n}");
        }

        return sb;
    }
}
