package org.tkit.onecx.permission.rs.operator.v1.controllers;

import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.permission.domain.daos.ApplicationDAO;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.rs.operator.v1.mappers.ExceptionMapper;
import org.tkit.onecx.permission.rs.operator.v1.mappers.OperatorPermissionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.rs.context.tenant.TenantExclude;

import gen.org.tkit.onecx.permission.rs.operator.v1.PermissionOperatorApi;
import gen.org.tkit.onecx.permission.rs.operator.v1.model.PermissionRequestDTOV1;
import gen.org.tkit.onecx.permission.rs.operator.v1.model.ProblemDetailResponseDTOV1;

@LogService
@ApplicationScoped
public class OperatorRestController implements PermissionOperatorApi {

    @Inject
    PermissionDAO dao;

    @Inject
    OperatorPermissionMapper mapper;

    @Inject
    ApplicationDAO applicationDAO;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    @TenantExclude
    @Transactional(Transactional.TxType.REQUIRED)
    public Response createOrUpdatePermission(String productName, String appId, PermissionRequestDTOV1 permissionRequestDTOV1) {

        var app = applicationDAO.loadByAppId(productName, appId);
        if (app == null) {
            app = mapper.createApp(permissionRequestDTOV1, appId, productName);
            applicationDAO.create(app);
        } else {
            mapper.updateApp(permissionRequestDTOV1, app);
            applicationDAO.update(app);
        }
        var data = mapper.map(permissionRequestDTOV1, appId, productName);
        if (data.isEmpty()) {
            return Response.ok().build();
        }
        var permissions = dao.loadByAppId(appId);
        var map = permissions.stream().collect(Collectors.toMap(x -> x.getResource() + x.getAction(), x -> x));

        for (Permission item : data) {
            var permission = map.get(item.getResource() + item.getAction());
            if (permission == null) {
                // create new permission
                dao.create(item);
            } else {
                // update existing permission
                permission.setDescription(item.getDescription());
                dao.update(permission);
            }
        }
        return Response.ok().build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
