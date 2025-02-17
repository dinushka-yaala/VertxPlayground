package com.yaalalabs;

import com.yaalalabs.schema.SchemaValidation;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        SchemaValidation.init();
        vertx.deployVerticle(new MainVerticle());
    }
}