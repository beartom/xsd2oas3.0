package org.openapi.xsdparser.visitors;

import org.openapi.xsdparser.XsdAny;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdSequenceVisitor;

/**
 * Represents the restrictions of the {@link XsdSequence} element, which can contain {@link XsdElement}, {@link XsdGroup},
 * {@link XsdChoice} or {@link XsdSequence} as children. Can also have {@link XsdAnnotation} as children as per
 * inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class ExtendXsdSequenceVisitor extends XsdSequenceVisitor implements ExtendXsdAbstractElementVisitor  {

    public ExtendXsdSequenceVisitor(XsdSequence owner) {
        super(owner);
    }
    @Override
    public void visit(XsdAny element){
        getOwner().addElement(element);
    }
}