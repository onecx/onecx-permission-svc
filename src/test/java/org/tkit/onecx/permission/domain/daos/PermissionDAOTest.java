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
        methodExceptionTests(() -> dao.findPermissionForUser(null, null, null),
                PermissionDAO.ErrorKeys.ERROR_FIND_PERMISSION_FOR_USER);
        methodExceptionTests(() -> dao.findByProductAndAppIdAndExcludePermissionsById(null, null, null),
                PermissionDAO.ErrorKeys.ERROR_FIND_BY_PRODUCT_AND_APP_ID);
        methodExceptionTests(() -> dao.findByProductNamesAndExcludePermissionsById(null, null),
                PermissionDAO.ErrorKeys.ERROR_FIND_BY_PRODUCT_NAMES_NOT_PERMISSIONS);
        methodExceptionTests(() -> dao.findByProductNames(null),
                PermissionDAO.ErrorKeys.ERROR_FIND_BY_PRODUCT_NAMES);
        methodExceptionTests(() -> dao.findByCriteria(null),
                PermissionDAO.ErrorKeys.ERROR_FIND_PERMISSION_BY_CRITERIA);
        methodExceptionTests(() -> dao.findAllExcludingGivenIds(null),
                PermissionDAO.ErrorKeys.ERROR_FIND_NOT_BY_IDS);
        methodExceptionTests(() -> dao.findUsersPermissions(null, 0, 0),
                PermissionDAO.ErrorKeys.ERROR_FIND_PERMISSION_FOR_USER);

    }

}
