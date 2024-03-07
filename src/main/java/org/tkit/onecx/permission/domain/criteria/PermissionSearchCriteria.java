package org.tkit.onecx.permission.domain.criteria;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionSearchCriteria {

    private String appId;
    private Set<String> productNames;
    private Integer pageNumber = 0;
    private Integer pageSize = 100;
}
