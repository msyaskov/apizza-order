package apizza.order.configuration;

import apizza.order.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;

@Configuration
@EnableMethodSecurity
public class SecurityFilterChainConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterAfter(authenticationFilter(), HeaderWriterFilter.class);

        return http.build();
    }

    private BasicAuthenticationFilter authenticationFilter() {
        AuthenticationManager authenticationManager = new ProviderManager(new BearerAuthenticationProvider());

        BasicAuthenticationFilter filter = new BasicAuthenticationFilter(authenticationManager);
        filter.setAuthenticationConverter(new BearerAuthenticationConverter());

        return filter;
    }
}
