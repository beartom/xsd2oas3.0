package org.openapi.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.openapi.schemaelement.SchemaAbstractElement;
import org.openapi.schemaelement.SchemaTypeElement;
import org.openapi.visitor.DefaultSchemaVisitor;
import org.silentsoft.arguments.parser.Argument;
import org.silentsoft.arguments.parser.Arguments;
import org.silentsoft.arguments.parser.ArgumentsParser;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdSchema;

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

    public static void main(String[] args) throws Exception {

        String filePath;
        String outputFolder = "."+File.separator;
        String targetFileName = null;

        Arguments parse = ArgumentsParser.parse(args).with(ArgumentsParser.ParsingOptions.REMOVE_DASH_PREFIX,
                ArgumentsParser.ParsingOptions.CASE_INSENSITIVE);
        Argument sourceFile = parse.get("s");

        if(sourceFile == null){
            throw new Exception("A source xsd file path is mandatory. Use -s specify the path.");
        }

        filePath = sourceFile.getValue();

        Argument targetFolderPath = parse.get("t");

        if(targetFolderPath != null){
            outputFolder = targetFolderPath.getValue();
        }

        Argument fileName = parse.get("f");

        if(fileName != null){
            targetFileName = fileName.getValue();
        }

        setUpConfig(parse);
        //Do the convert.
        convertXSDToJsonSchema(filePath,outputFolder,targetFileName);
    }

    public static void setUpConfig(Arguments parse)  {
        //Set up convert configuration.
        if(parse.containsKey("config_multi_type_support")){
            ConvertConfig.MULTI_TYPE_SUPPORT = ConvertConfig.MULTITYPE_OPTION.valueOf(parse.get("config_multi_type_support").getValue());
        }

        if(parse.containsKey("config_ref_prefix")){
            ConvertConfig.REF_PREFIX = parse.get("config_ref_prefix").getValue();
        }
        if(parse.containsKey("config_allow_single_object_in_array")){
            ConvertConfig.ALLOW_SINGLE_OBJECT_IN_ARRAY =Boolean.parseBoolean(parse.get("config_allow_single_object_in_array").getValue());
        }

        if(parse.containsKey("config_choice_ref_required")){
            ConvertConfig.EVERY_CHOICE_REF_REQUIRED =Boolean.parseBoolean(parse.get("config_choice_ref_required").getValue());
        }

        if(parse.containsKey("config_ref_anytype")){
            ConvertConfig.REF_ANYTYPE = ConvertConfig.ANYTYPE_OPTION.valueOf(parse.get("config_ref_anytype").getValue());
        }

    }

    public static Map<String, SchemaTypeElement> visitXSD(String filePath){
        XsdParser parserInstance = new XsdParser(filePath,new ExtendParserConfig());
        //Find the first root element.
        List<XsdElement> xsdElementList = parserInstance.getResultXsdElements()
                .filter( xsdElement -> (xsdElement.getParent() instanceof  XsdSchema) &&
                        ((XsdSchema)(xsdElement.getParent())).getFilePath().contains(filePath))
                .collect(Collectors.toList());
        if(xsdElementList.size()==0){
            throw new UnsupportedOperationException("You must have a xs:element as root element in the schema file");
        }
        XsdElement xsdElement = xsdElementList.get(0);
        DefaultSchemaVisitor schemaVisitor = new DefaultSchemaVisitor(xsdElement);
        xsdElement.accept(schemaVisitor);
        return schemaVisitor.getSchemaInstances();
    }

    public static void convertXSDToJsonSchema(String filePath, String outputFolder,String targetFileName) {

        Map<String, SchemaTypeElement> schemaInstances = visitXSD(filePath);

        if(targetFileName==null){
            targetFileName = new File(filePath).getName().replace(".xsd","");
        }

        String fileName=targetFileName;

        if (ConvertConfig.SEPARATED_FILE) {
            Map<String, ArrayList<SchemaTypeElement>> groupedElements = schemaInstances.values().stream().collect(Collectors.groupingBy(SchemaTypeElement::getPrefix, Collector.of(ArrayList<SchemaTypeElement>::new, ArrayList::add, (left, right) -> {
                left.addAll(right);
                return left;
            })));

            groupedElements.forEach((prefix, list) -> {
                String jsonSchema = outputJsonStr(list);
                if(prefix.equals("")){
                    prefix=fileName;
                }
                writeToFile(outputFolder, prefix, jsonSchema);
            });
        }else{
            String jsonSchema =  outputJsonStr(schemaInstances.values());
            writeToFile(outputFolder, fileName, jsonSchema);
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
