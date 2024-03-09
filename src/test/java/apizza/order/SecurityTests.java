package apizza.order;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SecurityTests {

    private static final String jwksString = """
            {
                "keys": [{
                    "kty": "RSA",
                    "e": "AQAB",
                    "use": "sig",
                    "kid": "key-id",
                    "alg": "RS256",
                    "n": "gkvMF-NmBcDLa79Ny3CIk21D74PlsHz_Mzz84bjGWcIE1OJOWVQ7OLlpEv5HpiGAvr101POYb6hs1bbU8QdyiQ5t8NYU2hUIKjvBmTi_hpGXcpY_3o4nepIhBuu-iH2UPWFlLo_e5nwLSHUtWm_5P1sH1vDQ6KOWzQfrevYWOBuVJBk-AAXcCriRUywtVlPWwAAAqW0M8JKDmtkKo0zKPjsdYvvbnbG0RdbzXzBwlklWLrXSkzNHKTb6s5UhgscjbcjR9U6Ws-rArd1IjvzI8c_cleS7rIMYHOO2YYIR-9cIH9Hy-P1QTjLGNHIUrhE70XMeqmt2oPXArJKhtZl2uQ"
                }]
            }
            """;

    private static final String jwksPair = """
            {
                "keys": [
                    {
                        "p": "wmZu4As12CCVkFTVMegse-UvIaI-tXsgmQx1HGJ3TO38s2MSi32G1WpyLC7A4ow82RvgcWxmUjhlai67OuRZYCRYINTjEJVbYtVepBBHCUqahZvZv5jKbnHtkaXCjI54HIPeXUDIY9JKkdsTk0Dk7Xsh25pHelBw31kkNj8SgeE",
                        "kty": "RSA",
                        "q": "q5VM7BqNb0awocVJqfYotSxR-Y9hhReiCCrXbuwbABzmIvbXUbIAFCbN-nJZQ6WWflH0ligVzfC3pusIkvyObGWfmTbsAvE3Zmpnh5R7-ojlpq6KGupzdtdCw0WWmjw8MVvF9fCf6DQO34uFUq2VYccKoqFVy_GVvVgg3TVTP9k",
                        "d": "S3iuIlQkf0aaTbIE1Mc9RO_wfucOf3kXtU0pSXHm6ozYkZr4OQPyPbwf4dGKHO-hDFrfNo5XCK4tRGlf1LE0pbDG-851S5ENJrFwRVk6jy7743KmUjpTIctM-B5LQfJDkH4-xoatXSISwwikmZLsc8c9tkHNMEO5kM96lcG_QwXbEqV10i_ouvf_siNFwlStEXMNZaQtYguNHY5_Hsvaw4fptZZHovaEbbA_rgdpVeGHC5_p1Tqvi3528hLu939PbCxsRmMGz5f5iTo4FHAb_p-4BTQEuAwOEdQAqxmuicqMyanjuA5X6Wo4lfer_WvlYGRPf39NQ2APnnvg32GdAQ",
                        "e": "AQAB",
                        "use": "sig",
                        "kid": "key-id",
                        "qi": "IdDI-9FDFSaSgYNHd3Cok00y8qMBgxUN49d_fxLGJnGYwlKj9AJVZPci6Oq4QGiri8ouWPcQL-usAyzAO13nuM6jNseYJgn_rUZvHNTR2rubirT3PX5NV-DTsPVeI4-Oew5_3OVcR_7k6LB5aM62UV2aWTLVqxrFoxfpPtzx1DM",
                        "dp": "NsfuzqvaDIGCJB4DxgCLKI647V1vFm0QDpD4H3uP6tnVsCdm_m_tIw7QBatXJ4AlVXkJkmzdmKXTzkxE7p-SoAkd3IKhSfcMvZkrBkif-_pN-QCmJ9vCj8UDEVwIqtsq9b4jV1v-HEMSSG_X4FMVL-bW769WHR218PqJvc-gQ4E",
                        "alg": "RS256",
                        "dq": "RZLehAYoP2y8hnwG6KHl08DK5JSdCGiSzRkCDfoy4JLXRmos2PWCTqmFb_4Z7_Ie2c3FjXVnkf__l_FJosBBx_ZVBr1cab0Bp8eRf0pYEzibVkEDwvI6K4ARlDiNXrD5_1GlLZqKXI9vUHm0b9fu8JzS1SryLSYif4pSmR_cR9E",
                        "n": "gkvMF-NmBcDLa79Ny3CIk21D74PlsHz_Mzz84bjGWcIE1OJOWVQ7OLlpEv5HpiGAvr101POYb6hs1bbU8QdyiQ5t8NYU2hUIKjvBmTi_hpGXcpY_3o4nepIhBuu-iH2UPWFlLo_e5nwLSHUtWm_5P1sH1vDQ6KOWzQfrevYWOBuVJBk-AAXcCriRUywtVlPWwAAAqW0M8JKDmtkKo0zKPjsdYvvbnbG0RdbzXzBwlklWLrXSkzNHKTb6s5UhgscjbcjR9U6Ws-rArd1IjvzI8c_cleS7rIMYHOO2YYIR-9cIH9Hy-P1QTjLGNHIUrhE70XMeqmt2oPXArJKhtZl2uQ"
                    }
                ]
            }
            """;

    @Test
    public void test() throws Exception {
        final UUID userId = UUID.randomUUID();

        JWK pair = JWKSet.parse(jwksPair).getKeys().iterator().next();

        Payload _jwtPayload = new Payload(Map.of("sub", userId));
        JWSObject _jwsObject = new JWSObject(new JWSHeader.Builder(JWSAlgorithm.parse(pair.getAlgorithm().getName()))
                .keyID(pair.getKeyID())
                .type(JOSEObjectType.JWT)
                .build(),
                _jwtPayload);

        JWSSigner _signer = new RSASSASigner(pair.toRSAKey());
        _jwsObject.sign(_signer);

        String _token = _jwsObject.serialize();
        System.out.println("token = " + _token);

        JWK pub = JWKSet.parse(jwksString).getKeys().iterator().next();

        JWSObject _receivedToken = JWSObject.parse(_token);

        // Check received token
        JWSVerifier _receivedTokenVerifier = new RSASSAVerifier(pub.toRSAKey());
        if (!_receivedToken.verify(_receivedTokenVerifier)) {
            throw new RuntimeException("unverify");
        }

        String _receivedPayload = new String(_jwsObject.getPayload().toBytes());
        System.out.println("receivedPayload = " + _receivedPayload);


        if (true) return;

        RSAKey rsaKey = new RSAKeyGenerator(2048)
                .keyID("k-id")
                .algorithm(JWSAlgorithm.RS256)
                .generate();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));

        // Getting JWK for encode token
        JWKMatcher rsa256Matcher = new JWKMatcher.Builder().algorithm(JWSAlgorithm.RS256).build();
        JWKSelector rsa256JWKSelector = new JWKSelector(rsa256Matcher);
        List<JWK> jwksForEncode = jwkSource.get(rsa256JWKSelector, new SimpleSecurityContext());
        JWK jwkForEncode = jwksForEncode.iterator().next();

        // Creating & signing JWT
        Payload jwtPayload = new Payload(Map.of("sub", UUID.randomUUID()));
        JWSObject jwsObject = new JWSObject(new JWSHeader.Builder((JWSAlgorithm) jwkForEncode.getAlgorithm())
                .keyID(jwkForEncode.getKeyID())
                .type(JOSEObjectType.JWT)
                .build(),
                jwtPayload);

        JWSSigner signer = new RSASSASigner(jwkForEncode.toRSAKey());
        jwsObject.sign(signer);

        // Getting token as String
        String token = jwsObject.serialize();
        System.out.println("jwsObject.serialize() = " + token);

        // Find JWK for received token
        JWSObject receivedToken = JWSObject.parse(token);

        JWKMatcher receivedTokenMatcher = new JWKMatcher.Builder()
                .algorithm(receivedToken.getHeader().getAlgorithm())
                .keyID(receivedToken.getHeader().getKeyID())
                .build();
        JWKSelector receivedTokenSelector = new JWKSelector(receivedTokenMatcher);
        JWK receivedTokenJWK = jwkSource.get(receivedTokenSelector, new SimpleSecurityContext()).iterator().next();

        // Check received token
        JWSVerifier receivedTokenVerifier = new RSASSAVerifier(receivedTokenJWK.toRSAKey());
        if (!receivedToken.verify(receivedTokenVerifier)) {
            throw new RuntimeException("unverify");
        }

        String receivedPayload = new String(jwsObject.getPayload().toBytes());
        System.out.println("receivedPayload = " + receivedPayload);
    }

}
