package org.tkit.onecx.permission.domain.di;

import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.daos.ApplicationDAO;
import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;
import org.tkit.onecx.permission.domain.di.models.TemplateCommonData;
import org.tkit.onecx.permission.domain.di.models.TemplateTenantData;
import org.tkit.onecx.permission.domain.models.Application;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

@ApplicationScoped
public class PermissionTemplateService {

    private static final String PRINCIPAL = "template-import";

    @Inject
    ApplicationDAO applicationDAO;

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    RoleDAO roleDAO;

    @Inject
    AssignmentDAO assignmentDAO;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createTenantData(String tenantId, List<Role> roles, List<Assignment> assignments) {
        try {
            var ctx = Context.builder()
                    .principal(PRINCIPAL)
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);

            roleDAO.create(roles);
            assignmentDAO.create(assignments);

        } finally {
            ApplicationContext.close();
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public TemplateTenantData getTenantData(String tenantId, Set<String> roles, Set<String> productNames) {

        try {
            var ctx = Context.builder()
                    .principal(PRINCIPAL)
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);

            // find apps in DB
            var r = roleDAO.findByNames(roles);
            // find permission in DB
            var a = assignmentDAO.findPermissionActionForProducts(productNames);

            return new TemplateTenantData(r, a);
        } finally {
            ApplicationContext.close();
        }
    }

    public TemplateCommonData getCommonData(Set<String> productNames) {
        // find apps in DB
        var a = applicationDAO.findByProductNames(productNames);
        // find permission in DB
        var p = permissionDAO.findByProductNames(productNames);

        return new TemplateCommonData(a, p);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createApplicationsAndPermissions(List<Application> applications, List<Permission> permissions) {
        permissionDAO.create(permissions);
        applicationDAO.create(applications);
    }
}
