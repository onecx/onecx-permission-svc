package io.github.onecx.permission.common.models;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "onecx.permission")
public interface TokenConfig {

    @WithName("token.verified")
    boolean tokenVerified();

    @WithName("token.issuer.public-key-location.suffix")
    String tokenPublicKeyLocationSuffix();

    @WithName("token.issuer.public-key-location.enabled")
    boolean tokenPublicKeyEnabled();

    @WithName("token.claim.separator")
    @WithDefault(" ")
    String tokenClaimSeparator();

    @WithName("token.claim.path")
    @WithDefault("realm_access/roles")
    String tokenClaimPath();
}
