package org.tkit.onecx.permission.common.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.permission.common.models.TokenConfig;
import org.tkit.onecx.permission.test.AbstractTest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
class TokenServiceTest extends AbstractTest {

    @Inject
    TokenService tokenService;

    @InjectMock
    TokenConfig tokenConfig;

    @Inject
    Config config;

    @Test
    void getTokenRolesWithoutDefaultRoles() {
        var token = createToken("org1");

        var roles = tokenService.getTokenRoles(token);

        assertThat(roles).isEmpty();
    }

    @BeforeEach
    void beforeEach() {
        var tmp = config.unwrap(SmallRyeConfig.class).getConfigMapping(TokenConfig.class);

        var c = new TokenConfig.Config() {

            @Override
            public boolean verified() {
                return false;
            }

            @Override
            public String publicKeyLocationSuffix() {
                return tmp.config().publicKeyLocationSuffix();
            }

            @Override
            public boolean publicKeyEnabled() {
                return false;
            }

            @Override
            public Optional<String> claimSeparator() {
                return Optional.empty();
            }

            @Override
            public String claimPath() {
                return tmp.config().claimPath();
            }
        };

        Mockito.when(tokenConfig.config()).thenReturn(c);
        Mockito.when(tokenConfig.defaultRoles()).thenReturn(Optional.empty());
    }

}
