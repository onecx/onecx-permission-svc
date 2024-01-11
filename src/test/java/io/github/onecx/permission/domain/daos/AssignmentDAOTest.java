package io.github.onecx.permission.domain.daos;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AssignmentDAOTest extends AbstractDAOTest {

    @Inject
    AssignmentDAO dao;

    @Test
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findById(null),
                AssignmentDAO.ErrorKeys.FIND_ENTITY_BY_ID_FAILED);
        methodExceptionTests(() -> dao.findByCriteria(null),
                AssignmentDAO.ErrorKeys.ERROR_FIND_ASSIGNMENT_BY_CRITERIA);
    }

}
