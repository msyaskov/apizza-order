package apizza.order.configuration;

import apizza.order.security.JWKSetLoader;
import apizza.order.security.JwtAuthenticationFilter;
import apizza.order.security.JwtAuthenticationProvider;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;

import java.text.ParseException;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterAfter(jwtAuthenticationFilter(), HeaderWriterFilter.class);
        http.authenticationManager(providerManager());

        return http.build();
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        filter.setExceptionIfHeaderMissing(false);

        return filter;
    }

    private AuthenticationManager providerManager() {
        return new ProviderManager(jwtAuthenticationProvider());
    }

    private JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwkSetLoader());
    }

    private JWKSetLoader jwkSetLoader() {
        return () -> {
            try {
                return JWKSet.parse("""
                        {
                            "keys": [{
                                "kty": "RSA",
                                "e": "AQAB",
                                "use": "sig",
                                "kid": "key-id",
                                "alg": "RS256",
                                "n": "gkvMF-NmBcDLa79Ny3CIk21D74PlsHz_Mzz84bjGWcIE1OJOWVQ7OLlpEv5HpiGAvr101POYb6hs1bbU8QdyiQ5t8NYU2hUIKjvBmTi_hpGXcpY_3o4nepIhBuu-iH2UPWFlLo_e5nwLSHUtWm_5P1sH1vDQ6KOWzQfrevYWOBuVJBk-AAXcCriRUywtVlPWwAAAqW0M8JKDmtkKo0zKPjsdYvvbnbG0RdbzXzBwlklWLrXSkzNHKTb6s5UhgscjbcjR9U6Ws-rArd1IjvzI8c_cleS7rIMYHOO2YYIR-9cIH9Hy-P1QTjLGNHIUrhE70XMeqmt2oPXArJKhtZl2uQ"
                            }]
                        }
                        """);
            } catch (ParseException e) {
                throw new InternalAuthenticationServiceException("Can't load public JWKSet");
            }
        };
    }

}
