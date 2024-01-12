package io.github.onecx.permission.rs.external.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.BAD_REQUEST;
import static org.jboss.resteasy.reactive.RestResponse.Status.OK;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.permission.rs.external.v1.model.*;
import io.github.onecx.permission.rs.external.v1.controllers.PermissionRestController;
import io.github.onecx.permission.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(PermissionRestController.class)
@WithDBData(value = "data/test-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class PermissionRestControllerTest extends AbstractTest {

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

    @Test
    void getApplicationPermissionsNoBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .post("/application/app1")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTOV1.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo("getApplicationPermissions.permissionRequestDTOV1: must not be null");

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
    void getWorkspacePermissionsNoBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .post("/workspace/wapp1")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTOV1.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo("getWorkspacePermission.permissionRequestDTOV1: must not be null");

    }

    @Test
    void getWorkspacePermissionAppsNoBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .post("/workspace/wapp1/applications")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTOV1.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail())
                .isEqualTo("getWorkspacePermissionApplications.permissionRequestDTOV1: must not be null");

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
