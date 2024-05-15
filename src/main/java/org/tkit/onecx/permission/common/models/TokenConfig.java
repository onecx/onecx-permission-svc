package org.tkit.onecx.permission.common.models;

import java.util.Optional;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigDocFilename("onecx-permission-svc.adoc")
@ConfigMapping(prefix = "onecx.permission.token")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface TokenConfig {

    /**
     * Verified permission token
     */
    @WithName("verified")
    boolean verified();

    /**
     * Issuer public key location suffix.
     */
    @WithName("issuer.public-key-location.suffix")
    @WithDefault("/protocol/openid-connect/certs")
    String publicKeyLocationSuffix();

    /**
     * Issuer public key location enabled
     */
    @WithName("issuer.public-key-location.enabled")
    boolean publicKeyEnabled();

    /**
     * Claim separator
     */
    @WithName("claim.separator")
    Optional<String> claimSeparator();

    /**
     * Claim path
     */
    @WithName("claim.path")
    @WithDefault("realm_access/roles")
    String claimPath();
}
