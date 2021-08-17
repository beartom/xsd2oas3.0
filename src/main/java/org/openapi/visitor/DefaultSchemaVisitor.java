package org.openapi.visitor;

import org.openapi.core.ConvertConfig;
import org.openapi.schemaelement.*;
import org.openapi.xsdparser.XsdAny;
import org.xmlet.xsdparser.xsdelements.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultSchemaVisitor extends BaseSchemaVisitor{

    SchemaAbstractElement parent;
    Map<String, SchemaTypeElement> schemaInstances = new LinkedHashMap<>();;
    String prefix="tns";
    SchemaNamedRefElement root;
    XsdAbstractElement rootXsdElement;

    public DefaultSchemaVisitor(XsdElement rootElement) {
        SchemaNamedRefElement namedRefElement = new SchemaNamedRefElement("","RootMsg");
        SchemaComplexElement complexElement = new SchemaComplexElement("","RootMsgSchema");
        SchemaSequenceElement sequenceElement = new SchemaSequenceElement("");
        this.schemaInstances.put(complexElement.getFullName(),complexElement);
        this.rootXsdElement = rootElement;
        namedRefElement.addSubElement(complexElement);
        complexElement.addSubElement(sequenceElement);
        this.parent = sequenceElement;
        this.root = namedRefElement;
    }

    public Map<String, SchemaTypeElement> getSchemaInstances() {
        return schemaInstances;
    }

    public SchemaNamedRefElement getRoot() {
        return root;
    }

    @Override
    public void visit(XsdAny element) {
        if(ConvertConfig.REF_ANYTYPE == ConvertConfig.ANYTYPE_OPTION.REFERENCE){
            SchemaAnyRefElement anyTypeRef = new SchemaAnyRefElement(prefix, "AnyObject");
            parent.addSubElement(anyTypeRef);
            schemaInstances.put(anyTypeRef.getFullName(), anyTypeRef);
        }else {
            parent.addSubElement(new SchemaAnyElement(prefix));
        }
    }

    @Override
    public void visit(XsdAttribute element) {
        SchemaNamedRefElement namedRefElement = new SchemaNamedRefElement(prefix,element.getName());
        SchemaAbstractElement oldParent = setNewParent(namedRefElement);
        super.visit(element);
        this.parent = oldParent;
    }

    @Override
    public void visit(XsdAttributeGroup element) {
        throw new UnsupportedOperationException("Unsupported parse XsdAttributeGroup yet");
    }

    @Override
    public void visit(XsdChoice element) {
        SchemaChoiceElement choiceElement = new SchemaChoiceElement(prefix);
        SchemaAbstractElement oldParent = setNewParent(choiceElement);
        super.visit(element);
        this.parent = oldParent;
    }

    @Override
    public void visit(XsdComplexType element) {
        SchemaComplexElement complexElement = new SchemaComplexElement(prefix,element.getName());
        this.schemaInstances.put(complexElement.getFullName(),complexElement);
        SchemaAbstractElement oldParent = setNewParent(complexElement);
        super.visit(element);
        this.parent = oldParent;
    }

    @Override
    public void visit(XsdElement element) {

        SchemaNamedRefElement namedRefElement = new SchemaNamedRefElement(element==this.rootXsdElement?"":prefix,element.getName());
        setOccurs(element,namedRefElement);
        SchemaAbstractElement oldParent = setNewParent(namedRefElement);
        String typeFullName = element.getAttributesMap().get("type");
        String prefixTmp = prefix;
        if(typeFullName!=null && typeFullName.indexOf(':')!=-1){
            prefix = typeFullName.substring(0,typeFullName.indexOf(':'));
        }
        //Hanlde privilege
        if( element.getXsdComplexType()==null&&element.getXsdSimpleType()==null){
            namedRefElement.addSubElement(SchemaPrivilegeElement.generateFromXsdElement(prefix,element.getType()));
        }else {
            super.visit(element);
        }
        this.parent = oldParent;
        this.prefix = prefixTmp;
    }

    @Override
    public void visit(XsdSequence element) {
        SchemaSequenceElement sequenceElement = new SchemaSequenceElement(prefix);
        SchemaAbstractElement oldParent = setNewParent(sequenceElement);
        super.visit(element);
        this.parent = oldParent;
    }

    @Override
    public void visit(XsdSimpleType element) {
        SchemaSimpleTypeElement simpleTypeElement = new SchemaSimpleTypeElement(prefix, element.getName());
        this.schemaInstances.put(simpleTypeElement.getFullName(),simpleTypeElement);
        SchemaAbstractElement oldParent = setNewParent(simpleTypeElement);
        super.visit(element);
        this.parent = oldParent;
    }

    @Override
    public void visit(XsdRestriction element) {
        super.visit(element);
        parent.addSubElement(SchemaPrivilegeElement.generateFromXsdElement(prefix,element));
    }

    @Override
    public void visit(XsdExtension element) {
        SchemaExtensionElement extensionElement = new SchemaExtensionElement(prefix);
        SchemaAbstractElement oldParent = setNewParent(extensionElement);
        super.visit(element);
        this.parent = oldParent;
    }

    private SchemaAbstractElement setNewParent(SchemaAbstractElement newParent){
        SchemaAbstractElement parentTmp = this.parent;
        //Skip for the same container type.
        //Like xs:sequence
        //        xs:sequence
        if(newParent.isContainer()&&parentTmp.isContainer()&&parentTmp.getClass().equals(newParent.getClass())){
            return parentTmp;
        }
        parentTmp.addSubElement(newParent);
        this.parent = newParent;
        return parentTmp;
    }
    
    private void setOccurs(XsdElement xsdElement, SchemaAbstractElement element){
        element.setMinOccurs(xsdElement.getMinOccurs());
        if(!(xsdElement.getMaxOccurs()==null||xsdElement.getMaxOccurs().equals(""))){
            if(xsdElement.getMaxOccurs().equals("unbounded")){
                element.setUnbounds(true);
            }else{
                element.setMaxOccors(Integer.parseInt(xsdElement.getMaxOccurs()));
            }
        }
    }
}
