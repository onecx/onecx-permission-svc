package io.github.onecx.permission.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

import io.github.onecx.permission.domain.models.Assignment;

@ApplicationScoped
public class AssignmentDAO extends AbstractDAO<Assignment> {
}
