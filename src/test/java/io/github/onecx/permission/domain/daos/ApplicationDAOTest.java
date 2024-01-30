package io.github.onecx.permission.domain.daos;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ApplicationDAOTest extends AbstractDAOTest {

    @Inject
    ApplicationDAO dao;

    @Test
    @SuppressWarnings("java:S2699")
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.loadByAppId(null),
                ApplicationDAO.ErrorKeys.ERROR_LOAD_BY_APP_ID);
        methodExceptionTests(() -> dao.findByCriteria(null),
                ApplicationDAO.ErrorKeys.ERROR_FIND_APPLICATIONS_BY_CRITERIA);
    }

}
