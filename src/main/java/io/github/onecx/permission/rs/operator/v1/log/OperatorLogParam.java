package io.github.onecx.permission.rs.operator.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.io.github.onecx.permission.rs.operator.v1.model.PermissionRequestDTOV1;

@ApplicationScoped
public class OperatorLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, PermissionRequestDTOV1.class, x -> x.getClass().getSimpleName()));
    }
}
