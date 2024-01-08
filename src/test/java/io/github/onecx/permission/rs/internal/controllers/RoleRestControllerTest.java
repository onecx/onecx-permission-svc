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
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(RoleRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class RoleRestControllerTest extends AbstractTest {

    @Test
    void createNewRoleTest() {

        // create Role
        var RoleDto = new CreateRoleRequestDTO();
        RoleDto.setName("test01");
        RoleDto.setDescription("description");

        var uri = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(RoleDto)
                .post()
                .then().statusCode(CREATED.getStatusCode())
                .extract().header(HttpHeaders.LOCATION);

        var dto = given()
                .contentType(APPLICATION_JSON)
                .get(uri)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(RoleDTO.class);

        assertThat(dto).isNotNull()
                .returns(RoleDto.getName(), from(RoleDTO::getName))
                .returns(RoleDto.getDescription(), from(RoleDTO::getDescription));

        // create Role without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createRole.createRoleRequestDTO: must not be null");

        // create Role with existing name
        RoleDto = new CreateRoleRequestDTO();
        RoleDto.setName("n1");

        exception = given().when()
                .contentType(APPLICATION_JSON)
                .body(RoleDto)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'tenant_name'  Detail: Key (tenant_id, name)=(default, n1) already exists.]");
    }

    @Test
    void deleteRoleTest() {

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
                .delete("r11")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

    }

    @Test
    void getRoleByIdTest() {

        var dto = given()
                .contentType(APPLICATION_JSON)
                .get("r12")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(RoleDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("n2");
        assertThat(dto.getId()).isEqualTo("r12");

        given()
                .contentType(APPLICATION_JSON)
                .get("___")
                .then().statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .get("r11")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(RoleDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("n1");
        assertThat(dto.getId()).isEqualTo("r11");

    }

    @Test
    void searchRolesTest() {
        var criteria = new RoleSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(RolePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(4);
        assertThat(data.getStream()).isNotNull().hasSize(4);

        criteria.setName(" ");
        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(RolePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(4);
        assertThat(data.getStream()).isNotNull().hasSize(4);

        criteria.setName("n3");
        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(RolePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);

    }

    @Test
    void updateRoleTest() {

        // update none existing Role
        var RoleDto = new UpdateRoleRequestDTO();
        RoleDto.setName("test01");
        RoleDto.setDescription("description-update");

        given()
                .contentType(APPLICATION_JSON)
                .body(RoleDto)
                .when()
                .put("does-not-exists")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // update Role
        given()
                .contentType(APPLICATION_JSON)
                .body(RoleDto)
                .when()
                .put("r11")
                .then().log().all()
                .statusCode(OK.getStatusCode());

        // download Role
        var dto = given().contentType(APPLICATION_JSON)
                .body(RoleDto)
                .when()
                .get("r11")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(RoleDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo(RoleDto.getDescription());

    }

    @Test
    void updateRoleWithExistingNameTest() {

        var dto = new UpdateRoleRequestDTO();
        dto.setName("n3");
        dto.setDescription("description");

        var exception = given()
                .contentType(APPLICATION_JSON)
                .when()
                .body(dto)
                .put("r11")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("MERGE_ENTITY_FAILED", exception.getErrorCode());
        Assertions.assertEquals(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'tenant_name'  Detail: Key (tenant_id, name)=(default, n3) already exists.]",
                exception.getDetail());
        Assertions.assertNull(exception.getInvalidParams());

    }

    @Test
    void updateRoleWithoutBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "update_create_new")
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
        Assertions.assertEquals("updateRole.updateRoleRequestDTO: must not be null",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertEquals(1, exception.getInvalidParams().size());
    }
}
