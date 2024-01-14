package io.github.onecx.permission.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.permission.rs.internal.model.*;
import io.github.onecx.permission.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(WorkspacePermissionRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class WorkspacePermissionRestControllerTest extends AbstractTest {

    @Test
    void searchTest() {
        var criteria = new WorkspacePermissionSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(WorkspacePermissionPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(7);
        assertThat(data.getStream()).isNotNull().hasSize(7);

        criteria.setWorkspaceId(" ");

        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(WorkspacePermissionPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(7);
        assertThat(data.getStream()).isNotNull().hasSize(7);
    }

    @Test
    void searchCriteriaTest() {
        var criteria = new WorkspacePermissionSearchCriteriaDTO();
        criteria.setWorkspaceId("wapp1");

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(WorkspacePermissionPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(5);
        assertThat(data.getStream()).isNotNull().hasSize(5);
    }

    @Test
    void searchNoBodyTest() {
        var exception = given()
                .contentType(APPLICATION_JSON)
                .post("search")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTO.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail())
                .isEqualTo("searchWorkspacePermissions.workspacePermissionSearchCriteriaDTO: must not be null");
    }

    @Test
    void deleteWorkspacePermissionTest() {

        // delete Role
        given()
                .contentType(APPLICATION_JSON)
                .delete("DELETE_1")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // check if Role exists
        given()
                .contentType(APPLICATION_JSON)
                .get("DELETE_1")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // delete Role in portal
        given()
                .contentType(APPLICATION_JSON)
                .delete("wp21")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

    }

    @Test
    void getWorkspacePermissionTest() {

        var dto = given()
                .contentType(APPLICATION_JSON)
                .get("wp21")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(WorkspacePermissionDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getWorkspaceId()).isEqualTo("wapp2");
        assertThat(dto.getId()).isEqualTo("wp21");

        given()
                .contentType(APPLICATION_JSON)
                .get("___")
                .then().statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .get("wp11")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(WorkspacePermissionDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getWorkspaceId()).isEqualTo("wapp1");
        assertThat(dto.getId()).isEqualTo("wp11");

    }

    @Test
    void createNewWorkspacePermissionTest() {

        // create Role
        var requestDTO = new CreateWorkspacePermissionRequestDTO();
        requestDTO.setWorkspaceId("w1");
        requestDTO.setAction("a1");
        requestDTO.setResource("r1");
        requestDTO.setDescription("d1");

        var uri = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post()
                .then().statusCode(CREATED.getStatusCode())
                .extract().header(HttpHeaders.LOCATION);

        var dto = given()
                .contentType(APPLICATION_JSON)
                .get(uri)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(WorkspacePermissionDTO.class);

        assertThat(dto).isNotNull()
                .returns(requestDTO.getAction(), from(WorkspacePermissionDTO::getAction))
                .returns(requestDTO.getResource(), from(WorkspacePermissionDTO::getResource))
                .returns(requestDTO.getWorkspaceId(), from(WorkspacePermissionDTO::getWorkspaceId))
                .returns(requestDTO.getDescription(), from(WorkspacePermissionDTO::getDescription));

        // create Role without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail())
                .isEqualTo("createWorkspacePermission.createWorkspacePermissionRequestDTO: must not be null");

        // create WorkspacePermission with existing data
        requestDTO = new CreateWorkspacePermissionRequestDTO();
        requestDTO.setWorkspaceId("wapp1");
        requestDTO.setAction("a1");
        requestDTO.setResource("o1");
        requestDTO.setDescription("d1");

        exception = given().when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'workspace_permission_key'  Detail: Key (tenant_id, workspace_id, resource, action)=(default, wapp1, o1, a1) already exists.]");
    }

    @Test
    void updateWorkspacePermissionWithoutBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .when()
                .put("update_create_new")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name(), exception.getErrorCode());
        Assertions.assertEquals("updateWorkspacePermission.updateWorkspacePermissionRequestDTO: must not be null",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertEquals(1, exception.getInvalidParams().size());
    }

    @Test
    void updateWorkspacePermissionTest() {

        var dto = given()
                .contentType(APPLICATION_JSON)
                .get("wp11")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(WorkspacePermissionDTO.class);

        var request = new UpdateWorkspacePermissionRequestDTO();
        request.setModificationCount(dto.getModificationCount());
        request.setDescription("description123");

        given()
                .contentType(APPLICATION_JSON)
                .when()
                .body(request)
                .put("wp11")
                .then()
                .statusCode(OK.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .get("wp11")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(WorkspacePermissionDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo(request.getDescription());
        assertThat(dto.getId()).isEqualTo("wp11");

    }

    @Test
    void updateWorkspacePermissionNotFoundTest() {

        var request = new UpdateWorkspacePermissionRequestDTO();
        request.setModificationCount(1);
        request.setDescription("description123");

        given()
                .contentType(APPLICATION_JSON)
                .when()
                .body(request)
                .put("does-not-exists")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }
}
