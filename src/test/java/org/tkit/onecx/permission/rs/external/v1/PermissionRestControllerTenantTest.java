package org.tkit.onecx.permission.rs.external.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.rs.external.v1.controllers.PermissionRestController;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.rs.external.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(PermissionRestController.class)
@WithDBData(value = "data/test-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class PermissionRestControllerTenantTest extends AbstractTest {

    @Test
    void getApplicationPermissionsTest() {

        var accessToken = createToken("org1", List.of("n3-100"));

        var dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, accessToken)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .post("/application/app1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(ApplicationPermissionsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a2");

    }

    @Test
    void getWorkspacePermissionsTest() {

        var accessToken = createToken("org1", List.of("n3-100"));

        var dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, accessToken)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .post("/workspace/wapp1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(WorkspacePermissionsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getPermissions()).isNotNull().isEmpty();

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, accessToken)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .post("/workspace/wapp1-100")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(WorkspacePermissionsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a1");

    }

    @Test
    void getWorkspacePermissionApplicationsTest() {

        var accessToken = createToken("org1", List.of("n3-100"));

        var dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, accessToken)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .post("/workspace/wapp1/applications")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(WorkspacePermissionApplicationsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getWorkspace()).isNotNull();
        assertThat(dto.getWorkspace().getPermissions()).isNotNull().isEmpty();

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, accessToken)
                .body(new PermissionRequestDTOV1().token(accessToken))
                .post("/workspace/wapp1-100/applications")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(WorkspacePermissionApplicationsDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getWorkspace()).isNotNull();
        assertThat(dto.getWorkspace().getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getWorkspace().getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a1");
        assertThat(dto.getApplications()).isNotNull().hasSize(1);
        assertThat(dto.getApplications().get(0)).isNotNull();
        assertThat(dto.getApplications().get(0).getPermissions()).isNotNull().hasSize(1);
        assertThat(dto.getApplications().get(0).getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a2");
    }
}
