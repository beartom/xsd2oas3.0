# xsd2oas3
A tool to convert XSD to openAPI 3.0 schema with a lot of limitation.

The original intention is my project need to convert validation from xsd to OAS3. So I didn't implement the xsd element those not used in my project's xsd file... 

Support main xsd file import/include multiple sub xsd files. So it must distinguish the same type name in diff name space.

The Types defined in main xsd will use default prefix "**tns_**". The Types defined in sub xsd file will use the abbreviation name space in main xsd as prefix.

Code extend from [XsdParser](https://github.com/xmlet/XsdParser)

Test using [openapi4j](https://github.com/openapi4j/openapi4j)

## **Limitation**


* xs:group, xs:attributeGroup, xs:all, xs:list, xs:union, xs:complexContent are **unsupported**.


* xs:annotation, xs:appInfo, xs:documentation, xs:field, xs:key, xs:keyref, xs:notation, xs:redefine, xs:selector, xs:unique, xs:anyAttribute are **ignored**


* xs:restriction only support for base privilege type.
  

* xs:extension only tested inside xs:simpleContent for additional attribute. All the attributes will be added as properties of Object Type. And the original simpleType will be a 'Value' property of Object Type.


* xs:attribute only support inside xs:extension


* Others I was not realized......

## **Usage**

```java -jar ./Xsd2oas3.jar -s ./ChoiceBasicType.xsd -t ./ -config_multi_type_support=false```

## **Operations**

### **-s**
Mandatory. Specify the source xsd file path.
### **-t**
Default value: **./**

Specify the target folder path.

### **-f**
Default value: _\<The same name as input xsd file\>_

Specify the target file name without suffix.

### **-config_multi_type_support**
Default value: **DEFAULT**

Multiple type support for XSD Number type.

Number type in XSD with fractionDigits/totalDigits Restriction must be converted to a String type with pattern validation in OAS schema.

Number type in XSD with Range Restriction should be converted to Number. Pattern is hard to clarify the range.

OAS schema can also support String and Number type without validation.

###### -config_multi_type_support=DEFAULT

* Convert to String with pattern validation for Number type in XSD with fractionDigits/totalDigits Restriction.(Ignore Range Restriction)
* Convert to Number for Number type if no fractionDigits/totalDigits Restriction.

###### 　　e.g. [MultiTypeSupport.xsd](src/test/resources/xsd/MultiTypeSupport.xsd) to [MultiTypeSupport_Default.yaml](src/test/resources/yaml/MultiTypeSupport_Default.yaml)


###### -config_multi_type_support=FORCE_TO_NUMBER
* Convert to String with pattern validation for Number type in XSD with fractionDigits/totalDigits Restriction. (Ignore Range Restriction)

###### 　　e.g. [MultiTypeSupport.xsd](src/test/resources/xsd/MultiTypeSupport.xsd) to [MultiTypeSupport_String.yaml](src/test/resources/yaml/MultiTypeSupport_String.yaml)


###### -config_multi_type_support=FORCE_TO_NUMBER
* Convert to Number with range validation for Number type in XSD with Range Restriction. (Ignore fractionDigits/totalDigits Restriction)

###### 　　e.g. [MultiTypeSupport.xsd](src/test/resources/xsd/MultiTypeSupport.xsd) to [MultiTypeSupport_Num.yaml](src/test/resources/yaml/MultiTypeSupport_Num.yaml)


###### -config_multi_type_support=BOTH
* Convert to both Number and String.
* Number type with range validation for Number type in XSD with Range Restriction. (Ignore fractionDigits/totalDigits Restriction)
* String type with pattern validation for Number type in XSD with fractionDigits/totalDigits Restriction. (Ignore Range Restriction)

###### 　　e.g. [MultiTypeSupport.xsd](src/test/resources/xsd/MultiTypeSupport.xsd) to [MultiTypeSupport_Both.yaml](src/test/resources/yaml/MultiTypeSupport_Both.yaml)

### **-config_ref_prefix**
Default value: **#/components/schemas/**

Specify the prefix of a reference type.

### **-config_allow_single_object_in_array**
Default value: **false**

Allow single element in array when MinOccurs<=1 and MaxOccurs>1.

######e.g. [SingleObjectInArraySupport.xsd](src/test/resources/xsd/SingleObjectInArraySupport.xsd) to [SingleObjectInArraySupport_true.yaml](src/test/resources/yaml/SingleObjectInArraySupport_true.yaml) or [SingleObjectInArraySupport_false.yaml](src/test/resources/yaml/SingleObjectInArraySupport_false.yaml)

### **-config_choice_ref_required**
Default value: **false**

Keyword 'required' for every element in choice when the minOccurs of element gather than 0.

######e.g. [ChoiceBasicType.xsd](src/test/resources/xsd/ChoiceBasicType.xsd) to [SingleObjectInArraySupport_true.yaml](src/test/resources/yaml/SingleObjectInArraySupport_true.yaml) or [SingleObjectInArraySupport.yaml](src/test/resources/yaml/ChoiceBasicType.yaml)

### **-config_ref_anytype**
Default value: **SIMPLIFY**

###### -config_ref_anytype=SIMPLIFY
```yaml
tns_AnyType: {}
```

###### -config_ref_anytype=COMPLICATED
```yaml
tns_AnyType:
  anyOf:
    - type: "string"
    - type: "number"
    - type: "integer"
    - type: "boolean"
    - type: "array"
      items: {}
    - type: "object"
```