package io.github.onecx.permission.domain.daos;

import io.github.onecx.permission.domain.models.PermissionAssignment;
import jakarta.enterprise.context.ApplicationScoped;
import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class PermissionAssignmentDAO extends AbstractDAO<PermissionAssignment> {
}
