package io.github.onecx.permission.common.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

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

    private static final Pattern CLAIM_PATH_PATTERN = Pattern.compile("\\/(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");

    @Inject
    JWTAuthContextInfo authContextInfo;

    @Inject
    TokenConfig config;

    @Inject
    JWTParser parser;

    public List<String> getTokenRoles(String tokenData) {
        try {
            return getRoles(tokenData);
        } catch (Exception ex) {
            throw new TokenException("Error parsing principal token", ex);
        }
    }

    private List<String> getRoles(String tokenData)
            throws JoseException, InvalidJwtException, MalformedClaimException, ParseException {

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

            info.setVerifyCertificateThumbprint(false);
            var token = parser.parse(tokenData, info);

            //            return findClaimWithRoles(config, token);
            return List.of();

        } else {

            var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(tokenData);
            var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
            List<?> list = (List<?>) jwtClaims.flattenClaims().get(config.tokenClaimPath());
            return (List<String>) list;
            //            jwtClaims.flattenClaims()
            //            return findClaimWithRoles(config, jwtClaims);
            //            return List.of();
        }
    }

    private static List<String> findClaimWithRoles(TokenConfig tokenConfig, JsonObject json) {

        var path = tokenConfig.tokenClaimPath();
        Object claimValue = findClaimValue(path, json, splitClaimPath(path), 0);

        if (claimValue instanceof JsonArray) {
            return convertJsonArrayToList((JsonArray) claimValue);
        } else if (claimValue != null) {
            String sep = tokenConfig.tokenClaimSeparator().isPresent() ? tokenConfig.tokenClaimSeparator().get() : " ";
            if (claimValue.toString().isBlank()) {
                return Collections.emptyList();
            }
            return Arrays.asList(claimValue.toString().split(sep));
        } else {
            return Collections.emptyList();
        }
    }

    private static List<String> convertJsonArrayToList(JsonArray claimValue) {
        List<String> list = new ArrayList<>(claimValue.size());
        for (int i = 0; i < claimValue.size(); i++) {
            String claimValueStr = claimValue.getString(i);
            if (claimValueStr == null || claimValueStr.isBlank()) {
                continue;
            }
            list.add(claimValue.getString(i));
        }
        return list;
    }

    private static String[] splitClaimPath(String claimPath) {
        return claimPath.indexOf('/') > 0 ? CLAIM_PATH_PATTERN.split(claimPath) : new String[] { claimPath };
    }

    private static Object findClaimValue(String claimPath, JsonObject json, String[] pathArray, int step) {
        Object claimValue = json.getValue(pathArray[step].replace("\"", ""));
        if (claimValue == null) {
            log.debug("No claim exists at the path '{}' at the path segment '{}'", claimPath, pathArray[step]);
        } else if (step + 1 < pathArray.length) {
            if (claimValue instanceof JsonObject) {
                int nextStep = step + 1;
                return findClaimValue(claimPath, (JsonObject) claimValue, pathArray, nextStep);
            } else {
                log.debug("Claim value at the path '{}' is not a json object", claimPath);
            }
        }

        return claimValue;
    }

    public static class TokenException extends RuntimeException {

        public TokenException(String message) {
            super(message);
        }

        public TokenException(String message, Throwable t) {
            super(message, t);
        }
    }
}
