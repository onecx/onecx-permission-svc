package io.github.onecx.permission.common.services;

import java.util.regex.Pattern;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.github.onecx.permission.common.models.TokenConfig;

@ApplicationScoped
public class ClaimService {

    private static final Pattern CLAIM_PATH_PATTERN = Pattern.compile("\\/(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");

    private static String[] claimPath;

    @Inject
    TokenConfig config;

    @PostConstruct
    public void init() {
        claimPath = splitClaimPath(config.tokenClaimPath());
    }

    public String[] getClaimPath() {
        return claimPath;
    }

    static String[] splitClaimPath(String claimPath) {
        return claimPath.indexOf('/') > 0 ? CLAIM_PATH_PATTERN.split(claimPath) : new String[] { claimPath };
    }
}
