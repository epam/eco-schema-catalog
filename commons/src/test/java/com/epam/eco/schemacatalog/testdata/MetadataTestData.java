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
package com.epam.eco.schemacatalog.testdata;

import java.util.Random;
import java.util.UUID;

import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;

/**
 * @author Andrei_Tytsik
 */
public class MetadataTestData {

    public static MetadataKey randomKey() {
        return FieldMetadataKey.with(
                "subject",
                new Random().nextInt(1000),
                "schemaFullName",
                UUID.randomUUID().toString());
    }

    public static MetadataValue randomValue() {
        return MetadataValue.builder().
                doc(UUID.randomUUID().toString()).
                build();
    }

    public static SchemaMetadataKey randomSchemaKey() {
        return SchemaMetadataKey.with(
                "subject",
                new Random().nextInt(1000));
    }

    public static MetadataUpdateParams randomUpdateParams() {
        return randomUpdateParams(null);
    }

    public static MetadataUpdateParams randomUpdateParams(MetadataKey key) {
        return MetadataUpdateParams.builder().
                key(key != null ? key : randomKey()).
                doc(UUID.randomUUID().toString()).
                build();
    }

}
