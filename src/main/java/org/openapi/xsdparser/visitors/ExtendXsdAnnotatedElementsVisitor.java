package org.openapi.xsdparser.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;

/**
 * Represents the restrictions of all the XSD elements that can have an {@link XsdAnnotation} as children.
 */
public class ExtendXsdAnnotatedElementsVisitor extends XsdAnnotatedElementsVisitor implements ExtendXsdAbstractElementVisitor {

    public ExtendXsdAnnotatedElementsVisitor(XsdAnnotatedElements owner){
        super(owner);
    }

    @Override
    public void visit(XsdAnnotation element) {
        super.visit(element);
    }
}
