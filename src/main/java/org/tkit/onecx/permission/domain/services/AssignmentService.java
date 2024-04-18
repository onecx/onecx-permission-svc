package org.tkit.onecx.permission.domain.services;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Role;

@ApplicationScoped
public class AssignmentService {

    @Inject
    AssignmentDAO dao;

    @Transactional
    public void createProductAssignment(List<Assignment> assignments, String roleId, List<String> productNames) {
        if (productNames != null && !productNames.isEmpty()) {
            dao.deleteByCriteria(roleId, productNames, null, null);
        }
        dao.create(assignments);
    }

    @Transactional
    public void createAssignments(Role role, List<Assignment> assignments) {
        dao.deleteByRoleId(role.getId());
        dao.create(assignments);
    }

    @Transactional
    public void createRoleProductAssignments(Role role, String productName, String appId, List<Assignment> assignments) {
        dao.deleteByProductNameAppId(role.getId(), productName, appId);
        dao.create(assignments);
    }

    @Transactional
    public void createRoleProductsAssignments(Role role, List<String> productNames, List<Assignment> assignments) {
        dao.deleteByProducts(role.getId(), productNames);
        dao.create(assignments);
    }
}
