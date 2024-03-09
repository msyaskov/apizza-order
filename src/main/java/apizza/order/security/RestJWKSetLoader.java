package apizza.order.security;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class RestJWKSetLoader implements JWKSetLoader {

    private final RestTemplate restTemplate;

    private final String targetUrl;

    @Override
    public JWKSet load() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(targetUrl, HttpMethod.GET,
                RequestEntity.EMPTY, new ParameterizedTypeReference<Map<String, Object>>() {});

        JWKSet jwks = null;
        try {
            jwks = JWKSet.parse(Objects.requireNonNull(response.getBody()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return jwks;
    }
}
