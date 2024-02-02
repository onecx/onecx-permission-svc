package org.tkit.onecx.permission.common.models;

import java.util.Optional;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "onecx.permission.token")
public interface TokenConfig {

    @WithName("verified")
    boolean verified();

    @WithName("issuer.public-key-location.suffix")
    String publicKeyLocationSuffix();

    @WithName("issuer.public-key-location.enabled")
    boolean publicKeyEnabled();

    @WithName("claim.separator")
    Optional<String> claimSeparator();

    @WithName("claim.path")
    @WithDefault("realm_access/roles")
    String claimPath();
}
