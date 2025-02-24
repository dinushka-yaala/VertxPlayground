package com.yaalalabs;

import com.yaalalabs.schema.SchemaService;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.json.schema.OutputUnit;

public class ApiRouter {
    private final SchemaService schemaService;

    public ApiRouter(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    public Router createRouter(Vertx vertx) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/").handler(ctx -> ctx.response().end("Welcome to Vert.x!"));

        router.get("/hello/:name").handler(ctx -> {
            String name = ctx.pathParam("name");
            ctx.response().end("Hello " + (name == null ? "World" : name) + " !");
        });

        router.post("/age").handler(ctx -> validateRequest(ctx, "root#schema1", "age"))
                .failureHandler(this::failureHandler);
        router.post("/color").handler(ctx -> validateRequest(ctx, "root#schema2", " favourite color"))
                .failureHandler(this::failureHandler);
        router.post("/dob").handler(ctx -> validateRequest(ctx, "root#schema3", "dob"))
                .failureHandler(this::failureHandler);
        router.post("/array").handler(ctx -> validateAndEcho(ctx, "root#schema4"))
                .failureHandler(this::failureHandler);
        router.post("/orderbookStat").handler(ctx -> validateAndEcho(ctx, "root#orderbookStat"))
                .failureHandler(this::failureHandler);

        return router;
    }

    private void validateAndEcho(RoutingContext ctx, String schemaId) {
        JsonObject body = ctx.body().asJsonObject();
        OutputUnit result = schemaService.validate(schemaId, body);
        if (result.getValid()) {
            ctx.response().end(body.encodePrettily());
        } else {
            ctx.response().end(result.toJson().encodePrettily());
        }
    }

    private void validateRequest(RoutingContext ctx, String schemaId, String fieldName) {
        JsonObject body = ctx.body().asJsonObject();
        OutputUnit validationResult = schemaService.validate(schemaId, body);
        if (validationResult.getValid()) {
            ctx.response().end("Your " + fieldName + " is " + body.getString(fieldName));
        } else {
            ctx.response().end(validationResult.toJson().encodePrettily());
        }
    }

    private void failureHandler(RoutingContext ctx) {
        Throwable failure = ctx.failure();
        if (failure != null) {
            ctx.response().setStatusCode(500).end("Internal Server Error: " + failure.getMessage());
        } else {
            ctx.response().setStatusCode(400).end("Bad Request");
        }
    }
}
