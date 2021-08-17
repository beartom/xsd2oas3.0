package org.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
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
import java.util.Map;

public class TestConvert {

    private static String XSD_PATH="/xsd/%s.xsd";
    private static String DATA_PATH="/data/%s.json";
    //private static String YAML_PATH="/yaml/%s.json";

    @Test
    public void generateYAMLFile(){
        String filePath = "D:\\MyProjects\\Xsd2Schema\\src\\test\\resources\\xsd";
        String outputFile = "D:\\MyProjects\\Xsd2Schema\\src\\test\\resources\\yaml";
        //String filePath = TestConvert.class.getResource("/xsd").getPath();
        //String outputFile = TestConvert.class.getResource("/yaml").getPath();
        File folder = new File(filePath);
        for(File file :folder.listFiles()) {
            Convertor.convertXSDToJsonSchema(file.getAbsolutePath(), outputFile);
        }
    }

    @Test
    public void testProvilegeType() throws ResolutionException, IOException {
        validateSchmea("PrivilegeType");
    }

    @Test
    public void testBasicType() throws ResolutionException, IOException {
        validateSchmea("ChoiceBasicType");
    }


    public void validateSchmea(String fileName) throws IOException, ResolutionException {
        String xsdPath = TestConvert.class.getResource(String.format(XSD_PATH,fileName)).getPath();
        Map<String, SchemaTypeElement> schemaInstances = Convertor.visitXSD(xsdPath);
        String jsonSchema =  Convertor.outputJsonStr(schemaInstances.values());
        //jsonSchema.replaceAll("#/components/schemas/","#");
        //Compare jsonSchema md5
        ObjectNode schemaNode = (ObjectNode) TreeUtil.json.readTree(jsonSchema);
        JsonNode rootSchema =TreeUtil.json.createArrayNode().add( schemaNode.at("/components/schemas/RootMsgSchema"));
        schemaNode.set("allOf",  rootSchema);
        ValidationContext validationContext = new ValidationContext(new OAI3Context(new URL("file:/"), schemaNode));
        validationContext.addValidator("properties", MyNullablePropertyValidator::new);
        SchemaValidator schemaValidator = new SchemaValidator(validationContext,null, schemaNode);
        ArrayNode testNodes = (ArrayNode) TreeUtil.json.readTree(TestConvert.class.getResource(String.format(DATA_PATH,fileName)));
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
            }//else{
            //System.out.println(validation.results().toString());
            //}

            if (isValidExpected != validation.results().isValid()) {
                Assert.fail();
            }
        }
    }
}
