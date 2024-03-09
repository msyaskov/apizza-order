package apizza.order.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

public class JwtAuthenticationFilter extends RequestHeaderAuthenticationFilter {

    public static final String DEFAULT_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;

    public JwtAuthenticationFilter() {
        setPrincipalRequestHeader(DEFAULT_TOKEN_HEADER);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        Object headerValue = super.getPreAuthenticatedPrincipal(request);
        return headerValue != null ? headerValue.toString().substring(7) : null; // 'Bearer '
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return null;
    }
}
