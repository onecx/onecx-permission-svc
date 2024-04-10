package org.tkit.onecx.permission.rs.operator.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.BAD_REQUEST;
import static org.jboss.resteasy.reactive.RestResponse.Status.OK;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.rs.operator.v1.mappers.ExceptionMapper;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.rs.operator.v1.model.PermissionDTOV1;
import gen.org.tkit.onecx.permission.rs.operator.v1.model.PermissionRequestDTOV1;
import gen.org.tkit.onecx.permission.rs.operator.v1.model.ProblemDetailResponseDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorRestController.class)
@WithDBData(value = "data/test-operator-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class OperatorRestControllerTest extends AbstractTest {

    @Test
    void requestNoBodyTest() {
        var exception = given()
                .contentType(APPLICATION_JSON)
                .pathParam("productName", "test1")
                .pathParam("appId", "app1")
                .put()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTOV1.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo("createOrUpdatePermission.permissionRequestDTOV1: must not be null");
    }

    @Test
    void requestEmptyListTest() {
        var request = new PermissionRequestDTOV1();
        request.setPermissions(List.of());

        given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("productName", "test1")
                .pathParam("appId", "app1")
                .put()
                .then()
                .statusCode(OK.getStatusCode());

    }

    @Test
    void requestWrongPermissionTest() {
        var per = new PermissionDTOV1();
        per.setDescription("description");

        var request = new PermissionRequestDTOV1();
        request.setPermissions(List.of(per));

        var exception = given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("productName", "test1")
                .pathParam("appId", "app1")
                .put()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTOV1.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).startsWith("createOrUpdatePermission.permissionRequestDTOV1.permissions[0]");
        assertThat(exception.getInvalidParams()).hasSize(2);
    }

    @Test
    void requestPermissionTest() {
        var per1 = new PermissionDTOV1().action("a1").resource("o1").description("description");
        var per2 = new PermissionDTOV1().action("new1").resource("o1").description("description1");

        var request = new PermissionRequestDTOV1();
        request.setPermissions(List.of(per1, per2));

        given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("productName", "test1")
                .pathParam("appId", "app1")
                .put()
                .then()
                .statusCode(OK.getStatusCode());

    }

    @Test
    void requestDuplicatePermissionTest() {
        var per1 = new PermissionDTOV1().action("a1").resource("o1").description("description");
        var per2 = new PermissionDTOV1().action("a1").resource("o1").description("description1");

        var request = new PermissionRequestDTOV1();
        request.setPermissions(List.of(per1, per2));

        var exception = given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("productName", "test1")
                .pathParam("appId", "app3")
                .put()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTOV1.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'uc_permission_key'  Detail: Key (app_id, product_name, resource, action)=(app3, test1, o1, a1) already exists.]");
    }
}
