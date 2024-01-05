package io.github.onecx.permission.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.BAD_REQUEST;
import static org.jboss.resteasy.reactive.RestResponse.Status.OK;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.permission.rs.internal.model.PermissionPageResultDTO;
import gen.io.github.onecx.permission.rs.internal.model.PermissionSearchCriteriaDTO;
import gen.io.github.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;
import io.github.onecx.permission.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.permission.test.AbstractTest;
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
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(PermissionPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(7);
        assertThat(data.getStream()).isNotNull().hasSize(7);

        criteria.setAppId(" ");
        criteria.setName(" ");
        criteria.setObject(" ");
        criteria.setAction(" ");

        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post()
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
        criteria.setName("n1");
        criteria.setObject("o1");
        criteria.setAction("a1");

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(PermissionPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);
    }

    @Test
    void searchNoBodyTest() {
        var exception = given()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTO.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo("searchPermissions.permissionSearchCriteriaDTO: must not be null");
    }
}
