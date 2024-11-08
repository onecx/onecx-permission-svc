package org.tkit.onecx.permission.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.daos.ApplicationDAO;
import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.quarkus.jpa.models.TraceableEntity;

@ApplicationScoped
public class ApplicationService {

    @Inject
    AssignmentDAO assignmentDAO;

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    ApplicationDAO applicationDAO;

    @Transactional
    public void deleteApplicationAndRelatedPermissionsAndAssignmentsById(String id, String applicationId) {
        applicationDAO.deleteQueryById(id);
        var permissions = permissionDAO.findByAppId(applicationId);
        var permissionsIds = permissions.stream().map(TraceableEntity::getId).toList();
        assignmentDAO.deleteByPermissionIds(permissionsIds);
        permissionDAO.delete(permissions);
    }
}
