package org.openapi.xsdparser.visitors;

import org.openapi.xsdparser.XsdAny;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

public interface ExtendXsdAbstractElementVisitor extends XsdAbstractElementVisitor {

    default  void visit(XsdAny element){}
}
