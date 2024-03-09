package apizza.order.security;

import com.nimbusds.jose.jwk.JWKSet;

public interface JWKSetLoader {

    JWKSet load();

}
