package uk.ac.ebi.tsc.aap.client.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by neilg on 24/05/2017.
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass({ EnableWebSecurity.class, AuthenticationEntryPoint.class })
@ConditionalOnMissingBean(WebSecurityConfiguration.class)
@ConditionalOnWebApplication
@EnableWebSecurity
@ComponentScan("uk.ac.ebi.tsc.aap.client.security")
@PropertySource(value = "/application-security.properties")
public class AAPWebSecurityAutoConfiguration {

    @Configuration
    @Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
    public static  class AAPWebSecurityConfig extends WebSecurityConfigurerAdapter {
        private static final Logger LOGGER = LoggerFactory.getLogger(AAPWebSecurityConfig.class);

        @Autowired
        private StatelessAuthenticationEntryPoint unauthorizedHandler;

        @Autowired
        private TokenAuthenticationService tokenAuthenticationService;

        private StatelessAuthenticationFilter statelessAuthenticationFilterBean() throws Exception {
            LOGGER.info("this.tokenAuthenticationService: " + this.tokenAuthenticationService);
            return new StatelessAuthenticationFilter(this.tokenAuthenticationService);
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            LOGGER.info("[StatelessAuthenticationEntryPoint]- " + unauthorizedHandler);
            httpSecurity
                    // we don't need CSRF because our token is invulnerable
                    .csrf().disable()
                    .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                    // don't create session
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .authorizeRequests().anyRequest().authenticated();

            httpSecurity.addFilterBefore(statelessAuthenticationFilterBean(),
                    UsernamePasswordAuthenticationFilter.class);
            // disable page caching
            httpSecurity.headers().cacheControl();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService());
        }
    }
}
