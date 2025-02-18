package com.yaalalabs;

import com.yaalalabs.schema.SchemaValidation;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.impl.BodyHandlerImpl;
import io.vertx.json.schema.OutputUnit;
import io.vertx.json.schema.Validator;

public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(new BodyHandlerImpl());

        router.get("/").handler(ctx -> {
            ctx.response().end("Welcome to Vert.x!");
        });
        router.get("/hello/:name").handler(ctx -> {
            String name = ctx.pathParam("name");
            ctx.response().end("Hello " + (name == null ? "World" : name) + " !");
        });
        router.post("/age").handler(ctx -> {
            Validator validator = SchemaValidation.getValidator("http://localhost:8080/schema1");
            JsonObject body = ctx.body().asJsonObject();
            OutputUnit validationResult = validator.validate(body);
            if (validationResult.getValid()) {
                ctx.response().end("Your age is " + body.getInteger("age"));
            } else {
                ctx.response().end(validationResult.toJson().encodePrettily());
            }
        });
        router.post("/color").handler(ctx -> {
            Validator validatorbase = SchemaValidation.getValidator("http://localhost:8080");
            Validator validator = SchemaValidation.getValidator("http://localhost:8080/schema2");
            JsonObject body = ctx.body().asJsonObject();
            OutputUnit validationResultbase = validatorbase.validate(body);
            OutputUnit validationResult = validator.validate(body);
            if (validationResultbase.getValid() && validationResult.getValid()) {
                ctx.response().end("Your favorite color is " + body.getString("color"));
            } else {
                ctx.response().end(validationResultbase.toJson().encodePrettily());
            }
        });
        router.post("/dob").handler(ctx -> {
            Validator validator = SchemaValidation.getValidator("http://localhost:8080/schema3");
            JsonObject body = ctx.body().asJsonObject();
            OutputUnit validationResult = validator.validate(body);
            if (validationResult.getValid()) {
                ctx.response().end("Your date of birth is " + body.getString("dob"));
            } else {
                ctx.response().end(validationResult.toJson().encodePrettily());
            }
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .exceptionHandler(this::errorHandler)
                .listen(8080, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("HTTP server started on port 8080");
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }

    private void errorHandler(Throwable throwable) {
        System.out.println("Error occurred: " + throwable.getMessage());
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        System.out.println("Shutting down the server");
        stopPromise.complete();
    }
}
