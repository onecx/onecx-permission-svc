package org.tkit.onecx.permission.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.permission.domain.criteria.PermissionSearchCriteria;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.permission.rs.internal.model.PermissionDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.PermissionPageResultDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.PermissionSearchCriteriaDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface PermissionMapper {

    PermissionSearchCriteria map(PermissionSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    PermissionPageResultDTO map(PageResult<Permission> page);

    PermissionDTO map(Permission data);
}
