/*
 * Copyright 2019 EPAM Systems
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
package com.epam.eco.schemacatalog.serde.kafka;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.StringUtils;

import com.epam.eco.commons.avro.traversal.SchemaTraverseListener;
import com.epam.eco.commons.avro.traversal.SchemaTraverser;
import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;
import com.epam.eco.schemacatalog.serde.kafka.VerificationResult.Status;
import com.epam.eco.schemacatalog.utils.SchemaVersionRange;

/**
 * @author Andrei_Tytsik
 */
public final class BySchemaFieldsVerifier extends AbstractVerifier<GenericRecord>  {

    public static final String SCHEMA_FIELDS_REQUIRED_CONFIG = "schema.fields.required";
    public static final String SCHEMA_FIELDS_EXPECTED_CONFIG = "schema.fields.expected";

    private final Set<String> requiredFields = new HashSet<>();
    private final Set<String> expectedFields = new HashSet<>();

    private SubjectSchemas<BasicSchemaInfo> subjectSchemas;
    private SchemaVersionRange allowedVersionRange;

    @Override
    public void init(
            String subject, ExtendedSchemaRegistryClient
            schemaRegistryClient,
            Map<String, ?> config) {
        super.init(subject, schemaRegistryClient, config);

        initRequiredFields();
        initExpectedFields();
        updateAllowedVersionRangeIfNeeded(1);
    }

    @Override
    public VerificationResult verify(GenericRecord data, Schema originalSchema) {
        if (originalSchema == null) {
            return VerificationResult.with(Status.PASSED);
        }

        int version = getSchemaVersion(originalSchema);
        updateAllowedVersionRangeIfNeeded(version);

        if (allowedVersionRange.contains(version)) {
            return VerificationResult.with(Status.PASSED);
        } else if (allowedVersionRange.after(version)) {
            return VerificationResult.with(Status.SKIPPABLE);
        } else if (allowedVersionRange.before(version)) {
            return VerificationResult.with(
                    Status.NOT_PASSED,
                    String.format("Required fields are missing in schema %s", originalSchema));
        } else {
            throw new RuntimeException(
                    String.format("Can't compare version %d to range %s", version, allowedVersionRange));
        }
    }

    private void initRequiredFields() {
        String fieldsStr = readStringConfig(SCHEMA_FIELDS_REQUIRED_CONFIG, true);
        for (String field : fieldsStr.split(",")) {
            field = StringUtils.stripToNull(field);
            if (field != null) {
                requiredFields.add(field);
            }
        }
    }

    private void initExpectedFields() {
        String fieldsStr = readStringConfig(SCHEMA_FIELDS_EXPECTED_CONFIG, false);

        if (fieldsStr == null) {
            return;
        }

        for (String field : fieldsStr.split(",")) {
            field = StringUtils.stripToNull(field);
            if (field != null) {
                expectedFields.add(field);
            }
        }
    }

    private void updateAllowedVersionRangeIfNeeded(Integer versionToExamine) {
        if (subjectSchemas != null && subjectSchemas.getSchemasAsMap().containsKey(versionToExamine)) {
            return;
        }

        subjectSchemas = schemaRegistryClient.getSubjectSchemaInfos(subject);

        // sanity checks
        if (subjectSchemas.getSchemas().isEmpty()) {
            throw new RuntimeException(
                    String.format("Subject %s has no schemas", subject));
        }
        if (!subjectSchemas.getSchemasAsMap().containsKey(versionToExamine)) {
            throw new RuntimeException(
                    String.format("Subject %s has no schema of version %d", subject, versionToExamine));
        }

        int earliestVersion = -1;
        int latestVersion = -1;
        for (BasicSchemaInfo schemaInfo : subjectSchemas) {
            Set<String> schemaFields = extractSchemaFields(schemaInfo.getSchemaAvro());

            // has required fields
            if (!schemaFields.containsAll(requiredFields)) {
                if (earliestVersion == -1) {
                    continue;
                } else {
                    break;
                }
            }

            earliestVersion = earliestVersion == -1 ? schemaInfo.getVersion() : earliestVersion;

            // has expected fields
            if (expectedFields.isEmpty() || schemaFields.containsAll(expectedFields)) {
                latestVersion = schemaInfo.getVersion();
            }
        }

        if (earliestVersion == -1 || latestVersion == -1) {
            throw new RuntimeException(
                    String.format(
                            "No schemas meeting given conditions: required fields=%s, expected fields=%s",
                            requiredFields, expectedFields));
        }

        allowedVersionRange = SchemaVersionRange.with(earliestVersion, latestVersion);
    }

    private Set<String> extractSchemaFields(Schema schema) {
        Set<String> allFields = new HashSet<>();
        new SchemaTraverser(new SchemaTraverseListener() {
            @Override
            public void onSchemaField(String path, Schema parentSchema, Field field) {
                allFields.add(path);
            }
            @Override
            public void onSchema(String path, Schema parentSchema, Schema schema) {
                // do nothing
            }
        }).walk(schema);
        return allFields;
    }

}
