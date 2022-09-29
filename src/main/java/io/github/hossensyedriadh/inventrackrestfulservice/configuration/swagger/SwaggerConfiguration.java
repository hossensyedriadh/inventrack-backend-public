package io.github.hossensyedriadh.inventrackrestfulservice.configuration.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@Configuration
@EnableSwagger2
@EnableOpenApi
@Import({BeanValidatorPluginsConfiguration.class})
@PropertySource("classpath:application.properties")
public class SwaggerConfiguration {
    @Value("${info.application.metadata.name}")
    private String applicationName;

    @Value("${info.application.metadata.description}")
    private String applicationDescription;

    @Value("${info.application.metadata.version}")
    private String applicationVersion;

    @Value("${info.application.developer.name}")
    private String developerName;

    @Value("${info.application.developer.url}")
    private String developerUrl;

    @Value("${info.application.developer.email}")
    private String developerEmail;

    @Value("${server.servlet.context-path}")
    private String applicationContextPath;

    @Bean
    public Docket swaggerUiConfiguration() {
        Set<String> protocols = new HashSet<>();
        protocols.add("https");

        Set<String> consumedTypes = new HashSet<>();
        consumedTypes.add(MediaType.APPLICATION_JSON_VALUE);

        return new Docket(DocumentationType.OAS_30)
                .securityContexts(List.of(this.securityContext()))
                .securitySchemes(List.of(this.apiKey()))
                .select().apis(RequestHandlerSelectors.basePackage("io.github.hossensyedriadh.inventrackrestfulservice.controller"))
                .paths(PathSelectors.ant("/" + this.applicationContextPath + "/**")).build()
                .apiInfo(this.metadata()).protocols(protocols).consumes(consumedTypes);
    }

    private ApiInfo metadata() {
        return new ApiInfo(this.applicationName, this.applicationDescription, this.applicationVersion,
                null, new Contact(this.developerName, this.developerUrl, this.developerEmail), "MIT License",
                "https://github.com/hossensyedriadh/inventrack-restful-service/blob/main/LICENSE", Collections.emptyList());
    }

    private ApiKey apiKey() {
        return new ApiKey(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(this.defaultAuthentication()).build();
    }

    private List<SecurityReference> defaultAuthentication() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "Access secured endpoints");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;

        return List.of(new SecurityReference(HttpHeaders.AUTHORIZATION, authorizationScopes));
    }

    /*@Bean
    public UiConfiguration uiConfig() {
        return new UiConfiguration(true, false, 0, 0,
                null, false, null, null,
                0, null, true, true, null,
                UiConfiguration.Constants.NO_SUBMIT_METHODS, "https://validator.swagger.io/validator", null);
    }*/

    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
                                                                         ServletEndpointsSupplier servletEndpointsSupplier,
                                                                         ControllerEndpointsSupplier controllerEndpointsSupplier,
                                                                         EndpointMediaTypes endpointMediaTypes,
                                                                         CorsEndpointProperties corsProperties,
                                                                         WebEndpointProperties webEndpointProperties, Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(),
                new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
    }

    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
        return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
                || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
    }
}
