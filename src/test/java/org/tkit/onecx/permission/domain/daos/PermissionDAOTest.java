package org.tkit.onecx.permission.domain.daos;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PermissionDAOTest extends AbstractDAOTest {

    @Inject
    PermissionDAO dao;

    @Test
    @SuppressWarnings("java:S2699")
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findPermissionForUser(null, null),
                PermissionDAO.ErrorKeys.ERROR_FIND_PERMISSION_FOR_USER);
        methodExceptionTests(() -> dao.loadByAppId(null),
                PermissionDAO.ErrorKeys.ERROR_LOAD_BY_APP_ID);
        methodExceptionTests(() -> dao.findByCriteria(null),
                PermissionDAO.ErrorKeys.ERROR_FIND_PERMISSION_BY_CRITERIA);
    }

}
