package com.open.tool.revolut.config;

import com.google.inject.AbstractModule;
import com.open.tool.revolut.repository.RepositoryModule;
import com.open.tool.revolut.service.ServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(ApplicationModule.class);

    @Override
    protected void configure() {
        log.info("Initializing ApplicationModule");
        install(new RepositoryModule());
        install(new ServiceModule());
    }
}
