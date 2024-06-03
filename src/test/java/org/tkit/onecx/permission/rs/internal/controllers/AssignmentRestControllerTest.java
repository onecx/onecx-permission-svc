package org.tkit.onecx.permission.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;
import static org.jboss.resteasy.reactive.RestResponse.Status.BAD_REQUEST;

import java.util.List;

import jakarta.ws.rs.core.HttpHeaders;

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
        var requestDTO = new CreateAssignmentRequestDTO();
        requestDTO.setPermissionId("p11");
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
                .body().as(AssignmentDTO.class);

        assertThat(dto).isNotNull()
                .returns(requestDTO.getRoleId(), from(AssignmentDTO::getRoleId))
                .returns(requestDTO.getPermissionId(), from(AssignmentDTO::getPermissionId));

        // create Role without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo("createAssignment.createAssignmentRequestDTO: must not be null");

        // create Role with existing name
        requestDTO = new CreateAssignmentRequestDTO();
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
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'uc_assignment_key'  Detail: Key (permission_id, role_id, tenant_id)=(p13, r13, default) already exists.]");

    }

    @Test
    void createAssignmentWrong() {
        // create Assignment
        var requestDTO = new CreateAssignmentRequestDTO();
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

        criteria.setAppIds(List.of("  "));

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

        criteria2.setAppIds(List.of("app1"));

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
        multipleAppIdsCriteria.appIds(List.of("app1", "app2", ""));

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

        // try to delete mandatory assignment
        given()
                .contentType(APPLICATION_JSON)
                .delete("a13")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // check Assignment
        given()
                .contentType(APPLICATION_JSON)
                .get("a13")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void grantAssignmentByRole() {
        // create role assignment
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("/grant/role1")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("/grant/r14")
                .then()
                .statusCode(CREATED.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("/grant/r14")
                .then()
                .statusCode(CREATED.getStatusCode());

        var idToken = createToken("org1", List.of("n3-100"));
        given()
                .when()
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .post("/grant/r14")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void grantAssignmentByRoleProduct() {

        // create role assignment
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("/grant/role1/product")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductAssignmentRequestDTO())
                .post("/grant/role1/product")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductAssignmentRequestDTO().productName(null).appId(null))
                .post("/grant/role1/product")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductAssignmentRequestDTO()
                        .productName("does-not-exists").appId("app1"))
                .post("/grant/r14/product")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductAssignmentRequestDTO()
                        .productName("test1").appId("app1"))
                .post("/grant/does-not-exists/product")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductAssignmentRequestDTO()
                        .productName("test1").appId("app1"))
                .post("/grant/r14/product")
                .then()
                .statusCode(CREATED.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductAssignmentRequestDTO()
                        .productName("test1").appId("app1"))
                .post("/grant/r14/product")
                .then()
                .statusCode(CREATED.getStatusCode());

    }

    @Test
    void grantAssignmentByRoleProducts() {

        // create role assignment
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("/grant/role1/products")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductsAssignmentRequestDTO())
                .post("/grant/role1/products")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductsAssignmentRequestDTO().productNames(List.of()))
                .post("/grant/role1/products")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("does-not-exists")))
                .post("/grant/r14/products")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("test1")))
                .post("/grant/does-not-exists/products")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("test1")))
                .post("/grant/r14/products")
                .then()
                .statusCode(CREATED.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("test1")))
                .post("/grant/r14/products")
                .then()
                .statusCode(CREATED.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new CreateRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("test1", "does-not-exists")))
                .post("/grant/r14/products")
                .then()
                .statusCode(CREATED.getStatusCode());

    }

    @Test
    void revokeAssignmentByRole() {
        // revoke role assignment
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("/revoke/role1")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("/revoke/r14")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        var idToken = createToken("org1", List.of("n3-100"));
        given()
                .when()
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .post("/revoke/r14")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void revokeAssignmentByRoleProduct() {

        // revoke role assignment
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("/revoke/role1/product")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductAssignmentRequestDTO())
                .post("/revoke/role1/product")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductAssignmentRequestDTO().productName(null).appId(null))
                .post("/revoke/role1/product")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductAssignmentRequestDTO()
                        .productName("does-not-exists").appId("app1"))
                .post("/revoke/r14/product")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductAssignmentRequestDTO()
                        .productName("test1").appId("app1"))
                .post("/revoke/does-not-exists/product")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductAssignmentRequestDTO()
                        .productName("test1").appId("app1"))
                .post("/revoke/r14/product")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductAssignmentRequestDTO()
                        .productName("test1").appId("app1"))
                .post("/revoke/r14/product")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

    }

    @Test
    void revokeAssignmentByRoleProducts() {

        // create role assignment
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("/revoke/role1/products")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductsAssignmentRequestDTO())
                .post("/revoke/role1/products")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductsAssignmentRequestDTO().productNames(List.of()))
                .post("/revoke/role1/products")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("does-not-exists")))
                .post("/revoke/r14/products")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("test1")))
                .post("/revoke/does-not-exists/products")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("test1")))
                .post("/revoke/r14/products")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("test1")))
                .post("/revoke/r14/products")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new RevokeRoleProductsAssignmentRequestDTO()
                        .productNames(List.of("test1", "does-not-exists")))
                .post("/revoke/r14/products")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

    }
}
