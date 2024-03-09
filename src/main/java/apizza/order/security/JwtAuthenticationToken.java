package apizza.order.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private String token;

    @Getter
    private UUID userId;

    public static JwtAuthenticationToken unauthenticated(String token) {
        return new JwtAuthenticationToken(token);
    }

    public static JwtAuthenticationToken authenticated(UUID userID, Collection<? extends GrantedAuthority> authorities) {
        return new JwtAuthenticationToken(userID, authorities);
    }

    private JwtAuthenticationToken(String token) {
        super(List.of());
        this.token = token;

        super.setAuthenticated(false);
    }

    private JwtAuthenticationToken(UUID userId, Collection<? extends GrantedAuthority> authorities) {
        super(Collections.unmodifiableCollection(authorities));
        this.userId = userId;

        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        Assert.isTrue(!super.isAuthenticated(),
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }
}
