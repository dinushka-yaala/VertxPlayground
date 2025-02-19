package com.yaalalabs;

import com.yaalalabs.schema.SchemaValidation;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        SchemaValidation.init();
        
        vertx.deployVerticle(new MainVerticle(), res -> {
            if (res.succeeded()) {
                System.out.println("MainVerticle deployed successfully");

                // Register a shutdown hook
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Shutdown initiated...");
                    vertx.close().onComplete(ar -> {
                        if (ar.succeeded()) {
                            System.out.println("Vert.x closed successfully");
                        } else {
                            System.err.println("Error closing Vert.x: " + ar.cause().getMessage());
                        }
                    });
                }));
            } else {
                System.err.println("Failed to deploy MainVerticle: " + res.cause().getMessage());
            }
        });
    }
}