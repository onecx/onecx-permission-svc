package org.tkit.onecx.permission.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.permission.domain.criteria.ApplicationSearchCriteria;
import org.tkit.onecx.permission.domain.models.Application;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.permission.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ApplicationMapper {

    ApplicationSearchCriteria map(ApplicationSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    ApplicationPageResultDTO map(PageResult<Application> page);

    ApplicationDTO map(Application data);
}
