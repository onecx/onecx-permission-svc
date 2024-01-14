package io.github.onecx.permission.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.log.cdi.LogExclude;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.permission.rs.external.v1.PermissionApiV1;
import gen.io.github.onecx.permission.rs.external.v1.model.PermissionRequestDTOV1;
import gen.io.github.onecx.permission.rs.external.v1.model.ProblemDetailResponseDTOV1;
import io.github.onecx.permission.common.services.TokenService;
import io.github.onecx.permission.domain.daos.PermissionDAO;
import io.github.onecx.permission.domain.daos.WorkspacePermissionDAO;
import io.github.onecx.permission.rs.external.v1.mappers.ExceptionMapper;
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

    @Inject
    WorkspacePermissionDAO workspacePermissionDAO;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response getApplicationPermissions(String appId, @LogExclude PermissionRequestDTOV1 permissionRequestDTOV1) {
        var roles = tokenService.getTokenRoles(permissionRequestDTOV1.getToken());
        var permissions = permissionDAO.findPermissionForUser(appId, roles);
        return Response.ok(mapper.create(appId, permissions)).build();
    }

    @Override
    public Response getWorkspacePermission(String workspace, @LogExclude PermissionRequestDTOV1 permissionRequestDTOV1) {
        var roles = tokenService.getTokenRoles(permissionRequestDTOV1.getToken());
        var permissions = workspacePermissionDAO.findWorkspacePermissionForUser(workspace, roles);
        return Response.ok(mapper.createWorkspace(workspace, permissions)).build();
    }

    @Override
    public Response getWorkspacePermissionApplications(String workspace,
            @LogExclude PermissionRequestDTOV1 permissionRequestDTOV1) {

        var roles = tokenService.getTokenRoles(permissionRequestDTOV1.getToken());
        var workspacePermissions = workspacePermissionDAO.findWorkspacePermissionForUser(workspace, roles);
        var permissions = permissionDAO.findAllPermissionForUser(roles);

        return Response.ok(mapper.createWorkspaceApps(workspace, workspacePermissions, permissions)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
