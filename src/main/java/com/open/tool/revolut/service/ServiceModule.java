package com.open.tool.revolut.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.open.tool.revolut.controller.handler.AccountController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(ServiceModule.class);

    @Override
    protected void configure() {
        log.info("Initializing ServiceModule");
        bind(AccountService.class).in(Singleton.class);
        bind(ValidationService.class).in(Singleton.class);
        bind(OperationService.class).in(Singleton.class);
    }
}
