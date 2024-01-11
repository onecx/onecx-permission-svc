package io.github.onecx.permission.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspacePermissionSearchCriteria {

    private String workspaceId;
    private String resource;
    private String action;
    private Integer pageNumber;
    private Integer pageSize;
}
