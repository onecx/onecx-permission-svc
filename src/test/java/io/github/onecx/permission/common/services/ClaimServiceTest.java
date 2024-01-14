package io.github.onecx.permission.common.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ClaimServiceTest extends AbstractTest {

    @Test
    void claimPathTest() {

        var out = ClaimService.splitClaimPath("realms/roles");
        assertThat(out).isNotNull().hasSize(2).containsExactly("realms", "roles");

        out = ClaimService.splitClaimPath("groups");
        assertThat(out).isNotNull().hasSize(1);
    }
}
