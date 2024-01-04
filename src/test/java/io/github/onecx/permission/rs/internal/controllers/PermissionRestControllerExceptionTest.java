package io.github.onecx.permission.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jboss.resteasy.reactive.RestResponse.Status.INTERNAL_SERVER_ERROR;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.permission.rs.internal.model.PermissionSearchCriteriaDTO;
import io.github.onecx.permission.domain.daos.PermissionDAO;
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(PermissionRestController.class)
class PermissionRestControllerExceptionTest extends AbstractTest {

    @InjectMock
    PermissionDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findByCriteria(any()))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(PermissionDAO.ErrorKeys.ERROR_FIND_PERMISSION_BY_CRITERIA,
                        new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {
        var criteria = new PermissionSearchCriteriaDTO();
        given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

    }
}
