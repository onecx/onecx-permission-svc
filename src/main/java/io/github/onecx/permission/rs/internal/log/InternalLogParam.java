package io.github.onecx.permission.rs.internal.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.io.github.onecx.permission.rs.internal.model.*;

@ApplicationScoped
public class InternalLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, PermissionSearchCriteriaDTO.class, x -> {
                    PermissionSearchCriteriaDTO d = (PermissionSearchCriteriaDTO) x;
                    return PermissionSearchCriteriaDTO.class.getSimpleName() + "[" + d.getPageNumber() + "," + d.getPageSize()
                            + "]";
                }),
                item(10, AssignmentSearchCriteriaDTO.class, x -> {
                    AssignmentSearchCriteriaDTO d = (AssignmentSearchCriteriaDTO) x;
                    return AssignmentSearchCriteriaDTO.class.getSimpleName() + "[" + d.getPageNumber() + "," + d.getPageSize()
                            + "]";
                }),
                item(10, RoleSearchCriteriaDTO.class, x -> {
                    RoleSearchCriteriaDTO d = (RoleSearchCriteriaDTO) x;
                    return RoleSearchCriteriaDTO.class.getSimpleName() + "[" + d.getPageNumber() + "," + d.getPageSize()
                            + "]";
                }),
                item(10, CreateAssignmentRequestDTO.class, x -> {
                    CreateAssignmentRequestDTO d = (CreateAssignmentRequestDTO) x;
                    return CreateAssignmentRequestDTO.class.getSimpleName() + ":r=" + d.getRoleId() + ",p="
                            + d.getPermissionId();
                }),
                item(10, CreateRoleRequestDTO.class,
                        x -> x.getClass().getSimpleName() + ":" + ((CreateRoleRequestDTO) x).getName()),
                item(10, UpdateRoleRequestDTO.class,
                        x -> x.getClass().getSimpleName() + ":" + ((UpdateRoleRequestDTO) x).getName()));
    }
}
