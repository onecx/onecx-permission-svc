package io.github.onecx.permission.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.permission.rs.internal.model.PermissionDTO;
import gen.io.github.onecx.permission.rs.internal.model.PermissionPageResultDTO;
import gen.io.github.onecx.permission.rs.internal.model.PermissionSearchCriteriaDTO;
import io.github.onecx.permission.domain.criteria.PermissionSearchCriteria;
import io.github.onecx.permission.domain.models.Permission;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface PermissionMapper {

    PermissionSearchCriteria map(PermissionSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    PermissionPageResultDTO map(PageResult<Permission> page);

    @Mapping(target = "_object", source = "object")
    PermissionDTO map(Permission data);
}
