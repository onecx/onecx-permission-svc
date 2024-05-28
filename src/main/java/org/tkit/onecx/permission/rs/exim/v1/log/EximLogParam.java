package org.tkit.onecx.permission.rs.exim.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.permission.rs.exim.v1.model.AssignmentSnapshotDTOV1;

@ApplicationScoped
public class EximLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, AssignmentSnapshotDTOV1.class, x -> {
                    AssignmentSnapshotDTOV1 d = (AssignmentSnapshotDTOV1) x;
                    return AssignmentSnapshotDTOV1.class.getSimpleName() + "[" + d.getId() + "]";
                }));
    }
}
