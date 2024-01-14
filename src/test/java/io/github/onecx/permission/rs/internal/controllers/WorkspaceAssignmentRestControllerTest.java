package io.github.onecx.permission.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.permission.rs.internal.model.*;
import io.github.onecx.permission.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(WorkspaceAssignmentRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class WorkspaceAssignmentRestControllerTest extends AbstractTest {

    @Test
    void createWorkspaceAssignment() {
        // create Assignment
        var requestDTO = new CreateWorkspaceAssignmentRequestDTO();
        requestDTO.setPermissionId("wp11");
        requestDTO.setRoleId("r11");

        var uri = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().header(HttpHeaders.LOCATION);

        var dto = given()
                .contentType(APPLICATION_JSON)
                .get(uri)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(WorkspaceAssignmentDTO.class);

        assertThat(dto).isNotNull()
                .returns(requestDTO.getRoleId(), from(WorkspaceAssignmentDTO::getRoleId))
                .returns(requestDTO.getPermissionId(), from(WorkspaceAssignmentDTO::getPermissionId));

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
                .isEqualTo("createWorkspaceAssignment.createWorkspaceAssignmentRequestDTO: must not be null");

        // create Role with existing name
        requestDTO = new CreateWorkspaceAssignmentRequestDTO();
        requestDTO.setPermissionId("wp13");
        requestDTO.setRoleId("r13");

        exception = given().when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'workspace_assignment_key'  Detail: Key (tenant_id, role_id, permission_id)=(default, r13, wp13) already exists.]");

    }

    @Test
    void createWorkspaceAssignmentWrong() {
        // create Assignment
        var requestDTO = new CreateWorkspaceAssignmentRequestDTO();
        requestDTO.setPermissionId("does-not-exists");
        requestDTO.setRoleId("r11");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        requestDTO.setPermissionId("wp11");
        requestDTO.setRoleId("does-not-exists");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getNotFoundWorkspaceAssignment() {
        given()
                .contentType(APPLICATION_JSON)
                .get("does-not-exists")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void searchWorkspaceAssignmentTest() {
        var criteria = new WorkspaceAssignmentSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(WorkspaceAssignmentPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setWorkspaceId(" ");
        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(WorkspaceAssignmentPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setWorkspaceId("wapp1");

        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(WorkspaceAssignmentPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);
    }

    @Test
    void deleteWorkspaceAssignmentTest() {

        // delete Assignment
        given()
                .contentType(APPLICATION_JSON)
                .delete("DELETE_1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // check Assignment
        given()
                .contentType(APPLICATION_JSON)
                .get("wa11")
                .then()
                .statusCode(OK.getStatusCode());

        // check if Assignment does not exist
        given()
                .contentType(APPLICATION_JSON)
                .delete("wa11")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // check Assignment
        given()
                .contentType(APPLICATION_JSON)
                .get("wa11")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }
}
