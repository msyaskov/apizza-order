package apizza.order.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class CurrentBearerTokenClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @NonNull
    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body,
                                        @NonNull ClientHttpRequestExecution execution) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof BearerAuthenticationToken jwt) {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getToken());
        }

        return execution.execute(request, body);
    }
}
