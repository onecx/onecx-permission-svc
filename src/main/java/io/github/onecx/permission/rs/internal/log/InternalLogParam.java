package io.github.onecx.permission.rs.internal.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.io.github.onecx.permission.rs.internal.model.PermissionSearchCriteriaDTO;

@ApplicationScoped
public class InternalLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, PermissionSearchCriteriaDTO.class, x -> {
                    PermissionSearchCriteriaDTO d = (PermissionSearchCriteriaDTO) x;
                    return PermissionSearchCriteriaDTO.class.getSimpleName() + "[" + d.getPageNumber() + "," + d.getPageSize()
                            + "]";
                }));
    }
}
