package org.tkit.onecx.permission.domain.di;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.daos.*;
import org.tkit.onecx.permission.domain.models.Application;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

@ApplicationScoped
public class PermissionImportService {

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    AssignmentDAO assignmentDAO;

    @Inject
    RoleDAO roleDAO;

    @Inject
    ApplicationDAO applicationDAO;

    private static final String PRINCIPAL = "data-import";

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void deleteAllData(String tenantId) {
        try {
            var ctx = Context.builder()
                    .principal(PRINCIPAL)
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);

            assignmentDAO.deleteQueryAll();
            roleDAO.deleteQueryAll();

        } finally {
            ApplicationContext.close();
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createAllProducts(List<Application> applications, List<Permission> permissions) {

        permissionDAO.deleteQueryAll();
        permissionDAO.create(permissions);

        applicationDAO.deleteQueryAll();
        applicationDAO.create(applications);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createAndUpdateAllProducts(List<Application> createApplications, List<Permission> createPermissions,
            List<Application> updateApplications, List<Permission> updatePermissions) {

        permissionDAO.create(createPermissions);
        permissionDAO.update(updatePermissions);

        applicationDAO.create(createApplications);
        applicationDAO.update(updateApplications);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createTenantData(String tenantId, List<Role> roles, List<Assignment> assignments) {
        try {
            var ctx = Context.builder()
                    .principal(PRINCIPAL)
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);

            // create tenant roles
            roleDAO.create(roles);

            // create tenant assignments
            assignmentDAO.create(assignments);

        } finally {
            ApplicationContext.close();
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createAndUpdateTenantData(String tenantId, List<Role> createRoles, List<Assignment> createAssignments,
            List<Role> updateRoles) {
        try {
            var ctx = Context.builder()
                    .principal(PRINCIPAL)
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);

            // create tenant roles
            roleDAO.create(createRoles);
            roleDAO.update(updateRoles);

            // create tenant assignments
            assignmentDAO.create(createAssignments);

        } finally {
            ApplicationContext.close();
        }
    }

}
