package io.github.hossensyedriadh.InvenTrackRESTfulService.configuration.security;

import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.entrypoint.JwtAuthenticationEntryPoint;
import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.filter.ExceptionFilter;
import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.filter.JwtAuthenticationFilter;
import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.handler.ApiAccessDeniedHandler;
import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final ApiAccessDeniedHandler apiAccessDeniedHandler;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionFilter exceptionFilter;

    /**
     * Creates an instance with the default configuration enabled.
     */
    @Autowired
    public SecurityConfiguration(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                 ApiAccessDeniedHandler apiAccessDeniedHandler,
                                 JwtUserDetailsService jwtUserDetailsService,
                                 JwtAuthenticationFilter jwtAuthenticationFilter,
                                 ExceptionFilter exceptionFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.apiAccessDeniedHandler = apiAccessDeniedHandler;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.exceptionFilter = exceptionFilter;
    }

    /**
     * Used by the default implementation of {@link #authenticationManager()} to attempt to obtain an {@link
     * AuthenticationManager}. If overridden, the {@link AuthenticationManagerBuilder} should be used to specify the
     * {@link AuthenticationManager}.
     *
     * <p>
     * The {@link #authenticationManagerBean()} method can be used to expose the resulting {@link AuthenticationManager}
     * as a Bean. The {@link #userDetailsServiceBean()} can be used to expose the last populated {@link
     * UserDetailsService} that is created with the {@link AuthenticationManagerBuilder} as a Bean. The {@link
     * UserDetailsService} will also automatically be populated on {@link HttpSecurity#getSharedObject(Class)} for use
     * with other {@link SecurityContextConfigurer} (i.e. RememberMeConfigurer )
     * </p>
     *
     * <p>
     * For example, the following configuration could be used to register in memory authentication that exposes an in
     * memory {@link UserDetailsService}:
     * </p>
     *
     * <pre>
     * &#064;Override
     * protected void configure(AuthenticationManagerBuilder auth) {
     * 	auth
     * 	// enable in memory based authentication with a user named
     * 	// &quot;user&quot; and &quot;admin&quot;
     * 	.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;).and()
     * 			.withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;, &quot;ADMIN&quot;);
     * }
     *
     * // Expose the UserDetailsService as a Bean
     * &#064;Bean
     * &#064;Override
     * public UserDetailsService userDetailsServiceBean() throws Exception {
     * 	return super.userDetailsServiceBean();
     * }
     *
     * </pre>
     *
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception caught
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * Gets the {@link AuthenticationManager} to use. The default strategy is if {@link
     * #configure(AuthenticationManagerBuilder)} method is overridden to use the {@link AuthenticationManagerBuilder}
     * that was passed in. Otherwise, autowire the {@link AuthenticationManager} by type.
     *
     * @return the {@link AuthenticationManager} to use
     * @throws Exception caught
     */
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * Override this method to configure the {@link HttpSecurity}. Typically, subclasses should not invoke this method
     * by calling super as it may override their configuration. The default configuration is:
     *
     * <pre>
     * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
     * </pre>
     * <p>
     * Any endpoint that requires defense against common vulnerabilities can be specified here, including public ones.
     * See {@link HttpSecurity#authorizeRequests} and the `permitAll()` authorization rule for more details on public
     * endpoints.
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and().exceptionHandling().accessDeniedHandler(apiAccessDeniedHandler)
                .and().authorizeRequests(configurer -> configurer.antMatchers("/v1/authentication/**",
                        "/v1/public/**", "/error", "/actuator/**").permitAll().anyRequest().authenticated());

        //http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        http.requiresChannel().anyRequest().requiresSecure();

        http.headers().xssProtection().block(false).and().contentSecurityPolicy("script-src 'self'");

        http.headers().referrerPolicy(referrerPolicyConfig -> referrerPolicyConfig.policy(
                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
        ));

        http.headers().frameOptions().deny();

        http.headers().httpStrictTransportSecurity().includeSubDomains(true).maxAgeInSeconds(31536000);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionFilter, JwtAuthenticationFilter.class);
    }

    /**
     * Override this method to configure {@link WebSecurity}. For example, if you wish to
     * ignore certain requests.
     * <p>
     * Endpoints specified in this method will be ignored by Spring Security, meaning it
     * will not protect them from CSRF, XSS, Click-jacking, and so on.
     * <p>
     * Instead, if you want to protect endpoints against common vulnerabilities, then see
     * {@link #configure(HttpSecurity)} and the {@link HttpSecurity#authorizeRequests}
     * configuration method.
     *
     * @param web web security object
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v3/api-docs", "/swagger-resources/**",
                "/swagger-resources", "/swagger-ui/**");
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
