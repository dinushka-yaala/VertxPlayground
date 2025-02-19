package com.yaalalabs.schema;

import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchema;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.OutputFormat;
import io.vertx.json.schema.SchemaRepository;
import io.vertx.json.schema.Validator;

import lombok.Getter;

/**
 * This util class is used JSON schema validation
 */
public class SchemaValidation {
    @Getter
    private static SchemaRepository schemaRepository = null;
    public static final String ENV_URI = "http://localhost:8080/";

    private SchemaValidation() {
    }

    public static void init() {
        schemaRepository = SchemaRepository.create(
            new JsonSchemaOptions()
                .setBaseUri(ENV_URI)
                .setDraft(Draft.DRAFT7)
                .setOutputFormat(OutputFormat.Basic)
        );
    }

    public static void addSchema(String schemaName, JsonObject schema) {
        schemaRepository.dereference(JsonSchema.of(schemaName, schema));
    }

    public static void addSchema(JsonSchema schema) {
        schemaRepository.dereference(schema);
    }

    public static Validator getValidator(String jsonPointer) {
        return schemaRepository.validator(ENV_URI + jsonPointer);
    }
}
