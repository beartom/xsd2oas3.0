package org.openapi.schemaelement;

import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdEnumeration;

import java.util.stream.Collectors;

public abstract class SchemaPrivilegeElement extends SchemaAbstractElement {

    public SchemaPrivilegeElement(String prefix) {
        super(prefix);
    }

    public static SchemaPrivilegeElement generateFromXsdElement(String prefix, XsdRestriction xsdRestriction){
        SchemaPrivilegeElement privilegeElement = generateFromXsdElement(prefix,xsdRestriction.getBase());
        if(privilegeElement instanceof SchemaStringElement){
            if(xsdRestriction.getEnumeration().size()>0) {
                ((SchemaStringElement) privilegeElement).setEnumeration(xsdRestriction.getEnumeration().stream().map(XsdEnumeration::getValue).collect(Collectors.toList()));
            }else if(xsdRestriction.getPattern()!=null){
                ((SchemaStringElement) privilegeElement).setPattern(xsdRestriction.getPattern().getValue());
            }else if(xsdRestriction.getLength()!=null){
                ((SchemaStringElement) privilegeElement).setMinLength(xsdRestriction.getLength().getValue());
                ((SchemaStringElement) privilegeElement).setMaxLength(xsdRestriction.getLength().getValue());
            }else{
                if(xsdRestriction.getMinLength()!=null){
                    ((SchemaStringElement) privilegeElement).setMinLength(xsdRestriction.getMinLength().getValue());
                }

                if(xsdRestriction.getMaxLength()!=null){
                    ((SchemaStringElement) privilegeElement).setMaxLength(xsdRestriction.getMaxLength().getValue());
                }
            }
        }else if(privilegeElement instanceof SchemaNumberElement){
            if(xsdRestriction.getMaxExclusive()!=null){
                ((SchemaNumberElement) privilegeElement).setMaximum(xsdRestriction.getMaxExclusive().getValue());
                ((SchemaNumberElement) privilegeElement).setExclusiveMaximum(true);
            }else if(xsdRestriction.getMaxInclusive()!=null){
                ((SchemaNumberElement) privilegeElement).setMaximum(xsdRestriction.getMaxExclusive().getValue());
            }

            if(xsdRestriction.getMinExclusive()!=null){
                ((SchemaNumberElement) privilegeElement).setMinimum(xsdRestriction.getMinExclusive().getValue());
                ((SchemaNumberElement) privilegeElement).setExclusiveMinimum(true);
            }else if(xsdRestriction.getMaxInclusive()!=null){
                ((SchemaNumberElement) privilegeElement).setMinimum(xsdRestriction.getMinExclusive().getValue());
            }

            if(xsdRestriction.getFractionDigits()!=null){
                ((SchemaNumberElement) privilegeElement).setFractionDigits(xsdRestriction.getFractionDigits().getValue());
            }
            if(xsdRestriction.getTotalDigits()!=null){
                ((SchemaNumberElement) privilegeElement).setTotalDigits(xsdRestriction.getTotalDigits().getValue());
            }
        }
        return privilegeElement;
    }

    @Override
    public boolean isContainer() {
        return true;
    }


    public static SchemaPrivilegeElement generateFromXsdElement(String prefix, String baseType){
        String type = baseType;
        if(baseType.indexOf(':')!=-1){
            type = baseType.substring(baseType.indexOf(':')+1);
        }

        SchemaPrivilegeElement privilegeElement = null;
        switch (type){
            case "any":
                privilegeElement = new SchemaAnyElement(prefix) ;
                break;
            case "ENTITIES":
            case "ENTITY":
            case "ID":
            case "IDREF":
            case "IDREFS":
            case "Name":
            case "NCName":
            case "NMTOKEN":
            case "NMTOKENS":
            case "normalizedString":
            case "QName":
            case "string":
            case "token":
                privilegeElement = new SchemaStringElement(prefix) ;
                break;
            case "time":
                privilegeElement = new SchemaStringElement(prefix);
                ((SchemaStringElement)privilegeElement).setPattern("([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\\.[0-9]{1,3})?(Z|[+]((0[0-9]|1[0-4]):00)|[-]((0[0-9]|1[0-2]):00))?");
                break;
            case "date":
                privilegeElement = new SchemaStringElement(prefix);
                ((SchemaStringElement)privilegeElement).setFormat("date");
                break;
            case "dateTime":
                privilegeElement = new SchemaStringElement(prefix);
                ((SchemaStringElement)privilegeElement).setFormat("date-time");
                break;
            case "base64Binary":
                privilegeElement = new SchemaStringElement(prefix);
                ((SchemaStringElement)privilegeElement).setFormat("byte");
                break;
            case "hexBinary":
                privilegeElement = new SchemaStringElement(prefix);
                ((SchemaStringElement)privilegeElement).setFormat("binary");
                break;
            case "boolean":
                privilegeElement = new SchemaBooleanElement(prefix);
                break;
            case "byte":
            case "decimal":
            case "int":
            case "integer":
            case "long":
            case "negativeInteger":
            case "nonNegativeInteger":
            case "nonPositiveInteger":
            case "positiveInteger":
            case "short":
            case "unsignedLong":
            case "unsignedInt":
            case "unsignedShort":
            case "unsignedByte":
                privilegeElement = new SchemaNumberElement(prefix);
                break;
            case "duration":
            case "anyURI":
            case "gDay":
            case "gMonth":
            case "gMonthDay":
            case "gYear":
            case "gYearMonth":
            default:
                throw new UnsupportedOperationException("Unsupported xsd type "+baseType+" yet");
        }
        return privilegeElement;
    }
}
