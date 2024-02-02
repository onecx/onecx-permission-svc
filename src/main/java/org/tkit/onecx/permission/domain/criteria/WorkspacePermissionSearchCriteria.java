package org.tkit.onecx.permission.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspacePermissionSearchCriteria {

    private String workspaceId;
    private Integer pageNumber;
    private Integer pageSize;
}
