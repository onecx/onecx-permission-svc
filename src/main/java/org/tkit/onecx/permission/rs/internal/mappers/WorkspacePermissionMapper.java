package org.tkit.onecx.permission.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.permission.domain.criteria.WorkspacePermissionSearchCriteria;
import org.tkit.onecx.permission.domain.models.WorkspacePermission;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.permission.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface WorkspacePermissionMapper {

    WorkspacePermissionSearchCriteria map(WorkspacePermissionSearchCriteriaDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "workspaceId", ignore = true)
    @Mapping(target = "action", ignore = true)
    @Mapping(target = "resource", ignore = true)
    void update(UpdateWorkspacePermissionRequestDTO dto, @MappingTarget WorkspacePermission data);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    WorkspacePermission create(CreateWorkspacePermissionRequestDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    WorkspacePermissionPageResultDTO map(PageResult<WorkspacePermission> page);

    WorkspacePermissionDTO map(WorkspacePermission data);
}
