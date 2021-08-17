package org.openapi.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.openapi.schemaelement.SchemaAbstractElement;
import org.openapi.schemaelement.SchemaTypeElement;
import org.openapi.visitor.DefaultSchemaVisitor;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Convertor {


    public static void main(String[] args) {
        String filePath = "D:\\tmp\\openapi3\\xsds\\MainCIHub.xsd";
        String outputFile = "D:\\MyProjects\\Xsd2Schema\\JSONSchemaRefAny\\";
        convertXSDToJsonSchema(filePath,outputFile);
    }

    public static Map<String, SchemaTypeElement> visitXSD(String filePath){
        XsdParser parserInstance = new XsdParser(filePath,new ExtendParserConfig());
        //Find the first root element.
        List<XsdElement> xsdElementList = parserInstance.getResultXsdElements().collect(Collectors.toList());
        if(xsdElementList.size()==0){
            throw new UnsupportedOperationException("You must have a xs:element as root element in the schema file");
        }
        XsdElement xsdElement = xsdElementList.get(0);
        DefaultSchemaVisitor schemaVisitor = new DefaultSchemaVisitor(xsdElement);
        xsdElement.accept(schemaVisitor);
        return schemaVisitor.getSchemaInstances();
    }

    public static void convertXSDToJsonSchema(String filePath, String outputFile ) {
        Map<String, SchemaTypeElement> schemaInstances = visitXSD(filePath);

        if (ConvertConfig.SEPARATED_FILE) {
            Map<String, ArrayList<SchemaTypeElement>> groupedElements = schemaInstances.values().stream().collect(Collectors.groupingBy(SchemaTypeElement::getPrefix, Collector.of(ArrayList<SchemaTypeElement>::new, ArrayList::add, (left, right) -> {
                left.addAll(right);
                return left;
            })));

            groupedElements.forEach((prefix, list) -> {
                String jsonSchema = outputJsonStr(list);
                if(prefix==""){
                    prefix="root";
                }
                writeToFile(outputFile, prefix, jsonSchema);
            });
        }else{
            String jsonSchema =  outputJsonStr(schemaInstances.values());
            writeToFile(outputFile, new File(filePath).getName().replace(".xsd",""), jsonSchema);
        }
    }

    public static String outputJsonStr(Collection<SchemaTypeElement> elements) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"components\": {\n");
        sb.append("    \"schemas\": {\n ");
        StringBuilder definitions = new StringBuilder();
        elements.forEach(value -> {
                definitions.append(value.convertSchema());
                definitions.append(",\n");
        });
        sb.append(SchemaAbstractElement.retract(definitions, 3));
        sb.append("    }\n  }\n}");
        return sb.toString().replaceAll("'", "\"");
    }

    public static void writeToFile(String folderPath, String prefix, String jsonSchema) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File outputFIle = new File(folder.getAbsolutePath() + File.separator + prefix + ".json");
        File outputYAML = new File(folder.getAbsolutePath() + File.separator + prefix + ".yaml");
        try {
            if (!outputFIle.exists()) {
                outputFIle.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(outputFIle, false);
            fos.write(jsonSchema.getBytes(StandardCharsets.UTF_8));
            fos.close();
            FileOutputStream fos1 = new FileOutputStream(outputYAML, false);
            fos1.write((asYaml(jsonSchema)).getBytes(StandardCharsets.UTF_8));
            fos1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String asYaml(String jsonString) throws  IOException {
        // parse JSON
        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
        // save it as YAML
        return new YAMLMapper().writeValueAsString(jsonNodeTree);
    }
}
