package apizza.order.security;

import com.nimbusds.jose.jwk.JWKSet;

public class EmptyJWKSetLoader implements JWKSetLoader {
    @Override
    public JWKSet load() {
        return new JWKSet();
    }
}
