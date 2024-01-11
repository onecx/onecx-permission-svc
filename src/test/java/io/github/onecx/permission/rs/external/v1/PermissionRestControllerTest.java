package io.github.onecx.permission.rs.external.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jboss.resteasy.reactive.RestResponse.Status.OK;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.permission.rs.v1.model.ApplicationPermissionsDTOV1;
import io.github.onecx.permission.rs.external.v1.controllers.PermissionRestController;
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(PermissionRestController.class)
@WithDBData(value = "data/test-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class PermissionRestControllerTest extends AbstractTest {

    @Test
    void getApplicationPermissionsTest() {

        var accessToken = createToken(List.of("n3"));

        var dto = given()
                .contentType(APPLICATION_JSON)
                .body(accessToken)
                .post("/application/app1")
                .then().log().all()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(ApplicationPermissionsDTOV1.class);

    }
}
