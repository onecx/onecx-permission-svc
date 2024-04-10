package org.tkit.onecx.permission.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.rs.internal.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(PermissionRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class PermissionRestControllerTest extends AbstractTest {

    @Test
    void searchTest() {
        var criteria = new PermissionSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(PermissionPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(7);
        assertThat(data.getStream()).isNotNull().hasSize(7);

        criteria.setAppId(" ");

        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(PermissionPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(7);
        assertThat(data.getStream()).isNotNull().hasSize(7);
    }

    @Test
    void searchCriteriaTest() {
        var criteria = new PermissionSearchCriteriaDTO();
        criteria.setAppId("app1");

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(PermissionPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(5);
        assertThat(data.getStream()).isNotNull().hasSize(5);

        var productNamesCriteria = new PermissionSearchCriteriaDTO();
        productNamesCriteria.setProductNames(List.of("test1"));
        var output = given()
                .contentType(APPLICATION_JSON)
                .body(productNamesCriteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(PermissionPageResultDTO.class);

        assertThat(output).isNotNull();
        assertThat(output.getTotalElements()).isEqualTo(7);
        assertThat(output.getStream()).isNotNull().hasSize(7);

    }

    @Test
    void searchNoBodyTest() {
        var exception = given()
                .contentType(APPLICATION_JSON)
                .post("/search")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTO.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo("searchPermissions.permissionSearchCriteriaDTO: must not be null");
    }

    @Test
    void createPermissionTest() {
        var criteria = new CreatePermissionRequestDTO();
        criteria.setAppId("app1");
        criteria.setProductName("productName");
        criteria.setAction("SEARCH");

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());

        assertThat(data).isNotNull();

        var exception = given()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTO.class);

        assertThat(exception).isNotNull();
    }

    @Test
    void deletePermissionTest() {

        given()
                .contentType(APPLICATION_JSON)
                .delete("p14")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .delete("p_Not_Exist")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

    }

    @Test
    void updatePermissionTest() {
        var criteria = new UpdatePermissionRequestDTO();
        criteria.setAppId("app1");
        criteria.setProductName("productName");
        criteria.setAction("SEARCH");
        criteria.setModificationCount(0);

        var output = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .put("p14")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(Permission.class);

        assertThat(output).isNotNull();
        Assertions.assertEquals(criteria.getAction(), output.getAction());

        given()
                .contentType(APPLICATION_JSON)
                .put("p14")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTO.class);
    }

    @Test
    void getPermissionTest() {

        var output = given()
                .contentType(APPLICATION_JSON)
                .get("p14")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(Permission.class);

        assertThat(output).isNotNull();
        Assertions.assertEquals("app1", output.getAppId());
        Assertions.assertEquals("a2", output.getAction());
        Assertions.assertEquals("test1", output.getProductName());
    }
}
