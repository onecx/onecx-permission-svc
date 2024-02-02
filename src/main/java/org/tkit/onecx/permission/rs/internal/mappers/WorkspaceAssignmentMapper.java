package org.tkit.onecx.permission.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.permission.domain.criteria.WorkspaceAssignmentSearchCriteria;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.onecx.permission.domain.models.WorkspaceAssignment;
import org.tkit.onecx.permission.domain.models.WorkspacePermission;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.permission.rs.internal.model.*;

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
