package io.github.hossensyedriadh.inventrackrestfulservice.configuration.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {
    /**
     * Override this method to add additional configuration.
     *
     * @param config Main configuration bean.
     * @param cors   CORS configuration.
     * @since 3.4
     */
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.disableDefaultExposure();
    }
}
