package org.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import org.openapi.core.ConvertConfig;
import org.openapi.core.Convertor;
import org.openapi.schemaelement.SchemaTypeElement;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;
import org.openapi4j.schema.validator.v3.SchemaValidator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestConvert {

    private static String XSD_PATH="/xsd/%s.xsd";
    private static String DATA_PATH="/data/%s.json";
    //private static String YAML_PATH="/yaml/%s.json";

    @Test
    public void generateYAMLFile(){
        String filePath = getURL("/xsd").getPath();
        String outputFile = getURL("/yaml").getPath();
        List<String> excludeFileName = Arrays.asList("MultiTypeSupport.xsd","SingleObjectInArraySupport.xsd","Embedded.xsd");
        File folder = new File(filePath);
        for(File file :folder.listFiles()) {
            if(file.isFile() && !excludeFileName.contains(file.getName())) {
                Convertor.convertXSDToJsonSchema(file.getAbsolutePath(), outputFile,null);
            }
        }

        //Generate schema for test multi_type_support
        filePath = getURL("/xsd/MultiTypeSupport.xsd").getPath();
        Convertor.convertXSDToJsonSchema(filePath, outputFile,"MultiTypeSupport_Default");
        ConvertConfig.MULTI_TYPE_SUPPORT = ConvertConfig.MULTITYPE_OPTION.FORCE_TO_STRING;
        Convertor.convertXSDToJsonSchema(filePath, outputFile,"MultiTypeSupport_String");
        ConvertConfig.MULTI_TYPE_SUPPORT = ConvertConfig.MULTITYPE_OPTION.FORCE_TO_NUMBER;
        Convertor.convertXSDToJsonSchema(filePath, outputFile,"MultiTypeSupport_Num");
        ConvertConfig.MULTI_TYPE_SUPPORT = ConvertConfig.MULTITYPE_OPTION.BOTH;
        Convertor.convertXSDToJsonSchema(filePath, outputFile,"MultiTypeSupport_Both");
        resetConfig();

        //Generate schema for test config_allow_single_object_in_array
        filePath = getURL("/xsd/SingleObjectInArraySupport.xsd").getPath();
        Convertor.convertXSDToJsonSchema(filePath, outputFile,"SingleObjectInArraySupport_false");
        ConvertConfig.ALLOW_SINGLE_OBJECT_IN_ARRAY = true;
        Convertor.convertXSDToJsonSchema(filePath, outputFile,"SingleObjectInArraySupport_true");
        resetConfig();


        //Generate schema for test config_choice_ref_required
        filePath = getURL("/xsd/ChoiceBasicType.xsd").getPath();
        ConvertConfig.EVERY_CHOICE_REF_REQUIRED = true;
        Convertor.convertXSDToJsonSchema(filePath, outputFile,"ChoiceBasicType_true");
        resetConfig();

        //Generate schema for test config_ref_anytype
        filePath = getURL("/xsd/AnyType.xsd").getPath();
        ConvertConfig.REF_ANYTYPE = ConvertConfig.ANYTYPE_OPTION.REFERENCE;
        Convertor.convertXSDToJsonSchema(filePath, outputFile,"AnyType_reference");
        ConvertConfig.REF_ANYTYPE = ConvertConfig.ANYTYPE_OPTION.COMPLICATED;
        Convertor.convertXSDToJsonSchema(filePath, outputFile,"AnyType_complicated");
        resetConfig();
    }

    @Test
    public void testProvilegeType() throws ResolutionException, IOException {
        validateSchmea("PrivilegeType");
        resetConfig();
    }

    @Test
    public void testBasicType() throws ResolutionException, IOException {
        validateSchmea("ChoiceBasicType");
        resetConfig();
    }

    @Test
    public void testSingleObjectInArraySupport() throws ResolutionException, IOException {
        validateSchmea("SingleObjectInArraySupport_false");
        ConvertConfig.ALLOW_SINGLE_OBJECT_IN_ARRAY = true;
        validateSchmea("SingleObjectInArraySupport_true");
        resetConfig();
    }

    @Test
    public void testMultiTypeSupport() throws ResolutionException, IOException {
        validateSchmea("MultiTypeSupport_Default");
        ConvertConfig.MULTI_TYPE_SUPPORT = ConvertConfig.MULTITYPE_OPTION.BOTH;
        validateSchmea("MultiTypeSupport_Both");
        ConvertConfig.MULTI_TYPE_SUPPORT = ConvertConfig.MULTITYPE_OPTION.FORCE_TO_NUMBER;
        validateSchmea("MultiTypeSupport_Num");
        ConvertConfig.MULTI_TYPE_SUPPORT = ConvertConfig.MULTITYPE_OPTION.FORCE_TO_STRING;
        validateSchmea("MultiTypeSupport_String");
        resetConfig();
    }

    @Test
    public void testAnyType() throws ResolutionException, IOException {
        validateSchmea("AnyType");
        ConvertConfig.REF_ANYTYPE = ConvertConfig.ANYTYPE_OPTION.COMPLICATED;
        validateSchmea("AnyType_complicated");
        resetConfig();
    }

    @Test
    public void testImportAndEmbedded() throws ResolutionException, IOException {
        validateSchmea("ImportAndEmbedded");
        resetConfig();
    }

    public void validateSchmea(String fileName) throws IOException, ResolutionException {
        String xsdPath = "";
        if(fileName.indexOf('_')!=-1){
            xsdPath =  getURL(String.format(XSD_PATH, fileName.substring(0,fileName.indexOf('_')))).getPath();
        }else {
            xsdPath = getURL(String.format(XSD_PATH, fileName)).getPath();
        }
        Map<String, SchemaTypeElement> schemaInstances = Convertor.visitXSD(xsdPath);
        String jsonSchema =  Convertor.outputJsonStr(schemaInstances.values());

        ObjectNode schemaNode = (ObjectNode) TreeUtil.json.readTree(jsonSchema);
        JsonNode rootSchema =TreeUtil.json.createArrayNode().add( schemaNode.at("/components/schemas/RootMsgSchema"));
        schemaNode.set("allOf",  rootSchema);
        ValidationContext validationContext = new ValidationContext(new OAI3Context(new URL("file:/"), schemaNode));
        validationContext.addValidator("properties", MyNullablePropertyValidator::new);
        SchemaValidator schemaValidator = new SchemaValidator(validationContext,null, schemaNode);
        ArrayNode testNodes = (ArrayNode) TreeUtil.json.readTree(getURL(String.format(DATA_PATH,fileName)));
        doTest(testNodes,schemaValidator,fileName);
    }

    public void doTest(ArrayNode testNodes,SchemaValidator schemaValidator,String testDescription){
        for (int i = 0; i < testNodes.size(); i++) {
            JsonNode test = testNodes.get(i);
            JsonNode data = test.get("data");
            boolean isValidExpected = test.get("valid").asBoolean();
            ValidationData<Void> validation = new ValidationData<>();
            schemaValidator.validate(data, validation);

            if (isValidExpected != validation.isValid()) {
                String message = String.format(
                        "TEST FAILURE : %s - %s\nData : %s",
                        testDescription,
                        test.get("description"),
                        data);
                System.out.println(message);
                System.out.println(validation.results().toString());
            }
            //else{
            //System.out.println(validation.results().toString());
            //}

            if (isValidExpected != validation.results().isValid()) {
                Assert.fail();
            }
        }
    }
    
    private URL getURL(String relativePath){
        return TestConvert.class.getResource(relativePath);
    }

    private void resetConfig(){
        ConvertConfig.ALLOW_SINGLE_OBJECT_IN_ARRAY = false;
        ConvertConfig.REF_ANYTYPE = ConvertConfig.ANYTYPE_OPTION.SIMPLIFY;
        ConvertConfig.EVERY_CHOICE_REF_REQUIRED = false;
        ConvertConfig.MULTI_TYPE_SUPPORT = ConvertConfig.MULTITYPE_OPTION.DEFAULT;
    }
}
