package apizza.order.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JWKSetLoader jwkSetLoader;

    @Setter
    private Set<JWSAlgorithm> allowedSignatureAlgorithms = Set.of(JWSAlgorithm.RS256);

    @Setter
    private Set<JOSEObjectType> allowedTokenTypes = Set.of(JOSEObjectType.JWT);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication);
        try {
            JWSObject token = JWSObject.parse(((JwtAuthenticationToken) authentication).getToken());
            checkTokenHeader(token);

            RSAKey publicKeyForToken = loadPublicRSAKeyForToken(token);
            if (publicKeyForToken == null) {
                throw new PreAuthenticatedCredentialsNotFoundException("Not found key");
            }

            JWSVerifier verifier = new RSASSAVerifier(publicKeyForToken);
            if (!token.verify(verifier)) {
                throw new BadCredentialsException("Bad signature");
            }

            return obtainAuthenticationFromToken(token);
        } catch (ParseException e) {
            throw new BadCredentialsException("Invalid JWT", e);
        } catch (JOSEException e) {
            throw new InternalAuthenticationServiceException("Can't verify JWT", e);
        }
    }

    private RSAKey loadPublicRSAKeyForToken(JWSObject token) {
        JWKSet publicJWKSet = jwkSetLoader.load();
        JWKMatcher tokenKeyMatcher = new JWKMatcher.Builder()
                .algorithm(token.getHeader().getAlgorithm())
                .keyID(token.getHeader().getKeyID())
                .keyUse(KeyUse.SIGNATURE)
                .build();

        List<JWK> publicKeysForToken = publicJWKSet.filter(tokenKeyMatcher).getKeys();
        return publicKeysForToken.size() != 0 ? publicKeysForToken.get(0).toRSAKey() : null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void checkTokenHeader(JWSObject token) {
        if (token.getHeader().getKeyID() == null) {
            throw new BadCredentialsException("KeyId is null");
        }

        JOSEObjectType type = token.getHeader().getType();
        if (type == null || !allowedTokenTypes.contains(type)) {
            throw new BadCredentialsException("Type is not allowed: %s".formatted(type));
        }

        JWSAlgorithm algorithm = token.getHeader().getAlgorithm();
        if (algorithm == null || !allowedSignatureAlgorithms.contains(algorithm)) {
            throw new BadCredentialsException("Algorithm is not allowed: %s".formatted(algorithm));
        }
    }

    private JwtAuthenticationToken obtainAuthenticationFromToken(JWSObject token) {
        Payload payload = token.getPayload();
        if (payload == null) {
            throw new BadCredentialsException("JWT Payload is null");
        }

        Map<String, Object> payloadMap = payload.toJSONObject();
        String sub = (String) payloadMap.get("sub");
        if (sub == null) {
            throw new BadCredentialsException("Claim 'sub' is null");
        }
        UUID userId = null;
        try {
            userId = UUID.fromString(sub);
        } catch (Exception e) {
            throw new BadCredentialsException("Claim 'sub' unknown format");
        }

        String scopes = (String) payloadMap.get("scopes");
        if (scopes == null) {
            throw new BadCredentialsException("Claim 'scope' is null");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(StringUtils.delimitedListToStringArray(scopes, " "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return JwtAuthenticationToken.authenticated(userId, authorities);
    }
}
