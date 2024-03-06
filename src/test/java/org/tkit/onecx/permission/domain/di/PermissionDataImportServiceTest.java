package org.tkit.onecx.permission.domain.di;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.test.WithDBData;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportDTOV1;
import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportProductValueDTOV1;
import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportTenantValueDTOV1;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class PermissionDataImportServiceTest extends AbstractTest {

    @Inject
    PermissionDataImportV1 service;

    @Inject
    ObjectMapper mapper;

    @Inject
    PermissionDAO permissionDAO;

    @Test
    void checkTest() {
        var data = new DataImportDTOV1().tenants(null).products(null);
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isTrue();

        data.setTenants(null);
        data.products(Map.of());
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isTrue();

        data.setTenants(Map.of());
        data.products(Map.of());
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isTrue();

        data.setTenants(Map.of());
        data.products(null);
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isTrue();

        data.setTenants(Map.of("2", new DataImportTenantValueDTOV1()));
        data.products(Map.of("2", new DataImportProductValueDTOV1()));
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isFalse();

        data.setTenants(Map.of("2", new DataImportTenantValueDTOV1()));
        data.products(null);
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isFalse();

        data.setTenants(null);
        data.products(Map.of("2", new DataImportProductValueDTOV1()));
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isFalse();
    }

    @Test
    void importDataNotSupportedActionTest() {

        Map<String, String> metadata = new HashMap<>();
        metadata.put("operation", "CUSTOM_NOT_SUPPORTED");
        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return metadata;
            }
        };

        service.importData(config);

        List<Permission> data = permissionDAO.findAll().toList();
        assertThat(data).isNotNull().hasSize(7);

    }

    @Test
    void importEmptyDataTest() {
        Assertions.assertDoesNotThrow(() -> {
            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    return new byte[] {};
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    try {
                        return mapper.writeValueAsBytes(new DataImportDTOV1());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    try {
                        var data = new DataImportDTOV1();
                        data.setProducts(null);
                        data.setTenants(null);
                        return mapper.writeValueAsBytes(data);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    try {
                        var data = new DataImportDTOV1();
                        data.setProducts(null);
                        data.setTenants(Map.of());
                        return mapper.writeValueAsBytes(data);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

        });

        var config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return Map.of("operation", "CLEAN_INSERT");
            }

            @Override
            public byte[] getData() {
                return new byte[] { 0 };
            }
        };
        Assertions.assertThrows(PermissionDataImportV1.ImportException.class, () -> service.importData(config));

    }

    @Test
    void importEmptyProductsDataTest() {
        Assertions.assertDoesNotThrow(() -> {
            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    try {
                        var data = new DataImportDTOV1();
                        data.setProducts(new HashMap<>());
                        data.setTenants(Map.of(
                                "default", new DataImportTenantValueDTOV1(),
                                "100", new DataImportTenantValueDTOV1()));
                        return mapper.writeValueAsBytes(data);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        });
    }

    //    @Test
    //    void importProductEmptyApplicationsDataTest() {
    //        Assertions.assertDoesNotThrow(() -> {
    //            service.importData(new DataImportConfig() {
    //                @Override
    //                public Map<String, String> getMetadata() {
    //                    return Map.of("operation", "CLEAN_INSERT");
    //                }
    //
    //                @Override
    //                public byte[] getData() {
    //                    try {
    //                        var products = new HashMap<String, DataImportProductValueDTOV1>();
    //                        products.put("p", new DataImportProductValueDTOV1().applications(
    //                                Map.of("a", new DataImportApplicationValueDTOV1()
    //                                        .permissions(Map.of(
    //
    //                                        )))
    //                        ));
    //                        var data = new DataImportDTOV1();
    //                        data.setProducts(products);
    //                        data.setTenants(Map.of(
    //                                "default", new DataImportTenantValueDTOV1(),
    //                                "100", new DataImportTenantValueDTOV1()));
    //                        return mapper.writeValueAsBytes(data);
    //                    } catch (Exception ex) {
    //                        throw new RuntimeException(ex);
    //                    }
    //                }
    //            });
    //        });
    //    }
}
