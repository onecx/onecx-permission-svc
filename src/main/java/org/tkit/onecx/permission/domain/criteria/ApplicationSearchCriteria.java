package org.tkit.onecx.permission.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationSearchCriteria {

    private String appId;

    private String name;
    private String productName;

    private Integer pageNumber = 0;
    private Integer pageSize = 100;
}
