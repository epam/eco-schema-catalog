/*
 * Copyright 2020 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.epam.eco.schemacatalog.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ResourceUtils;

import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBatchUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;

/**
 * @author Raman_Babich
 */
@SpringBootApplication
@Profile("init-demo")
public class DemoInitializer implements CommandLineRunner {

    private static final String AVRO_SCHEMAS_DIR = "classpath:data/avro";
    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    private static final String SCHEMA_CATALOG_UI_URL = "http://localhost:8282";
    private static final String DEMO_NAMESPACE = "com.epam.eco.schemacatalog.demo";

    public static void main(String[] args) {
        SpringApplication.run(DemoInitializer.class, args);
    }

    @Autowired
    private SchemaCatalogClient schemaCatalogClient;

    @Override
    public void run(String... args) throws Exception {
        ExtendedSchemaRegistryClient schemaRegistryClient = new CachedExtendedSchemaRegistryClient(SCHEMA_REGISTRY_URL);


        Map<String, Map<Integer, File>> schemas = collectDemoData();
        for (Map.Entry<String, Map<Integer, File>> subjects : schemas.entrySet()) {
            String subject = subjects.getKey();
            for (Map.Entry<Integer, File> schema : subjects.getValue().entrySet()) {
                Schema.Parser schemaParser = new Schema.Parser();
                schemaRegistryClient.register(subject, schemaParser.parse(schema.getValue()));
            }
        }
        schemaRegistryClient.deleteSubject("order-item-history");

        schemaCatalogClient.updateMetadata(buildMetadata());
    }

    private Map<String, Map<Integer, File>> collectDemoData() throws Exception {
        Map<String, Map<Integer, File>> schemas = new HashMap<>();
        File dataDir = ResourceUtils.getFile(AVRO_SCHEMAS_DIR);
        dataDir.list((current, name) -> {
            File subjectDir = new File(current, name);
            if (!subjectDir.isDirectory()) {
                return false;
            }

            File[] schemaFiles = subjectDir.listFiles();
            if (schemaFiles == null) {
                throw new RuntimeException(String.format(
                        "Some problems occurs while processing '%s' dir.", subjectDir.getName()));
            }
            if (schemaFiles.length == 0) {
                return true;
            }
            Map<Integer, File> subjectSchemas = schemas.computeIfAbsent(subjectDir.getName(), k -> new TreeMap<>());
            for (File schema : schemaFiles) {
                subjectSchemas.put(parseVersion(schema), schema);
            }
            return true;
        });
        return schemas;
    }

    private static int parseVersion(File file) {
        return Integer.parseInt(StringUtils.substringBeforeLast(file.getName(), "."));
    }

    private static MetadataBatchUpdateParams buildMetadata() {
        return MetadataBatchUpdateParams.builder()
                .update(MetadataUpdateParams.builder()
                        .key(SchemaMetadataKey.with("address", 1))
                        .doc("General Billing and Delivery address information")
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("address", 1, demoSchemaName("Address"), "firstLine"))
                        .doc("Unstructured additional information")
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("address", 2, demoSchemaName("Address"), "firstLine"))
                        .doc("Suite or Apartment number if applicable")
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("address", 2, demoSchemaName("Address"), "secondLine"))
                        .doc("Street address")
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("user-account", 1, demoSchemaName("UserAccount"), "password"))
                        .doc("Real password is hashed with {@link SHA-3|https://en.wikipedia.org/wiki/SHA-3}")
                        .build())

                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("cart-detail", 1, demoSchemaName("CartDetail"), "inventoryItemId"))
                        .doc(buildSimpleFkTag("inventory-item", 1, demoSchemaName("InventoryItem"), "id"))
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("cart-detail", 1, demoSchemaName("CartDetail"), "shoppingCartId"))
                        .doc(buildSimpleFkTag("shopping-cart", 1, demoSchemaName("ShoppingCart"), "id"))
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("order", 1, demoSchemaName("Order"), "userAccountId"))
                        .doc(buildSimpleFkTag("user-account", 1, demoSchemaName("UserAccount"), "id"))
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("order", 1, demoSchemaName("Order"), "paymentId"))
                        .doc(buildSimpleFkTag("payment", 1, demoSchemaName("Payment"), "id"))
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("shipping", 1, demoSchemaName("Shipping"), "addressId"))
                        .doc(buildSimpleFkTag("address", 1, demoSchemaName("Address"), "id"))
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("shipping", 1, demoSchemaName("Shipping"), "invoiceId"))
                        .doc(buildSimpleFkTag("invoice", 1, demoSchemaName("invoice"), "id"))
                        .build())
                .update(MetadataUpdateParams.builder()
                        .key(FieldMetadataKey.with("shipping", 1, demoSchemaName("Shipping"), "orderId"))
                        .doc(buildSimpleFkTag("order", 1, demoSchemaName("Order"), "id"))
                        .build())
                .build();
    }

    private static String demoSchemaName(String entity) {
        return DEMO_NAMESPACE + "." + entity;
    }

    private static String buildSimpleFkTag(String subject, int version, String schemaFullName, String field) {
        return String.format(
                "{@foreign_key Primary key|%s|%s|%s|%s|%s}",
                subject, version, schemaFullName, field, buildSchemaLink(subject, version));
    }

    private static String buildSchemaLink(String subject, int version) {
        return String.format(SCHEMA_CATALOG_UI_URL + "/schema/%s/%s", subject, version);
    }

}
