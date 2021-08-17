package org.openapi.schemaelement;

public class SchemaSimpleTypeElement extends SchemaTypeElement {

    public SchemaSimpleTypeElement(String prefix, String name) {
        super(prefix, name);
    }

    private SchemaPrivilegeElement ref;


    public SchemaPrivilegeElement getRef() {
        return ref;
    }

    /**
     *  Should be top level schema instance
     */
    @Override
    public StringBuilder convertSchema() {
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(getFullName()).append("'").append(": {\n");
        sb.append(retract(this.ref.convertSchema(),1));
        sb.append("\n}");
        return sb;
    }

    @Override
    public void addSubElement(SchemaAbstractElement element) {
        if(this.ref!=null){
            throw new UnsupportedOperationException("Unsupported ref more than one element");
        }
        if(element instanceof SchemaPrivilegeElement){
            this.ref = (SchemaPrivilegeElement)element;
        }else{
            throw new UnsupportedOperationException("Unsupported ref type " +element.getClass().getName());
        }
    }
}
