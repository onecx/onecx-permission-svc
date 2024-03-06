package org.tkit.onecx.permission.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionSearchCriteria {

    private String appId;
    private Integer pageNumber = 0;
    private Integer pageSize = 100;
}
