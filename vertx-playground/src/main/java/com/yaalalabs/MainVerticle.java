package com.yaalalabs;

import com.yaalalabs.schema.SchemaService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {
    private SchemaService schemaService;
    private ApiRouter apiRouter;

    @Override
    public void start(Promise<Void> startPromise) {
        schemaService = new SchemaService(vertx);
        apiRouter = new ApiRouter(schemaService);

        schemaService.loadSchemas().onComplete(ar -> {
            if (ar.succeeded()) {
                Router router = apiRouter.createRouter(vertx);
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
            } else {
                startPromise.fail(ar.cause());
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
