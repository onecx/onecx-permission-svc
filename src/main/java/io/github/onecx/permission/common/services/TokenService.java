package io.github.onecx.permission.common.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;

import io.github.onecx.permission.common.models.TokenConfig;
import io.smallrye.jwt.JsonUtils;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TokenService {

    private static final Pattern CLAIM_PATH_PATTERN = Pattern.compile("\\/(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");

    private static final String[] CLAIM_PATH = splitClaimPath(
            ConfigProvider.getConfig().getValue("onecx.permission.token.claim.path", String.class));

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

            var token = parser.parse(tokenData, info);
            var first = (JsonValue) token.getClaim(CLAIM_PATH[0]);
            return findClaimWithRoles(config, first, CLAIM_PATH);

        } else {

            var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(tokenData);

            var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
            var tmp = jwtClaims.getClaimValue(CLAIM_PATH[0]);
            var first = replaceClaimValueWithJsonValue(tmp);
            return findClaimWithRoles(config, first, CLAIM_PATH);
        }
    }

    private JsonValue replaceClaimValueWithJsonValue(Object value) {
        if (value instanceof String) {
            return Json.createValue((String) value);
        }
        return JsonUtils.wrapValue(value);
    }

    private static List<String> findClaimWithRoles(TokenConfig config, JsonValue first, String[] path) {

        JsonValue claimValue = findClaimValue(first, path, 1);

        if (claimValue instanceof JsonArray) {
            return convertJsonArrayToList((JsonArray) claimValue);
        } else if (claimValue != null) {
            if (claimValue.toString().isBlank()) {
                return Collections.emptyList();
            }
            return Arrays.asList(claimValue.toString().split(config.tokenClaimSeparator()));
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

    private static JsonValue findClaimValue(JsonValue json, String[] pathArray, int step) {
        if (json == null) {
            log.debug("No claim exists at the path '{}' at the path segment '{}'", pathArray, pathArray[step]);
            return null;
        }

        if (step < pathArray.length) {
            if (json instanceof JsonObject) {
                JsonValue claimValue = json.asJsonObject().get(pathArray[step].replace("\"", ""));
                return findClaimValue(claimValue, pathArray, step + 1);
            } else {
                log.debug("Claim value at the path '{}' is not a json object. Step: {}", pathArray, step);
            }
        }
        return json;
    }

    public static class TokenException extends RuntimeException {

        public TokenException(String message, Throwable t) {
            super(message, t);
        }
    }
}
