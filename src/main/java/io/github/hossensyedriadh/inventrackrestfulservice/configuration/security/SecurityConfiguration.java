package io.github.hossensyedriadh.inventrackrestfulservice.configuration.security;

import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.entrypoint.BearerAuthenticationEntrypoint;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.filter.BearerAuthenticationFilter;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.filter.ExceptionFilter;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.handler.ApiAccessDeniedHandler;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.service.BearerAuthenticationUserDetailsService;
import io.github.hossensyedriadh.inventrackrestfulservice.configuration.filter.RequestHandleFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
    private final BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService;
    private final ApiAccessDeniedHandler apiAccessDeniedHandler;
    private final BearerAuthenticationEntrypoint bearerAuthenticationEntrypoint;
    private final BearerAuthenticationFilter bearerAuthenticationFilter;
    private final ExceptionFilter exceptionFilter;
    private final RequestHandleFilter requestHandleFilter;

    @Autowired
    public SecurityConfiguration(BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService,
                                 ApiAccessDeniedHandler apiAccessDeniedHandler, BearerAuthenticationEntrypoint bearerAuthenticationEntrypoint,
                                 BearerAuthenticationFilter bearerAuthenticationFilter, ExceptionFilter exceptionFilter,
                                 RequestHandleFilter requestHandleFilter) {
        this.bearerAuthenticationUserDetailsService = bearerAuthenticationUserDetailsService;
        this.apiAccessDeniedHandler = apiAccessDeniedHandler;
        this.bearerAuthenticationEntrypoint = bearerAuthenticationEntrypoint;
        this.bearerAuthenticationFilter = bearerAuthenticationFilter;
        this.exceptionFilter = exceptionFilter;
        this.requestHandleFilter = requestHandleFilter;
    }

    @Bean
    @Profile("dev")
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().authenticationEntryPoint(this.bearerAuthenticationEntrypoint)
                .and().exceptionHandling().accessDeniedHandler(this.apiAccessDeniedHandler)
                .and().userDetailsService(this.bearerAuthenticationUserDetailsService)
                .authorizeRequests(configurer -> configurer.antMatchers("/v1/authentication/**", "/v1/public/**",
                                "/error", "/actuator/info", "/actuator/health", "/v3/api-docs", "/swagger-resources/**",
                                "/swagger-resources", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated());

        http.addFilterBefore(bearerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionFilter, BearerAuthenticationFilter.class);
        http.addFilterAfter(requestHandleFilter, BearerAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Profile("pilot")
    public SecurityFilterChain pilotSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().authenticationEntryPoint(this.bearerAuthenticationEntrypoint)
                .and().exceptionHandling().accessDeniedHandler(this.apiAccessDeniedHandler)
                .and().userDetailsService(this.bearerAuthenticationUserDetailsService)
                .authorizeRequests(configurer -> configurer.antMatchers("/v1/authentication/**", "/v1/public/**",
                                "/error", "/actuator/info", "/actuator/health", "/v3/api-docs", "/swagger-resources/**", "/swagger-resources", "/swagger-ui/**")
                        .permitAll().anyRequest().authenticated());

        //http.requiresChannel().anyRequest().requiresSecure();

        http.headers().xssProtection().block(true).and().contentSecurityPolicy("script-src 'self'");
        http.headers().referrerPolicy(config -> config.policy(
                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
        ));
        http.headers().frameOptions().deny();
        http.headers().httpStrictTransportSecurity().includeSubDomains(true).maxAgeInSeconds(31536000);

        http.addFilterBefore(bearerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionFilter, BearerAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Profile("pilot")
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(httpFirewall());
    }

    @Bean
    @Profile("pilot")
    public HttpFirewall httpFirewall() {
        return new StrictHttpFirewall();
    }

    @Bean
    @Profile("pilot")
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }
}
