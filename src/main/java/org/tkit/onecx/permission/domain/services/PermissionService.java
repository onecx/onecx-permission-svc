package org.tkit.onecx.permission.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;

@ApplicationScoped
public class PermissionService {

    @Inject
    AssignmentDAO assignmentDAO;

    @Inject
    PermissionDAO dao;

    @Transactional
    public void deletePermission(String id) {
        var permission = dao.findById(id);
        if (permission != null && Boolean.TRUE.equals(permission.getMandatory())) {
            return;
        }
        assignmentDAO.deleteByPermissionId(id);
        dao.deleteQueryById(id);
    }
}
