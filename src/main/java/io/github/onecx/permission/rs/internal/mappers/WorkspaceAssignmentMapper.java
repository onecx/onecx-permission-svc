package io.github.onecx.permission.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.permission.rs.internal.model.*;
import io.github.onecx.permission.domain.criteria.WorkspaceAssignmentSearchCriteria;
import io.github.onecx.permission.domain.models.Role;
import io.github.onecx.permission.domain.models.WorkspaceAssignment;
import io.github.onecx.permission.domain.models.WorkspacePermission;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface WorkspaceAssignmentMapper {

    @Mapping(target = "removeStreamItem", ignore = true)
    WorkspaceAssignmentPageResultDTO map(PageResult<WorkspaceAssignment> page);

    WorkspaceAssignmentSearchCriteria map(WorkspaceAssignmentSearchCriteriaDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "permissionId", ignore = true)
    WorkspaceAssignment create(Role role, WorkspacePermission permission);

    WorkspaceAssignmentDTO map(WorkspaceAssignment data);
}
