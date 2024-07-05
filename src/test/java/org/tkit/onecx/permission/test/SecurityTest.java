package org.tkit.onecx.permission.test;

import java.util.List;

import org.tkit.quarkus.security.test.AbstractSecurityTest;
import org.tkit.quarkus.security.test.SecurityTestConfig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecurityTest extends AbstractSecurityTest {
    @Override
    public SecurityTestConfig getConfig() {
        SecurityTestConfig config = new SecurityTestConfig();
        config.addConfig("read", "/internal/permissions/id", 404, List.of("ocx-pm:read"), "get");
        config.addConfig("write", "/internal/permissions", 400, List.of("ocx-pm:write"), "post");
        return config;
    }
}
