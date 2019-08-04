package com.open.tool.revolut.controller;

import com.google.inject.Inject;
import com.open.tool.revolut.controller.handler.AccountController;
import com.open.tool.revolut.controller.handler.OperationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.path;
import static spark.Spark.port;

public class Router {
    private static Logger log = LoggerFactory.getLogger(Router.class);


    private List<Controller> applicationControllers = new ArrayList<>();

    @Inject
    public Router(final AccountController accountController, final OperationController operationController) {
        applicationControllers.add(accountController);
        applicationControllers.add(operationController);
    }


    public void init(final int port) {
        log.info("Spark starting on {} port", port);
        port(port);

        log.info("Spark has {} controller(s)", applicationControllers.size());
        path("/api", () -> applicationControllers.forEach(Controller::init));

    }
}
