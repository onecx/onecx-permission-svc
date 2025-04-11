package org.tkit.onecx.permission.domain.services;

import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Role;

@ApplicationScoped
public class AssignmentService {

    @Inject
    AssignmentDAO dao;

    @Inject
    RoleDAO roleDAO;

    @Transactional
    public void createAssignments(Role role, List<Assignment> assignments) {
        dao.deleteByRoleId(role.getId());
        dao.create(assignments);
    }

    @Transactional
    public void createRoleProductAssignments(Role role, String productName, String appId, List<Assignment> assignments) {
        dao.deleteByRoleProductNameAppId(role.getId(), productName, appId);
        dao.create(assignments);
    }

    @Transactional
    public void createRoleProductsAssignments(Role role, List<String> productNames, List<Assignment> assignments) {
        dao.deleteByProducts(role.getId(), productNames);
        dao.create(assignments);
    }

    @Transactional
    public void importOperator(List<Assignment> assignments, Map<String, List<String>> productNames, List<Role> createRoles) {
        productNames.forEach((productName, apps) -> dao.deleteByProductNameAppIds(productName, apps));
        roleDAO.create(createRoles);
        dao.create(assignments);
    }
}
