package org.tkit.onecx.permission.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentSearchCriteria {

    private String[] appId;
    private Integer pageNumber;
    private Integer pageSize;
}
