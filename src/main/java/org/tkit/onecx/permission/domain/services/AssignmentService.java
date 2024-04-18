package org.tkit.onecx.permission.domain.services;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.models.Assignment;

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
}
