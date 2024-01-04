package io.github.onecx.permission.rs.operator.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.permission.rs.operator.v1.PermissionOperatorApi;
import gen.io.github.onecx.permission.rs.operator.v1.model.PermissionRequestDTOV1;
import gen.io.github.onecx.permission.rs.operator.v1.model.ProblemDetailResponseDTOV1;
import io.github.onecx.permission.rs.operator.v1.mappers.ExceptionMapper;

@LogService
@ApplicationScoped
public class PermissionOperator implements PermissionOperatorApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createOrUpdatePermission(String appId, PermissionRequestDTOV1 permissionRequestDTOV1) {
        return null;
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
