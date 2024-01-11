package io.github.onecx.permission.domain.daos;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class WorkspacePermissionDAOTest extends AbstractDAOTest {

    @Inject
    WorkspacePermissionDAO dao;

    @Test
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findByCriteria(null),
                WorkspacePermissionDAO.ErrorKeys.ERROR_FIND_PERMISSION_BY_CRITERIA);
        methodExceptionTests(() -> dao.findById(null),
                WorkspacePermissionDAO.ErrorKeys.FIND_ENTITY_BY_ID_FAILED);
    }
}
