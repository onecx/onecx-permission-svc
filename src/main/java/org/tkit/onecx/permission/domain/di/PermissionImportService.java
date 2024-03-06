package org.tkit.onecx.permission.domain.di;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.daos.*;
import org.tkit.onecx.permission.domain.di.mappers.DataImportV1Mapper;
import org.tkit.onecx.permission.domain.models.Application;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportProductValueDTOV1;
import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportTenantValueDTOV1;

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

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void deleteAllData(String tenantId) {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
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
    public Map<String, Permission> createAllProducts(List<Application> applications, List<Permission> permissions) {

        permissionDAO.deleteQueryAll();
        permissionDAO.create(permissions);

        applicationDAO.deleteQueryAll();
        applicationDAO.create(applications);

        return permissions.stream()
                .collect(Collectors.toMap(r -> r.getProductName() + r.getAppId() + r.getResource() + r.getAction(), r -> r));
    }

    @Inject
    DataImportV1Mapper mapper;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createTenantData(String tenantId, List<Role> roles, List<Assignment> assignments) {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);

            // create tenant roles
            System.out.println("### " + tenantId + " roles " + roles);
            roleDAO.create(roles);

            // create tenant assignments
            assignmentDAO.create(assignments);

        } finally {
            ApplicationContext.close();
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Map<String, Permission> updateApplicationsAndPermissions(Map<String, DataImportProductValueDTOV1> products) {
        return null;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void updateTenantData(String tenantId, DataImportTenantValueDTOV1 dto, Map<String, Permission> permissionMap) {

    }

}
