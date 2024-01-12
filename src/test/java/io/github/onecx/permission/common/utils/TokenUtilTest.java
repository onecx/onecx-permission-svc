package io.github.onecx.permission.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import jakarta.json.Json;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.Test;

import io.github.onecx.permission.common.models.TokenConfig;
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TokenUtilTest extends AbstractTest {

    @Test
    void tokenUtilityTest() {

        var config = new TokenConfig() {
            @Override
            public boolean tokenVerified() {
                return false;
            }

            @Override
            public String tokenPublicKeyLocationSuffix() {
                return null;
            }

            @Override
            public boolean tokenPublicKeyEnabled() {
                return false;
            }

            @Override
            public Optional<String> tokenClaimSeparator() {
                return Optional.empty();
            }

            @Override
            public String tokenClaimPath() {
                return null;
            }
        };

        var tmp = TokenUtil.findClaimWithRoles(config, null, new String[] { "test" });
        assertThat(tmp).isNotNull().isEmpty();

        var value = Json.createValue(32);
        tmp = TokenUtil.findClaimWithRoles(config, value, new String[] { "test1", "test2" });
        assertThat(tmp).isNotNull().containsExactly("32");

        JsonValue emptyValue = new JsonValue() {
            @Override
            public ValueType getValueType() {
                return null;
            }

            @Override
            public String toString() {
                return " ";
            }
        };

        tmp = TokenUtil.findClaimWithRoles(config, emptyValue, new String[] { "test1", "test2" });
        assertThat(tmp).isNotNull().isEmpty();

        var list = Json.createArrayBuilder();
        list.add("s1");
        list.add(" ");
        list.add("");

        tmp = TokenUtil.findClaimWithRoles(config, list.build(), new String[] { "test1", "test2" });
        assertThat(tmp).isNotNull().containsExactly("s1");
    }
}
