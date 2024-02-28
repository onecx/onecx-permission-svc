package org.tkit.onecx.permission.rs.internal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.permission.domain.criteria.RoleSearchCriteria;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.permission.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface RoleMapper {

    @Mapping(target = "removeStreamItem", ignore = true)
    public abstract RolePageResultDTO mapPage(PageResult<Role> page);

    RoleSearchCriteria map(RoleSearchCriteriaDTO dto);

    default List<Role> create(CreateRolesRequestDTO dto) {
        return mapList(dto.getRoles());
    }

    List<Role> mapList(List<CreateRoleDTO> roles);

    default CreateRolesResponseDTO map(List<Role> roles) {
        CreateRolesResponseDTO responseDTO = new CreateRolesResponseDTO();
        responseDTO.setRoles(mapListResponse(roles));
        return responseDTO;
    }

    List<RoleDTO> mapListResponse(List<Role> roles);

    CreateRoleDTO create(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    Role map(CreateRoleDTO dto);

    RoleDTO map(Role data);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    void update(UpdateRoleRequestDTO dto, @MappingTarget Role role);
}
