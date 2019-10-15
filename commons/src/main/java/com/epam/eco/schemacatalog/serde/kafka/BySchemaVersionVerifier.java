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

import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;

import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;
import com.epam.eco.schemacatalog.domain.schema.SchemaVersionRange;
import com.epam.eco.schemacatalog.serde.kafka.VerificationResult.Status;

/**
 * @author Andrei_Tytsik
 */
public final class BySchemaVersionVerifier extends AbstractVerifier<GenericContainer> {

    public static final String SCHEMA_VERSION_CONFIG = "schema.version";

    private SchemaVersionRange allowedVersionRange;

    @Override
    public void init(
            String subject,
            ExtendedSchemaRegistryClient schemaRegistryClient,
            Map<String, ?> config) {
        super.init(subject, schemaRegistryClient, config);

        initAllowedVersionRange();
    }

    @Override
    public VerificationResult verify(GenericContainer data, Schema originalSchema) {
        if (originalSchema == null) {
            return VerificationResult.with(Status.PASSED);
        }

        int version = getSchemaVersion(originalSchema);
        if (allowedVersionRange.contains(version)) {
            return VerificationResult.with(Status.PASSED);
        } else if (allowedVersionRange.after(version)) {
            return VerificationResult.with(Status.SKIPPABLE);
        } else if (allowedVersionRange.before(version)) {
            return VerificationResult.with(
                    Status.NOT_PASSED,
                    String.format(
                            "Schema version %d is greater than allowed %s",
                            version, allowedVersionRange));
        } else {
            throw new RuntimeException(
                    String.format("Can't compare version %d to range %s", version, allowedVersionRange));
        }
    }

    private void initAllowedVersionRange() {
        String versionStr = readStringConfig(SCHEMA_VERSION_CONFIG, true);
        allowedVersionRange = SchemaVersionRange.parse(versionStr);
    }

}
