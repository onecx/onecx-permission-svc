package org.tkit.onecx.permission.domain.daos;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AssignmentDAOTest extends AbstractDAOTest {

    @Inject
    AssignmentDAO dao;

    @Test
    @SuppressWarnings("java:S2699")
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findById(null),
                AssignmentDAO.ErrorKeys.FIND_ENTITY_BY_ID_FAILED);
        methodExceptionTests(() -> dao.findByCriteria(null),
                AssignmentDAO.ErrorKeys.ERROR_FIND_ASSIGNMENT_BY_CRITERIA);
        methodExceptionTests(() -> dao.deleteByRoleId(null),
                AssignmentDAO.ErrorKeys.ERROR_DELETE_BY_ROLE_ID);
        methodExceptionTests(() -> dao.deleteByRoleProductNameAppId(null, null, null),
                AssignmentDAO.ErrorKeys.ERROR_DELETE_BY_ROLE_PRODUCT_NAME_APP_ID);
        methodExceptionTests(() -> dao.deleteByProductNameAppId(null, null),
                AssignmentDAO.ErrorKeys.ERROR_DELETE_BY_PRODUCT_NAME_APP_ID);
        methodExceptionTests(() -> dao.deleteByProducts(null, null),
                AssignmentDAO.ErrorKeys.ERROR_DELETE_BY_PRODUCTS);
        methodExceptionTests(() -> dao.deleteByPermissionId(null),
                AssignmentDAO.ErrorKeys.ERROR_DELETE_BY_PERMISSION_ID);
        methodExceptionTests(() -> dao.findPermissionActionForProducts(null),
                AssignmentDAO.ErrorKeys.ERROR_FIND_PERMISSION_ACTION_FOR_PRODUCTS);
        methodExceptionTests(() -> dao.selectMandatoryByRoleId(null),
                AssignmentDAO.ErrorKeys.ERROR_SELECT_MANDATORY_BY_ROLE_ID);

    }

}
