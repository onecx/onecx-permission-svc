package org.tkit.onecx.permission;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.domain.daos.*;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@DisplayName("Permission data import test from example file")
class AfterStartPermissionDataImportTest extends AbstractTest {

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    RoleDAO roleDAO;

    @Inject
    AssignmentDAO assignmentDAO;

    @Inject
    ApplicationDAO applicationDAO;

    @Test
    @DisplayName("Import permission from data file")
    void importDataFromFileTest() {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId("default")
                    .build();

            ApplicationContext.start(ctx);

            var applications = applicationDAO.findAll().toList();
            assertThat(applications).hasSize(2);

            var permissions = permissionDAO.findAll().toList();
            assertThat(permissions).hasSize(28);

            var roles = roleDAO.findAll().toList();
            assertThat(roles).hasSize(1);
            var roleNames = roles.stream().map(Role::getName).collect(Collectors.toSet());
            assertThat(roleNames).containsOnly("onecx-test");

            var assignments = assignmentDAO.findAll();
            assertThat(assignments).hasSize(28);

        } finally {
            ApplicationContext.close();
        }

        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId("900")
                    .build();

            ApplicationContext.start(ctx);

            var applications = applicationDAO.findAll().toList();
            assertThat(applications).hasSize(2);

            var permissions = permissionDAO.findAll().toList();
            assertThat(permissions).hasSize(28);

            var roles = roleDAO.findAll().toList();
            assertThat(roles).hasSize(1);
            var roleNames = roles.stream().map(Role::getName).collect(Collectors.toSet());
            assertThat(roleNames).containsOnly("onecx-test");

            var assignments = assignmentDAO.findAll();
            assertThat(assignments).hasSize(28);

        } finally {
            ApplicationContext.close();
        }
    }
}
