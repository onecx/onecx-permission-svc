package org.tkit.onecx.permission.rs.exim.v1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.Role;

@Mapper
public interface EximMapperV1 {

    @Mapping(target = "mandatory", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "permissionId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    Assignment create(Role role, Permission permission);

    static String permId(Permission p) {
        return permId(p.getProductName(), p.getAppId(), p.getResource(), p.getAction());
    }

    static String permId(String productName, String appId, String resource, String action) {
        return productName + "#" + appId + "#" + resource + "#" + action;
    }
}
