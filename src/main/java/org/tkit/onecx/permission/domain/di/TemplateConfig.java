package org.tkit.onecx.permission.domain.di;

import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("onecx-permission-svc.adoc")
@ConfigMapping(prefix = "onecx.permission")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface TemplateConfig {

    /**
     * Template configuration.
     *
     * @return template configuration
     */
    @WithName("template")
    Config config();

    interface Config {
        /**
         * Role mapping for the template import
         */
        @WithName("role-mapping")
        Map<String, String> roleMapping();

        /**
         * Template import tenants
         */
        @WithName("tenants")
        @WithDefault("default")
        List<String> tenants();
    }
}
