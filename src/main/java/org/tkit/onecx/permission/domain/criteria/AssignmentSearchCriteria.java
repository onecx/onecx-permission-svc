package org.tkit.onecx.permission.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentSearchCriteria {

    private String[] appIds;
    private Integer pageNumber = 0;
    private Integer pageSize = 100;
    private String roleId;
}
