package org.tkit.onecx.permission.rs.exim.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.rs.exim.v1.mappers.EximExceptionMapperV1;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.rs.exim.v1.model.AssignmentSnapshotDTOV1;
import gen.org.tkit.onecx.permission.rs.exim.v1.model.EximProblemDetailResponseDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(PermissionExportImportV1.class)
@WithDBData(value = "data/test-exim-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-pm:read", "ocx-pm:write" })
class PermissionExportImportV1Test extends AbstractTest {

    @Test
    void operatorImportNullProductTest() {
        var request = new AssignmentSnapshotDTOV1()
                .putAssignmentsItem("test1", null);

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then().log().all()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void operatorImportNullAppTest() {
        Map<String, Map<String, Map<String, List<String>>>> map = new HashMap<>();
        map.put("app1", null);
        var request = new AssignmentSnapshotDTOV1()
                .putAssignmentsItem("test1", map);

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then().log().all()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void operatorImportTest() {

        var request = new AssignmentSnapshotDTOV1()
                .putAssignmentsItem("test1", Map.of("app1",
                        Map.of(
                                "n1", Map.of("o1", List.of("a1", "a2", "a3"), "o2", List.of("a3", "a2")),
                                "k2", Map.of("o2", List.of("a3", "a2")))));

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then().log().all()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void operatorImportMissingDataTest() {

        var request = new AssignmentSnapshotDTOV1()
                .putAssignmentsItem("pp1", Map.of("aa1",
                        Map.of("rr1", Map.of("r", List.of("a1", "a2")), "n1", Map.of("r", List.of("a1", "a2")))));

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then().log().all()
                .statusCode(CONFLICT.getStatusCode())
                .extract()
                .body().as(EximProblemDetailResponseDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getErrorCode()).isEqualTo(EximExceptionMapperV1.ErrorCode.INVALID_IMPORT_REQUEST.name());
        assertThat(dto.getDetail()).isEqualTo(
                "The request could not be fully completed due to a conflict with the current state of the roles and permissions");
        assertThat(dto.getInvalidParams()).isNotNull().hasSize(4);
    }

    @Test
    void operatorImportEmptyBodyTest() {

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .post()
                .then().log().all()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(EximProblemDetailResponseDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getErrorCode()).isEqualTo(EximExceptionMapperV1.ErrorCode.CONSTRAINT_VIOLATIONS.name());
        assertThat(dto.getDetail()).isEqualTo(
                "operatorImportAssignments.assignmentSnapshotDTOV1: must not be null");
    }

}
