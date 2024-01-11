package io.github.onecx.permission.rs.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.permission.rs.v1.PermissionApiV1;

@LogService
@ApplicationScoped
public class PermissionRestController implements PermissionApiV1 {

    @Override
    public Response getApplicationPermission(String appId, String body) {
        return null;
    }

    @Override
    public Response getWorkspacePermission(String workspace, String body) {
        return null;
    }

    @Override
    public Response getWorkspacePermissionApplications(String workspace, String body) {
        return null;
    }

}
