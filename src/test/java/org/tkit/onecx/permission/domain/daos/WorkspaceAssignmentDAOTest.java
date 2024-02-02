package org.tkit.onecx.permission.domain.daos;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class WorkspaceAssignmentDAOTest extends AbstractDAOTest {

    @Inject
    WorkspaceAssignmentDAO dao;

    @Test
    @SuppressWarnings("java:S2699")
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findByCriteria(null),
                WorkspaceAssignmentDAO.ErrorKeys.ERROR_FIND_ASSIGNMENT_BY_CRITERIA);
        methodExceptionTests(() -> dao.findById(null),
                WorkspaceAssignmentDAO.ErrorKeys.FIND_ENTITY_BY_ID_FAILED);
    }

}
