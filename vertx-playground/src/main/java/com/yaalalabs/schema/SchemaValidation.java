package com.yaalalabs.schema;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchema;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.SchemaRepository;
import io.vertx.json.schema.Validator;
import io.vertx.json.schema.draft7.dsl.StringFormat;

import static io.vertx.json.schema.common.dsl.Schemas.*;
import static io.vertx.json.schema.draft7.dsl.Keywords.*;

import java.net.URI;

import lombok.Getter;

/**
 * This util class is used JSON schema validation
 */
public class SchemaValidation {
    @Getter
    private static SchemaRepository schemaRepository = null;
    private static final String ENV_URI = "http://localhost:8080/";

    private SchemaValidation() {
    }

    public static void init() {
        schemaRepository = SchemaRepository.create(new JsonSchemaOptions().setBaseUri(ENV_URI).setDraft(Draft.DRAFT7));

        addSchema(getSchema("schema1"));
        addSchema(getSchema("schema2"));
    }

    public static void addSchema(String schemaName, JsonObject schema) {
        schemaRepository.dereference(JsonSchema.of(schemaName, schema));
    }

    public static void addSchema(JsonSchema schema) {
        schemaRepository.dereference(schema);
    }

    public static Validator getValidator(String jsonPointer) {
        return schemaRepository.validator(jsonPointer);
    }

    public static JsonSchema getSchema(String schemaName) {
        JsonObject Schema = null;

        switch (schemaName) {
            case "schema1":
                Schema = objectSchema()
                        .id(JsonPointer.fromURI(URI.create(ENV_URI + schemaName)))
                        .requiredProperty("name", stringSchema())
                        .requiredProperty("age", intSchema())
                        .toJson();
                break;
            case "schema2":
                Schema = objectSchema()
                        .id(JsonPointer.fromURI(URI.create(ENV_URI + schemaName)))
                        .requiredProperty("name", stringSchema())
                        .requiredProperty("color", stringSchema())
                        .toJson();
                break;
            default:
                Schema = objectSchema()
                        .id(JsonPointer.fromURI(URI.create(ENV_URI + schemaName)))
                        .requiredProperty("name", stringSchema())
                        .requiredProperty("dob", stringSchema().with(format(StringFormat.DATE)))
                        .toJson();
                break;
        }

        return JsonSchema.of(Schema);
    }
}
