package org.tkit.onecx.permission.rs.external.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.tkit.onecx.permission.rs.external.v1.controllers.PermissionRestController;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.rs.external.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(PermissionRestController.class)
@WithDBData(value = "data/test-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-pm:read" })
class PermissionRestControllerTest extends AbstractTest {

    @Test
    void getApplicationPermissionsTest() {

        // bearer prefix
        var accessToken = createAccessTokenBearer(USER_ALICE);

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .pathParam("productName", "test1")
                .pathParam("appId", "app1")
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(ApplicationPermissionsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a3");

        // without bearer prefix
        accessToken = createAccessToken(USER_ALICE);

        dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .pathParam("productName", "test1")
                .pathParam("appId", "app1")
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(ApplicationPermissionsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a3");

    }

    private static Stream<Arguments> badRequestData() {
        return Stream.of(
                Arguments.of("test1", "app1", "getApplicationPermissions.permissionRequestDTOV1: must not be null"));
    }

    @ParameterizedTest
    @MethodSource("badRequestData")
    void getApplicationPermissionsNoBodyTest(String productName, String appId, String check) {

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("productName", productName)
                .pathParam("appId", appId)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTOV1.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo(check);

    }

    @Test
    void getApplicationPermissionsWrongTongTest() {

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(new PermissionRequestDTOV1().token("this-is-not-token"))
                .pathParam("productName", "test1")
                .pathParam("appId", "app1")
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

}
