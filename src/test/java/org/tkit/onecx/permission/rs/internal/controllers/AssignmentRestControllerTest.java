package org.tkit.onecx.permission.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;
import static org.jboss.resteasy.reactive.RestResponse.Status.BAD_REQUEST;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.rs.internal.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(AssignmentRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class AssignmentRestControllerTest extends AbstractTest {

    @Test
    void createAssignment() {
        // create Assignment
        var requestDTO = new CreateRevokeAssignmentRequestDTO();
        requestDTO.setPermissionId("p11");
        requestDTO.setRoleId("r11");

        var uri = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().as(CreateAssignmentResponseDTO.class);

        assertThat(uri).isNotNull();
        assertThat(uri.getAssignments().get(0).getRoleId()).isEqualTo(requestDTO.getRoleId());
        assertThat(uri.getAssignments().get(0).getPermissionId()).isEqualTo(requestDTO.getPermissionId());

        // create Role without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo("createAssignment.createRevokeAssignmentRequestDTO: must not be null");

        // create Role with existing name
        requestDTO = new CreateRevokeAssignmentRequestDTO();
        requestDTO.setPermissionId("p13");
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
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'assignment_key'  Detail: Key (tenant_id, role_id, permission_id)=(default, r13, p13) already exists.]");

    }

    @Test
    void createAssignmentWrong() {
        // create Assignment
        var requestDTO = new CreateRevokeAssignmentRequestDTO();
        requestDTO.setPermissionId("does-not-exists");
        requestDTO.setRoleId("r11");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        requestDTO.setPermissionId("p11");
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
    void batchCreateAssignmentsTest() {
        // create Assignment
        var requestDTO = new CreateRevokeAssignmentRequestDTO();
        requestDTO.setRoleId("r14");
        requestDTO.setAppId(List.of("app1", "app2"));

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().as(CreateAssignmentResponseDTO.class);
        assertThat(output.getAssignments()).hasSize(7);

        //should return not-found when no permission-id and app-ids are set
        var invalidRequestDTO = new CreateRevokeAssignmentRequestDTO();
        invalidRequestDTO.setRoleId("r12");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(invalidRequestDTO)
                .post()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //should return not-found when no permissions with given appId exists

        var request = new CreateRevokeAssignmentRequestDTO();
        request.setRoleId("r12");
        request.setAppId(List.of("randomAppId"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void revokeAssignmentsByOnlyRoleIdTest() {
        var requestDTO = new CreateRevokeAssignmentRequestDTO();
        requestDTO.roleId("r14");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post("/revoke")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        //check if assignment is gone
        given()
                .when()
                .get("a12")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //not-exiting role id
        requestDTO.setRoleId("not-existing");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post("/revoke")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void revokeAssignmentsByRoleIdAndPermissionIdTest() {
        var requestDTO = new CreateRevokeAssignmentRequestDTO();
        requestDTO.roleId("r14");
        requestDTO.permissionId("p13");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post("/revoke")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        //check if assignment is gone
        given()
                .when()
                .get("a12")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //not-existing permissionId
        requestDTO.setPermissionId("not-existing");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post("/revoke")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void revokeAssignmentsByRoleIdAndAppIdsTest() {
        var requestDTO = new CreateRevokeAssignmentRequestDTO();
        requestDTO.roleId("r14");
        requestDTO.appId(List.of("app1"));
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post("/revoke")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        //check if assignment is gone
        given()
                .when()
                .get("a12")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //not-existing appIds
        requestDTO.setAppId(List.of("not-existing"));
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestDTO)
                .post("/revoke")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getNotFoundAssignment() {
        given()
                .contentType(APPLICATION_JSON)
                .get("does-not-exists")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void searchAssignmentTest() {
        var criteria = new AssignmentSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AssignmentPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setAppId(List.of("  "));

        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AssignmentPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        var criteria2 = new AssignmentSearchCriteriaDTO();

        criteria2.setAppId(List.of("app1"));

        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria2)
                .post("/search")
                .then().log().all()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AssignmentPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);

        //get by multiple appIds
        var multipleAppIdsCriteria = new AssignmentSearchCriteriaDTO();
        multipleAppIdsCriteria.appId(List.of("app1", "app2", ""));

        var multipleAppIdsResult = given()
                .contentType(APPLICATION_JSON)
                .body(multipleAppIdsCriteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AssignmentPageResultDTO.class);

        assertThat(multipleAppIdsResult).isNotNull();
        assertThat(multipleAppIdsResult.getTotalElements()).isEqualTo(3);
        assertThat(multipleAppIdsResult.getStream()).isNotNull().hasSize(3);

    }

    @Test
    void deleteAssignmentTest() {

        // delete Assignment
        given()
                .contentType(APPLICATION_JSON)
                .delete("DELETE_1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // check Assignment
        given()
                .contentType(APPLICATION_JSON)
                .get("a11")
                .then()
                .statusCode(OK.getStatusCode());

        // check if Assignment does not exist
        given()
                .contentType(APPLICATION_JSON)
                .delete("a11")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // check Assignment
        given()
                .contentType(APPLICATION_JSON)
                .get("a11")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }
}
