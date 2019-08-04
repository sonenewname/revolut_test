package com.open.tool.revolut.controller;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.open.tool.revolut.controller.handler.AccountController;
import com.open.tool.revolut.controller.handler.OperationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerModule extends AbstractModule {


    private static Logger log = LoggerFactory.getLogger(ControllerModule.class);

    @Override
    protected void configure() {
        log.info("Initializing ControllerModule");
        bind(AccountController.class).in(Singleton.class);
        bind(OperationController.class).in(Singleton.class);
    }
}
