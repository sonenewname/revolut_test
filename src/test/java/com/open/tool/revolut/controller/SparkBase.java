package com.open.tool.revolut.controller;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.open.tool.revolut.controller.ControllerModule;
import com.open.tool.revolut.controller.Router;
import com.open.tool.revolut.controller.handler.AccountController;
import com.open.tool.revolut.controller.handler.OperationController;
import com.open.tool.revolut.model.rest.req.AccountRestReq;
import com.open.tool.revolut.model.rest.res.AccountRestRes;
import com.open.tool.revolut.repository.RepositoryModule;
import com.open.tool.revolut.service.ServiceModule;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static spark.Spark.*;

public class SparkBase {

    private static final int port = 8090;
    protected static String baseUri = "http://localhost:" + port + "/api/";
    protected static String accountUri = baseUri + "accounts";
    protected static String operationUri = baseUri + "operations";

    protected static Gson gson = new Gson();
    private static Injector injector = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            install(new RepositoryModule());
            install(new ServiceModule());
            install(new ControllerModule());
        }
    });


    protected static void runServer() {
        AccountController accountController = injector.getInstance(AccountController.class);
        OperationController operationController = injector.getInstance(OperationController.class);
        Router r = new Router(accountController, operationController);
        r.init(8090);

        awaitInitialization();
    }


    protected static void stopServer() {
        stop();
        awaitStop();
    }

    protected static AccountRestRes createAccount(HttpClient client, String name) throws IOException, InterruptedException, URISyntaxException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(accountUri))
                .POST(HttpRequest.BodyPublishers.ofString(
                        gson.toJson(AccountRestReq.builder()
                                .name(name)
                                .build()
                        ))
                )
                .build();

        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(postResponse.body(), AccountRestRes.class);

    }

    protected AccountRestRes getAccount(HttpClient client, Long id) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(accountUri + "/" + id))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(getResponse.body(), AccountRestRes.class);
    }

}
