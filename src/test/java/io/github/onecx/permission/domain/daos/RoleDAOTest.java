package io.github.onecx.permission.domain.daos;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class RoleDAOTest extends AbstractDAOTest {

    @Inject
    RoleDAO dao;

    @Test
    @SuppressWarnings("java:S2699")
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findById(null),
                RoleDAO.ErrorKeys.FIND_ENTITY_BY_ID_FAILED);
        methodExceptionTests(() -> dao.findByCriteria(null),
                RoleDAO.ErrorKeys.ERROR_FIND_ROLE_BY_CRITERIA);
    }

}
