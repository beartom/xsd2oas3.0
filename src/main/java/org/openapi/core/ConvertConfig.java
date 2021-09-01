package org.openapi.core;

public class ConvertConfig {

    public static boolean SEPARATED_FILE =false;

    /**
     * To support more than one type for Number.
     * A Number type in XSD with fractionDigits/totalDigits Restriction must be convert to a String type in JSON or YAML.
     * But JSON and YAML can also support Number type without validation.
     * If MULTI_TYPE_SUPPORT=false,
     * 1.Convert to String with pattern validation for Number type in XSD with fractionDigits/totalDigits Restriction.
     * 2.Convert to Number for Number type without fractionDigits/totalDigits Restriction.
     * Otherwise
     * 1.Convert to both String with pattern validation and Number without validation for Number type in XSD with fractionDigits/totalDigits Restriction.
     * 2.Convert to both String with incomplete pattern validation  and Number with validation for Number type in XSD without fractionDigits/totalDigits Restriction.
     */
    public static MULTITYPE_OPTION MULTI_TYPE_SUPPORT=MULTITYPE_OPTION.DEFAULT;

    public static String REF_PREFIX="#/components/schemas/";

    public static boolean ALLOW_SINGLE_OBJECT_IN_ARRAY = false;

    public static boolean EVERY_CHOICE_REF_REQUIRED = false;

    public static ANYTYPE_OPTION REF_ANYTYPE = ANYTYPE_OPTION.SIMPLIFY;

    public enum MULTITYPE_OPTION{
        DEFAULT,
        FORCE_TO_NUMBER,
        FORCE_TO_STRING,
        BOTH
    }

    public enum ANYTYPE_OPTION {
        REFERENCE,
        SIMPLIFY,
        COMPLICATED,
    }
}
