package io.github.onecx.permission.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.permission.rs.internal.model.*;
import io.github.onecx.permission.domain.criteria.ApplicationSearchCriteria;
import io.github.onecx.permission.domain.models.Application;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ApplicationMapper {

    ApplicationSearchCriteria map(ApplicationSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    ApplicationPageResultDTO map(PageResult<Application> page);

    ApplicationDTO map(Application data);
}
