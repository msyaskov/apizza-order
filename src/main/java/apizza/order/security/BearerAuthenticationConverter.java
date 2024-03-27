package apizza.order.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

public class BearerAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        final String headerValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (headerValue == null || !headerValue.startsWith("Bearer ")) {
            return null;
        }

        final String token = headerValue.substring(7);  // 'Bearer '
        return BearerAuthenticationToken.unauthenticated(token);
    }
}
