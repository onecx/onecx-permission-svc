package org.tkit.onecx.permission.rs.internal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.permission.domain.criteria.AssignmentSearchCriteria;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.PermissionAction;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.permission.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface AssignmentMapper {

    @Mapping(target = "removeStreamItem", ignore = true)
    AssignmentPageResultDTO map(PageResult<Assignment> page);

    @Mapping(target = "removeStreamItem", ignore = true)
    UserAssignmentPageResultDTO mapUserAssignments(PageResult<Assignment> pageResult);

    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "resource", source = "permission.resource")
    @Mapping(target = "productName", source = "permission.productName")
    @Mapping(target = "applicationId", source = "permission.appId")
    @Mapping(target = "action", source = "permission.action")
    UserAssignmentDTO mapAssignment(Assignment assignment);

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
    @Mapping(target = "mandatory", ignore = true)
    @Mapping(target = "operator", ignore = true)
    Assignment create(Role role, Permission permission);

    @Mapping(target = "appId", source = "permission.appId")
    @Mapping(target = "productName", source = "permission.productName")
    AssignmentDTO map(Assignment data);

    default List<Assignment> createList(Role role, List<Permission> permissions) {
        return permissions.stream().map(permission -> create(role, permission)).toList();
    }

    @Mapping(target = "removeStreamItem", ignore = true)
    UserAssignmentPageResultDTO mapPermissionActions(PageResult<PermissionAction> assignments);

    UserAssignmentDTO map(PermissionAction permissionAction);
}
