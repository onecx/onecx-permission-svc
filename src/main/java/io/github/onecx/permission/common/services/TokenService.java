package io.github.onecx.permission.common.services;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.tkit.quarkus.rs.context.token.TokenClaimUtility;
import org.tkit.quarkus.rs.context.token.TokenParserRequest;
import org.tkit.quarkus.rs.context.token.TokenParserService;

import io.github.onecx.permission.common.models.TokenConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TokenService {

    @Inject
    TokenConfig config;

    @Inject
    ClaimService claimService;

    @Inject
    TokenParserService tokenParserService;

    public List<String> getTokenRoles(String tokenData) {

        try {

            var request = new TokenParserRequest(tokenData)
                    .verify(config.verified())
                    .issuerEnabled(config.publicKeyEnabled())
                    .issuerSuffix(config.publicKeyLocationSuffix());

            var permissionToken = tokenParserService.parseToken(request);
            var path = claimService.getClaimPath();
            return TokenClaimUtility.findClaimStringList(permissionToken, path, config.claimSeparator().orElse(" "));

        } catch (Exception ex) {
            throw new TokenException("Error parsing permission token", ex);
        }
    }

    public static class TokenException extends RuntimeException {

        public TokenException(String message, Throwable t) {
            super(message, t);
        }
    }
}
