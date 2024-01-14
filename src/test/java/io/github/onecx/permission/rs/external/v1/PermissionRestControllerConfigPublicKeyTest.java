package io.github.onecx.permission.rs.external.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.OK;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.permission.rs.external.v1.model.ApplicationPermissionsDTOV1;
import gen.io.github.onecx.permission.rs.external.v1.model.PermissionRequestDTOV1;
import io.github.onecx.permission.common.models.TokenConfig;
import io.github.onecx.permission.common.services.ClaimService;
import io.github.onecx.permission.rs.external.v1.controllers.PermissionRestController;
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
@TestHTTPEndpoint(PermissionRestController.class)
@WithDBData(value = "data/test-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class PermissionRestControllerConfigPublicKeyTest extends AbstractTest {

    @InjectMock
    TokenConfig tokenConfig;

    @InjectMock
    ClaimService claimService;

    @Inject
    Config config;

    @BeforeEach
    void beforeEach() {
        Mockito.when(claimService.getClaimPath()).thenReturn(new String[] { "groups" });
        var tmp = config.unwrap(SmallRyeConfig.class).getConfigMapping(TokenConfig.class);
        Mockito.when(tokenConfig.tokenClaimSeparator()).thenReturn(tmp.tokenClaimSeparator());
        Mockito.when(tokenConfig.tokenClaimPath()).thenReturn(tmp.tokenClaimPath());
        Mockito.when(tokenConfig.tokenVerified()).thenReturn(true);
        Mockito.when(tokenConfig.tokenPublicKeyLocationSuffix()).thenReturn(tmp.tokenPublicKeyLocationSuffix());
        Mockito.when(tokenConfig.tokenPublicKeyEnabled()).thenReturn(false);
    }

    @Test
    void skipTokenVerified() {

        KeycloakTestClient keycloakClient = new KeycloakTestClient();
        var accessToken = keycloakClient.getAccessToken("bob");

        var dto = given()
                .contentType(APPLICATION_JSON)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .post("/application/app1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(ApplicationPermissionsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a3");
    }
}
