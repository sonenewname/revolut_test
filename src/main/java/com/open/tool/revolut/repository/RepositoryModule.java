package com.open.tool.revolut.repository;

import com.google.inject.AbstractModule;
import com.open.tool.revolut.controller.handler.AccountController;
import com.open.tool.revolut.repository.impl.AccountRepositoryImpl;
import com.open.tool.revolut.repository.impl.OperationRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryModule extends AbstractModule {


    private static Logger log = LoggerFactory.getLogger(RepositoryModule.class);
    @Override
    protected void configure() {
        log.info("Initializing RepositoryModule");
        bind(AccountRepository.class).to(AccountRepositoryImpl.class);
        bind(OperationRepository.class).to(OperationRepositoryImpl.class);
    }
}
