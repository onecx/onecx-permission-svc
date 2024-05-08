package org.tkit.onecx.permission.domain.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ApplicationRef(String productName, String appId) {
}
