package org.wso2.apimgt.choreo.rest.api.analytics.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.ConfigHolder;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.Configuration;
import springfox.documentation.oas.annotations.EnableOpenApi;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableOpenApi
@ComponentScan(basePackages = { "org.wso2.apimgt.choreo.rest.api.analytics.impl",
        "org.wso2.apimgt.choreo.rest.api.analytics.v1", "org.wso2.apimgt.choreo" })
public class Application {
    @Autowired
    private Configuration configuration;

    @PostConstruct
    void postConstruct() {
        ConfigHolder.getInstance().setConfiguration(configuration);
        if (configuration.getSecurity().getTrustStore() != null
                && configuration.getSecurity().getTrustStorePass() != null) {
            System.setProperty("javax.net.ssl.trustStore", configuration.getSecurity().getTrustStore());
            System.setProperty("javax.net.ssl.trustStorePassword", configuration.getSecurity().getTrustStorePass());
        }

    }

    public static void main(String[] args) throws Exception {
        new SpringApplication(Application.class).run(args);
    }

    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
}
