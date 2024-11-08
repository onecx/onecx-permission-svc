package org.tkit.onecx.permission.domain.daos;

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
        methodExceptionTests(() -> dao.findByProductNames(null),
                ApplicationDAO.ErrorKeys.ERROR_FIND_APPLICATIONS_BY_PRODUCT_NAMES);
        methodExceptionTests(() -> dao.loadByAppId(null, null),
                ApplicationDAO.ErrorKeys.ERROR_LOAD_BY_APP_ID);
        methodExceptionTests(() -> dao.findByCriteria(null),
                ApplicationDAO.ErrorKeys.ERROR_FIND_APPLICATIONS_BY_CRITERIA);
        methodExceptionTests(() -> dao.loadByName(null),
                ApplicationDAO.ErrorKeys.ERROR_LOAD_BY_APP_NAME);
    }

}
