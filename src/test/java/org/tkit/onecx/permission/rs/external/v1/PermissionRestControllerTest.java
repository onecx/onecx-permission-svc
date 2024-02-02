package org.tkit.onecx.permission.rs.external.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.tkit.onecx.permission.rs.external.v1.controllers.PermissionRestController;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.rs.external.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(PermissionRestController.class)
@WithDBData(value = "data/test-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class PermissionRestControllerTest extends AbstractTest {

    @Test
    void getApplicationPermissionsTest() {

        var accessToken = createToken(List.of("n3"));

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

    private static Stream<Arguments> badRequestData() {
        return Stream.of(
                Arguments.of("/application/app1", "getApplicationPermissions.permissionRequestDTOV1: must not be null"),
                Arguments.of("/workspace/wapp1", "getWorkspacePermission.permissionRequestDTOV1: must not be null"),
                Arguments.of("/workspace/wapp1/applications",
                        "getWorkspacePermissionApplications.permissionRequestDTOV1: must not be null"));
    }

    @ParameterizedTest
    @MethodSource("badRequestData")
    void getApplicationPermissionsNoBodyTest(String post, String check) {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .post(post)
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
                .contentType(APPLICATION_JSON)
                .body(new PermissionRequestDTOV1().token("this-is-not-token"))
                .post("/application/app1")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    void getWorkspacePermissionsTest() {

        var accessToken = createToken(List.of("n3"));

        var dto = given()
                .contentType(APPLICATION_JSON)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .post("/workspace/wapp1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(WorkspacePermissionsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a3");

    }

    @Test
    void getWorkspacePermissionApplicationsTest() {

        var accessToken = createToken(List.of("n3"));

        var dto = given()
                .contentType(APPLICATION_JSON)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .post("/workspace/wapp1/applications")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(WorkspacePermissionApplicationsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getWorkspace()).isNotNull();
        assertThat(dto.getWorkspace().getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getWorkspace().getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a3");
        assertThat(dto.getApplications()).isNotNull().hasSize(1);
        assertThat(dto.getApplications().get(0)).isNotNull();
        assertThat(dto.getApplications().get(0).getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getApplications().get(0).getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a3");
    }
}
