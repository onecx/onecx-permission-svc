package org.tkit.onecx.permission.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;

@ApplicationScoped
public class RoleService {

    @Inject
    AssignmentDAO assignmentDAO;

    @Inject
    RoleDAO dao;

    @Transactional
    public void deleteRole(String id) {
        var role = dao.findById(id);
        if (role != null && Boolean.TRUE.equals(role.getMandatory())) {
            return;
        }
        assignmentDAO.deleteByRoleId(id);
        dao.deleteQueryById(id);
    }
}
