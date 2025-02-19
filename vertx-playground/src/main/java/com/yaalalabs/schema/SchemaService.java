package com.yaalalabs.schema;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.JsonSchema;
import io.vertx.json.schema.OutputUnit;
import io.vertx.json.schema.Validator;

public class SchemaService {
    private final Vertx vertx;

    public SchemaService(Vertx vertx) {
        this.vertx = vertx;
    }

    public Future<Void> loadSchemas() {
        FileSystem fs = vertx.fileSystem();
        return fs.readFile("schema.json").compose(buffer -> {
            JsonObject schema = buffer.toJsonObject();
            if (schema != null) {
                System.out.println("Adding schema: " + schema.getString("$id"));
                SchemaValidation.addSchema(JsonSchema.of(schema));
                return Future.succeededFuture();
            } else {
                return Future.failedFuture(new Exception("Failed to load schema"));
            }
        });
    }

    public OutputUnit validate(String schemaId, JsonObject data) {
        Validator validator = SchemaValidation.getValidator(schemaId);
        return validator.validate(data);
    }
}
