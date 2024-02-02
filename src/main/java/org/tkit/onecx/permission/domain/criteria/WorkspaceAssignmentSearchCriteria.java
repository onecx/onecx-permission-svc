package org.tkit.onecx.permission.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceAssignmentSearchCriteria {

    private String workspaceId;
    private Integer pageNumber;
    private Integer pageSize;
}
