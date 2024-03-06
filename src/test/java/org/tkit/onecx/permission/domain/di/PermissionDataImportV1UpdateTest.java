package org.tkit.onecx.permission.domain.di;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.domain.di.v1.model.*;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = "data/test-di.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class PermissionDataImportV1UpdateTest extends AbstractPermissionDataImportV1Test {

    @Inject
    PermissionDataImportV1 service;

    @Test
    void updateNewDataTest() {

        var dto = new DataImportDTOV1();
        dto.products(Map.of(
                "product1", new DataImportProductValueDTOV1()
                        .applications(Map.of(
                                "app1", new DataImportApplicationValueDTOV1()
                                        .description("app1")
                                        .name("app1")
                                        .permissions(Map.of(
                                                "resource1", Map.of(
                                                        "action1", "action description",
                                                        "action2", "action description")))

                        ))))
                .tenants(Map.of(
                        "default", new DataImportTenantValueDTOV1()
                                .roles(Map.of("role1", new DataImportTenantRoleValueDTOV1()
                                        .description("role description")
                                        .assignments(
                                                Map.of("product1",
                                                        Map.of("app1",
                                                                Map.of("resource1", List.of("action1", "action2")))))

                                ))));

        var config = createConfig("test-update-1", PermissionDataImportV1.Operation.UPDATE, dto);

        service.importData(config);
        checkResult("default", 4, 10, 5, 5);
    }

    @Test
    void updateExistingDataTest() {

        var dto = new DataImportDTOV1();
        dto.products(Map.of(
                "test1", new DataImportProductValueDTOV1()
                        .applications(Map.of(
                                "app1", new DataImportApplicationValueDTOV1()
                                        .description("app1")
                                        .name("app1")
                                        .permissions(Map.of(
                                                "o2", Map.of(
                                                        "a2", "change permission description"),
                                                "resource1", Map.of(
                                                        "action1", "action description",
                                                        "action2", "action description")))

                        ))))
                .tenants(Map.of(
                        "default", new DataImportTenantValueDTOV1()
                                .roles(Map.of(
                                        "n3", new DataImportTenantRoleValueDTOV1()
                                                .description("desc")
                                                .assignments(
                                                        Map.of("test1", Map.of("app1",
                                                                Map.of("o1", List.of("a3"))))),
                                        "n1", new DataImportTenantRoleValueDTOV1()
                                                .description("role description")
                                                .assignments(
                                                        Map.of("test2", Map.of(),
                                                                "test1", Map.of("app1",
                                                                        Map.of("resource1", List.of("action1", "action2"),
                                                                                "o1", List.of("a3")))))

                                ))));

        var config = createConfig("test-update-1", PermissionDataImportV1.Operation.UPDATE, dto);

        service.importData(config);
        checkResult("default", 3, 10, 4, 6);
    }
}
