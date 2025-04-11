package org.tkit.onecx.permission.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;
import static org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS;
import static org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper.ErrorKeys.INVALID_IMPORT_REQUEST;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.rs.internal.model.AssignmentSnapshotDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ExportAssignmentsRequestDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(EximRestController.class)
@WithDBData(value = "data/test-exim-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-pm:read", "ocx-pm:write" })
public class EximRestControllerTest extends AbstractTest {

    @Test
    void exportImportTest() {
        var exportRequest = new ExportAssignmentsRequestDTO().productNames(Set.of("test1"));
        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(exportRequest)
                .post("/export")
                .then().log().all()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(AssignmentSnapshotDTO.class);
        assertThat(dto).isNotNull();

        //snapshot should be importable
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(dto)
                .post("/import")
                .then().log().all()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void importEmptyBodyTest() {

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .post("/import")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getErrorCode()).isEqualTo(CONSTRAINT_VIOLATIONS.name());
        assertThat(dto.getDetail()).isEqualTo(
                "importAssignments.assignmentSnapshotDTO: must not be null");
    }

    @Test
    void importMissingDataTest() {

        var request = new AssignmentSnapshotDTO()
                .putAssignmentsItem("pp1", Map.of("aa1",
                        Map.of("rr1", Map.of("r", List.of("a1", "a2")), "n1", Map.of("r", List.of("a1", "a2")))));

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("/import")
                .then()
                .statusCode(CONFLICT.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getErrorCode()).isEqualTo(INVALID_IMPORT_REQUEST.name());
        assertThat(dto.getDetail()).isEqualTo(
                "The request could not be fully completed due to a conflict with the current state of the roles and permissions");
        assertThat(dto.getInvalidParams()).isNotNull().hasSize(4);
    }
}
