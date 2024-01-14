package io.github.onecx.permission.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.permission.rs.internal.model.AssignmentDTO;
import gen.io.github.onecx.permission.rs.internal.model.AssignmentPageResultDTO;
import gen.io.github.onecx.permission.rs.internal.model.AssignmentSearchCriteriaDTO;
import io.github.onecx.permission.domain.criteria.AssignmentSearchCriteria;
import io.github.onecx.permission.domain.models.Assignment;
import io.github.onecx.permission.domain.models.Permission;
import io.github.onecx.permission.domain.models.Role;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface AssignmentMapper {

    @Mapping(target = "removeStreamItem", ignore = true)
    AssignmentPageResultDTO map(PageResult<Assignment> page);

    AssignmentSearchCriteria map(AssignmentSearchCriteriaDTO dto);

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
    Assignment create(Role role, Permission permission);

    AssignmentDTO map(Assignment data);
}
