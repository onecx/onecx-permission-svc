package org.tkit.onecx.permission.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(AssignmentRestController.class)
@WithDBData(value = "data/test-internal-no-permission.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class AssignmentRestControllerExtendTest extends AbstractTest {

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
                .statusCode(NOT_FOUND.getStatusCode());
    }

}
