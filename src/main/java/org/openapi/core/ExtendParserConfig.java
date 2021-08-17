package org.openapi.core;

import org.openapi.xsdparser.XsdAny;
import org.openapi.xsdparser.visitors.ExtendXsdAnnotatedElementsVisitor;
import org.openapi.xsdparser.visitors.ExtendXsdSequenceVisitor;
import org.xmlet.xsdparser.core.utils.ConfigEntryData;
import org.xmlet.xsdparser.core.utils.DefaultParserConfig;
import org.xmlet.xsdparser.xsdelements.XsdSequence;

import java.util.Map;

public class ExtendParserConfig extends DefaultParserConfig {
    @Override
    public Map<String, String> getXsdTypesToJava() {
        return super.getXsdTypesToJava();
    }
    @Override
    public Map<String, ConfigEntryData> getParseMappers() {
        Map<String, ConfigEntryData> map = super.getParseMappers();
        map.put(XsdSequence.XSD_TAG, new ConfigEntryData(XsdSequence::parse, elem -> new ExtendXsdSequenceVisitor((XsdSequence) elem)));
        map.put(XsdSequence.XS_TAG, new ConfigEntryData(XsdSequence::parse, elem -> new ExtendXsdSequenceVisitor((XsdSequence) elem)));
        map.put(XsdAny.XSD_TAG,new ConfigEntryData(XsdAny::parse, elem -> new ExtendXsdAnnotatedElementsVisitor((XsdAny) elem)));
        map.put(XsdAny.XS_TAG,new ConfigEntryData(XsdAny::parse, elem -> new ExtendXsdAnnotatedElementsVisitor((XsdAny) elem)));
        return map;
    }
}
