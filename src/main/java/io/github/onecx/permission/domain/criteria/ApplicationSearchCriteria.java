package io.github.onecx.permission.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationSearchCriteria {

    private String appId;

    private String name;

    private Integer pageNumber;
    private Integer pageSize;
}
