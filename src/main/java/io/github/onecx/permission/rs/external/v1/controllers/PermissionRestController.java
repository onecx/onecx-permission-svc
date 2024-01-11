package io.github.onecx.permission.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.cdi.LogExclude;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.permission.rs.v1.PermissionApiV1;
import io.github.onecx.permission.common.services.TokenService;
import io.github.onecx.permission.domain.daos.PermissionDAO;
import io.github.onecx.permission.rs.external.v1.mappers.PermissionMapper;

@LogService
@ApplicationScoped
public class PermissionRestController implements PermissionApiV1 {

    @Inject
    TokenService tokenService;

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    PermissionMapper mapper;

    @Override
    public Response getApplicationPermissions(String appId, @LogExclude String body) {
        var roles = tokenService.getTokenRoles(body);
        var permissions = permissionDAO.findPermissionForUser(appId, roles);
        return Response.ok(mapper.create(appId, permissions)).build();
    }

    @Override
    public Response getWorkspacePermission(String workspace, @LogExclude String body) {
        return null;
    }

    @Override
    public Response getWorkspacePermissionApplications(String workspace, @LogExclude String body) {
        return null;
    }

}
