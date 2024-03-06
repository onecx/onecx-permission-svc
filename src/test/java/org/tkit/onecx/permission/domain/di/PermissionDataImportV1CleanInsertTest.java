package org.tkit.onecx.permission.domain.di;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.permission.domain.di.v1.model.*;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = "data/test-di.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class PermissionDataImportV1CleanInsertTest extends AbstractPermissionDataImportV1Test {

    @Test
    void cleanInsertTest() {

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

        var config = createConfig("test-clean-insert-1", PermissionDataImportV1.Operation.CLEAN_INSERT, dto);
        service.importData(config);

        checkResult("default", 1, 2, 1, 2);
    }

}
