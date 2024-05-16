package org.tkit.onecx.permission.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.permission.domain.criteria.PermissionSearchCriteria;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.permission.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface PermissionMapper {

    PermissionSearchCriteria map(PermissionSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    PermissionPageResultDTO map(PageResult<Permission> page);

    PermissionDTO map(Permission data);

    @Mapping(target = "mandatory", constant = "false")
    @Mapping(target = "operator", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    Permission create(CreatePermissionRequestDTO dto);

    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "mandatory", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    void update(UpdatePermissionRequestDTO dto, @MappingTarget Permission permission);
}
