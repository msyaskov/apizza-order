package apizza.order.security;

import apizza.order.util.logging.Logging;
import com.nimbusds.jose.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class BearerAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        /*
            Этот сервис за API Gateway, который проверяет токены.
            Поэтому доверяем токену (не проверяем его).
         */

        Assert.isInstanceOf(BearerAuthenticationToken.class, authentication);
        try {
            JWSObject token = JWSObject.parse(((BearerAuthenticationToken) authentication).getToken());
            return obtainAuthenticationFromToken(token);
        } catch (ParseException e) {
            log.debug("Invalid JWT", e);
            throw new BadCredentialsException("Invalid JWT", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private BearerAuthenticationToken obtainAuthenticationFromToken(JWSObject token) {
        Payload payload = token.getPayload();
        if (payload == null) {
            log.debug("JWT payload is null");
            throw new BadCredentialsException("JWT Payload is null");
        }

        Map<String, Object> payloadMap = payload.toJSONObject();
        String sub = (String) payloadMap.get("sub");
        if (sub == null) {
            log.debug("JWT payload sub is null");
            throw new BadCredentialsException("Claim 'sub' is null");
        }
        UUID userId = null;
        try {
            userId = UUID.fromString(sub);
        } catch (Exception e) {
            log.debug("JWT payload sub is not UUID");
            throw new BadCredentialsException("Claim 'sub' unknown format");
        }

        String scopes = (String) payloadMap.get("scope");
        if (scopes == null) {
            log.debug("JWT payload scope is null");
            throw new BadCredentialsException("Claim 'scope' is null");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(StringUtils.delimitedListToStringArray(scopes, " "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return BearerAuthenticationToken.authenticated(userId, authorities);
    }
}
