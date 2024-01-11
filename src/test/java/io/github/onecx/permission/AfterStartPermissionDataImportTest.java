package io.github.onecx.permission;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

import io.github.onecx.permission.domain.daos.*;
import io.github.onecx.permission.test.AbstractTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@DisplayName("Permission data import test from example file")
public class AfterStartPermissionDataImportTest extends AbstractTest {

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    RoleDAO roleDAO;

    @Inject
    WorkspacePermissionDAO workspacePermissionDAO;

    @Inject
    WorkspaceAssignmentDAO workspaceAssignmentDAO;

    @Inject
    AssignmentDAO assignmentDAO;

    @Test
    @DisplayName("Import permission from data file")
    void importDataFromFileTest() {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId("i100")
                    .build();

            ApplicationContext.start(ctx);

            var permissions = permissionDAO.findAll().toList();
            assertThat(permissions).hasSize(8);

            var roles = roleDAO.findAll().toList();
            assertThat(roles).hasSize(2);

            var workspacePermissions = workspacePermissionDAO.findAll();
            assertThat(workspacePermissions).hasSize(3);

            var assignments = assignmentDAO.findAll();
            assertThat(assignments).hasSize(6);

            var workspaceAssignments = workspaceAssignmentDAO.findAll();
            assertThat(workspaceAssignments).hasSize(4);

        } finally {
            ApplicationContext.close();
        }
    }
}
