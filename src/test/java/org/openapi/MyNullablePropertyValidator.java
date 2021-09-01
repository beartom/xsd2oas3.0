package org.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults.CrumbInfo;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;
import org.openapi4j.schema.validator.v3.SchemaValidator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Properties under 'oneOf' not allow all properties not present
 */
public class MyNullablePropertyValidator extends BaseJsonValidator<OAI3> {
    private static final ValidationResult ERR;
    private static final CrumbInfo CRUMB_INFO;
    private final boolean nullable;

    private final List<String> propertyNames = new ArrayList<>();

    public MyNullablePropertyValidator(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
        super(context, schemaNode, schemaParentNode, parentSchema);
        Iterator it = schemaNode.fieldNames();
        while(it.hasNext()) {
            String pname = (String)it.next();
            propertyNames.add(pname);
        }
        this.nullable = isNullable(context,schemaNode,schemaParentNode,parentSchema);
    }

    public boolean validate(JsonNode valueNode, ValidationData<?> validation) {
         List<String> exsitingProperty = new ArrayList<>();
         this.propertyNames.forEach( pName->{
             JsonNode propertyNode = valueNode.get(pName);
             if(propertyNode!=null){
                 exsitingProperty.add(pName);
             }
         });
        if (exsitingProperty.isEmpty() && !this.nullable) {
            validation.add(CRUMB_INFO, ERR, new Object[0]);
        }
        return true;
    }

    private boolean isNullable(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema){
        return !parentSchema.getParentSchemaNode().has("oneOf");
    }

    static {
        ERR = new ValidationResult(ValidationSeverity.ERROR, 1021, "Null value is not allowed.");
        CRUMB_INFO = new CrumbInfo("properties", true);
    }
}
