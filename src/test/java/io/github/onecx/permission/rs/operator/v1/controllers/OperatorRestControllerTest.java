package io.github.onecx.permission.rs.operator.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.BAD_REQUEST;
import static org.jboss.resteasy.reactive.RestResponse.Status.OK;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.permission.rs.operator.v1.model.PermissionDTOV1;
import gen.io.github.onecx.permission.rs.operator.v1.model.PermissionRequestDTOV1;
import gen.io.github.onecx.permission.rs.operator.v1.model.ProblemDetailResponseDTOV1;
import io.github.onecx.permission.rs.operator.v1.mappers.ExceptionMapper;
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorRestController.class)
@WithDBData(value = "data/test-operator-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class OperatorRestControllerTest extends AbstractTest {

    @Test
    void requestNoBodyTest() {
        var exception = given()
                .contentType(APPLICATION_JSON)
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

        var exception = given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("appId", "app1")
                .put()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTOV1.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo(
                "createOrUpdatePermission.permissionRequestDTOV1.permissions: size must be between 1 and 2147483647");
    }

    @Test
    void requestWrongPermissionTest() {
        var per = new PermissionDTOV1();
        per.setName("name");
        per.setDescription("description");

        var request = new PermissionRequestDTOV1();
        request.setPermissions(List.of(per));

        var exception = given()
                .contentType(APPLICATION_JSON)
                .body(request)
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
        var per1 = new PermissionDTOV1().action("a1")._object("o1").name("name").description("description");
        var per2 = new PermissionDTOV1().action("new1")._object("o1").name("name1").description("description1");

        var request = new PermissionRequestDTOV1();
        request.setPermissions(List.of(per1, per2));

        given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("appId", "app1")
                .put()
                .then()
                .statusCode(OK.getStatusCode());

    }
}
