package io.github.hossensyedriadh.InvenTrackRESTfulService.configuration.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiCorsConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Configure "global" cross-origin request processing. The configured CORS mappings apply to
             * annotated controllers, functional endpoints, and static resources.
             * <p>Annotated controllers can further declare more fine-grained config via
             * {@link CrossOrigin @CrossOrigin}. In such cases "global" CORS configuration declared here is
             * {@link CorsConfiguration#combine(CorsConfiguration) combined} with local CORS configuration
             * defined on a controller method.
             *
             * @param registry cors registry
             * @see CorsRegistry
             * @see CorsConfiguration#combine(CorsConfiguration)
             * @since 4.2
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*")
                        .allowedMethods(HttpMethod.GET.toString(), HttpMethod.POST.toString(), HttpMethod.PUT.toString(),
                                HttpMethod.PATCH.toString(), HttpMethod.OPTIONS.toString(), HttpMethod.HEAD.toString())
                        .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)
                        .allowCredentials(false).maxAge(7200);
            }
        };
    }
}
