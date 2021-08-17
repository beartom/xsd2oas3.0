package org.openapi.visitor;

import org.openapi.xsdparser.XsdAny;
import org.openapi.xsdparser.visitors.ExtendXsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.visitors.XsdExtensionVisitor;

import java.util.List;

/**
 * A basic visitor traverse all supported elements
 */
public class BaseSchemaVisitor implements ExtendXsdAbstractElementVisitor {


    @Override
    public void visit(XsdAny element) {

    }

    @Override
    public XsdAbstractElement getOwner() {
        return null;
    }

    @Override
    public void visit(XsdAll element) {
        throw new UnsupportedOperationException("Unsupported parse XsdAll yet");
    }

    @Override
    public void visit(XsdAttribute element) {
        element.getXsdSimpleType().accept(this);
    }

    @Override
    public void visit(XsdAttributeGroup element) {
        throw new UnsupportedOperationException("Unsupported parse XsdAttributeGroup yet");
    }

    @Override
    public void visit(XsdChoice element) {
        element.getElements().forEach( referenceBase -> referenceBase.getElement().accept(this));
    }

    @Override
    public void visit(XsdComplexType element) {
        XsdAbstractElement xsdChildElement = element.getXsdChildElement();
        if(xsdChildElement!=null){
            xsdChildElement.accept(this);
        }
        XsdSimpleContent simpleContent = element.getSimpleContent();
        if(simpleContent!=null){
            simpleContent.accept(this);
        }
        XsdComplexContent complexContent = element.getComplexContent();
        if(complexContent!=null){
            complexContent.accept(this);
        }
    }

    @Override
    public void visit(XsdElement element) {
        XsdComplexType xsdComplexType = element.getXsdComplexType();
        XsdSimpleType xsdSimpleType = element.getXsdSimpleType();
        if(xsdComplexType!=null){
            xsdComplexType.accept(this);
        }else if(xsdSimpleType!=null){
            xsdSimpleType.accept(this);
        }
    }

    @Override
    public void visit(XsdGroup element) {
        throw new UnsupportedOperationException("Unsupported parse XsdGroup yet");
    }

    @Override
    public void visit(XsdSequence element) {
        element.getElements().forEach(referenceBase -> referenceBase.getElement().accept(this));
    }

    @Override
    public void visit(XsdSimpleType element) {
        if(!element.getElements().isEmpty()){
            throw new UnsupportedOperationException("Unexpected Elements found for "+ element.getName());
        }
        List<XsdRestriction> allRestrictions = element.getAllRestrictions();
        if(allRestrictions.size()!=1){
            throw new UnsupportedOperationException("Unexpected Restrictions size"+ allRestrictions.size() +" found for "+ element.getName());
        }
        allRestrictions.get(0).accept(this);
    }

    @Override
    public void visit(XsdRestriction element) {
        
        if(element.getWhiteSpace()!=null){
            throw new UnsupportedOperationException("Unsupported type WhiteSpace in Restriction ");
        }
        if(element.getGroup()!=null){
            throw new UnsupportedOperationException("Unsupported type XsdGroup in Restriction ");
        }

        if(element.getAll()!=null){
            throw new UnsupportedOperationException("Unsupported type XsdAll in Restriction ");
        }

        if(element.getChoice()!=null){
            throw new UnsupportedOperationException("Unsupported type XsdChoice in Restriction ");
        }

        if(element.getSequence()!=null){
            throw new UnsupportedOperationException("Unsupported type XsdSequence in Restriction ");
        }

        if(element.getSimpleType()!=null){
            throw new UnsupportedOperationException("Unsupported type XsdSimpleType in Restriction ");
        }
        if(element.getBase()==null){
            throw new UnsupportedOperationException("None of base xsd type in Restriction");
        }
    }

    @Override
    public void visit(XsdList element) {
        throw new UnsupportedOperationException("Unsupported parse XsdList yet");

    }

    @Override
    public void visit(XsdUnion element) {
        throw new UnsupportedOperationException("Unsupported parse XsdUnion yet");
    }

    @Override
    public void visit(XsdExtension element) {
        XsdAbstractElement xsdChildElement = element.getXsdChildElement();
        if(xsdChildElement!=null){
            throw new UnsupportedOperationException("Unsupported ChildElement in Extension yet");
        }
        XsdNamedElements base = element.getBase();
        base.accept(this);
        ((XsdExtensionVisitor)element.getVisitor()).getAttributes().forEach(referenceAttr -> referenceAttr.getElement().accept(this));
    }

    @Override
    public void visit(XsdComplexContent element) {
        throw new UnsupportedOperationException("Unsupported parse XsdComplexContent yet");
    }

    @Override
    public void visit(XsdSimpleContent element) {
        
        XsdRestriction xsdRestriction = element.getXsdRestriction();
        XsdExtension xsdExtension = element.getXsdExtension();
        if(xsdRestriction!=null){
            xsdRestriction.accept(this);
        }
        if(xsdExtension!=null){
            xsdExtension.accept(this);
        }
    }
}
