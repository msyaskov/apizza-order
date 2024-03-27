package apizza.order.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class WithMockJWTAdminSecurityContextFactory implements WithSecurityContextFactory<WithMockJWTAdmin> {

    private static final Collection<? extends GrantedAuthority> DEFAULT_AUTHORITIES
            = List.of(new SimpleGrantedAuthority("ADMIN"));

    @Override
    public SecurityContext createSecurityContext(WithMockJWTAdmin annotation) {
        UUID userId = UUID.fromString(annotation.userId());
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(BearerAuthenticationToken.authenticated(userId, DEFAULT_AUTHORITIES));
        return securityContext;
    }
}
