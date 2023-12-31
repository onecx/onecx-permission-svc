package io.github.onecx.permission.rs.operator.v1.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.io.github.onecx.permission.rs.operator.v1.model.PermissionDTOV1;
import gen.io.github.onecx.permission.rs.operator.v1.model.PermissionRequestDTOV1;
import io.github.onecx.permission.domain.models.Permission;

@Mapper
public interface OperatorPermissionMapper {

    default List<Permission> map(PermissionRequestDTOV1 dto, String appId) {
        return map(dto.getPermissions(), appId);
    }

    default List<Permission> map(List<PermissionDTOV1> list, String appId) {
        if (list == null) {
            return List.of();
        }
        List<Permission> data = new ArrayList<>();
        for (PermissionDTOV1 dto : list) {
            data.add(map(dto, appId));
        }
        return data;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    Permission map(PermissionDTOV1 dto, String appId);
}
