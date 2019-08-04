package com.open.tool.revolut;

import com.google.inject.Guice;
import com.open.tool.revolut.config.ApplicationModule;
import com.open.tool.revolut.controller.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        Guice.createInjector(new ApplicationModule())
                .getInstance(Router.class)
                .init(getPort(args));
    }

    private static int getPort(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                log.error("Provided as argument port must be numeric: {}. Application will use default port: {}",
                        args[0], port);
            }
        } else {
            log.info("Port not presented in argument. Application will use default port: {}", port);
        }
        return port;
    }
}
