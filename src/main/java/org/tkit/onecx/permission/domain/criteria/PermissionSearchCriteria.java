package org.tkit.onecx.permission.domain.criteria;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionSearchCriteria {

    private String appId;
    private List<String> productNames;
    private Integer pageNumber;
    private Integer pageSize;
}
