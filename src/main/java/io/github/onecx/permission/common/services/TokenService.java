package io.github.onecx.permission.common.services;

import static io.github.onecx.permission.common.utils.TokenUtil.findClaimWithRoles;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;

import io.github.onecx.permission.common.models.TokenConfig;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TokenService {

    @Inject
    JWTAuthContextInfo authContextInfo;

    @Inject
    TokenConfig config;

    @Inject
    JWTParser parser;

    @Inject
    ClaimService claimService;

    public List<String> getTokenRoles(String tokenData) {
        try {
            return getRoles(tokenData);
        } catch (Exception ex) {
            throw new TokenException("Error parsing principal token", ex);
        }
    }

    private List<String> getRoles(String tokenData)
            throws JoseException, InvalidJwtException, MalformedClaimException, ParseException {

        var claimPath = claimService.getClaimPath();

        if (config.tokenVerified()) {
            var info = authContextInfo;

            // get public key location from issuer URL
            if (config.tokenPublicKeyEnabled()) {
                var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(tokenData);
                var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
                var publicKeyLocation = jwtClaims.getIssuer() + config.tokenPublicKeyLocationSuffix();
                info = new JWTAuthContextInfo(authContextInfo);
                info.setPublicKeyLocation(publicKeyLocation);
            }

            var token = parser.parse(tokenData, info);
            var first = token.getClaim(claimPath[0]);

            return findClaimWithRoles(config, first, claimPath);

        } else {

            var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(tokenData);

            var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
            var first = jwtClaims.getClaimValue(claimPath[0]);
            return findClaimWithRoles(config, first, claimPath);
        }
    }

    public static class TokenException extends RuntimeException {

        public TokenException(String message, Throwable t) {
            super(message, t);
        }
    }
}
