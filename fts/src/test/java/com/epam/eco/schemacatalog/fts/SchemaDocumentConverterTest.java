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
package com.epam.eco.schemacatalog.fts;

import java.util.Arrays;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.fts.convert.SchemaDocumentConverter;
import com.epam.eco.schemacatalog.fts.datagen.SchemaInfoGenerator;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Andrei_Tytsik
 */
public class SchemaDocumentConverterTest {

    @Test
    public void expectedDataMappedTest() throws Exception {
        FullSchemaInfo schemaInfo = SchemaInfoGenerator.randomFull("Subject", 3);

        SchemaDocument schemaDocument = SchemaDocumentConverter.convert(schemaInfo);

        assertThat(schemaDocument).isNotNull();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(schemaDocument.getSchemaRegistryId()).isNotNull();
        softly.assertThat(schemaDocument.getEcoId()).isNotNull();

        softly.assertThat(schemaDocument.getSubject()).isEqualTo(schemaInfo.getSubject());
        softly.assertThat(schemaDocument.getVersion()).isEqualTo(schemaInfo.getVersion());

        softly.assertThat(schemaDocument.getVersionLatest()).isNotNull();

        softly.assertThat(schemaInfo.isVersionLatest()).isEqualTo(schemaDocument.getVersionLatest());

        softly.assertThat(schemaDocument.getCompatibility()).isEqualTo(AvroCompatibilityLevel.BACKWARD.name());

        softly.assertThat(schemaDocument).isNotNull();
        softly.assertThat(schemaDocument.getPath().size()).isEqualTo(SchemaInfoGenerator.PATHS.length);
        softly.assertThat(schemaDocument.getPath()).containsAll(Arrays.asList(SchemaInfoGenerator.PATHS));

        softly.assertThat(schemaDocument.getRootName()).isEqualTo(SchemaInfoGenerator.NAMES[0]);
        softly.assertThat(schemaDocument.getRootNamespace()).isEqualTo(SchemaInfoGenerator.NAMESPACES[0]);
        softly.assertThat(schemaDocument.getRootFullname()).isEqualTo(SchemaInfoGenerator.NAMESPACES[0] + "." + SchemaInfoGenerator.NAMES[0]);

        softly.assertThat(schemaDocument.getName()).isNotNull();
        softly.assertThat(schemaDocument.getName()).hasSize(SchemaInfoGenerator.NAMES.length + SchemaInfoGenerator.FIELD_NAMES.length);
        softly.assertThat(schemaDocument.getName()).containsAll(Arrays.asList(SchemaInfoGenerator.NAMES));
        softly.assertThat(schemaDocument.getName()).containsAll(Arrays.asList(SchemaInfoGenerator.FIELD_NAMES));

        softly.assertThat(schemaDocument.getNamespace()).isNotNull();
        softly.assertThat(schemaDocument.getNamespace()).hasSameSizeAs(SchemaInfoGenerator.NAMESPACES);
        softly.assertThat(schemaDocument.getNamespace()).containsAll(Arrays.asList(SchemaInfoGenerator.NAMESPACES));

        softly.assertThat(schemaDocument.getFullname()).isNotNull();
        softly.assertThat(schemaDocument.getFullname()).hasSameSizeAs(SchemaInfoGenerator.FULLNAMES);
        softly.assertThat(schemaDocument.getFullname()).containsAll(Arrays.asList(SchemaInfoGenerator.FULLNAMES));

        softly.assertThat(schemaDocument.getDoc()).isNotNull();
        softly.assertThat(schemaDocument.getDoc()).hasSameSizeAs(SchemaInfoGenerator.DOCS);
        softly.assertThat(schemaDocument.getDoc()).containsAll(Arrays.asList(SchemaInfoGenerator.DOCS));

        softly.assertThat(schemaDocument.getLogicalType()).isNotNull();
        softly.assertThat(schemaDocument.getLogicalType()).hasSameSizeAs(SchemaInfoGenerator.LOGICAL_TYPES);
        softly.assertThat(schemaDocument.getLogicalType()).containsAll(Arrays.asList(SchemaInfoGenerator.LOGICAL_TYPES));
        softly.assertAll();

        assertThat(schemaDocument.getProperty()).isNotNull();
        // can't check sizes as logical types are considered as properties
        // Assert.assertEquals(TestSchemaData.PROP_KEYS.length, schemaDocument.getProperties().size());
        for (int i = 0; i < SchemaInfoGenerator.PROP_KEYS.length; i++) {
            assertThat(schemaDocument.getProperty()).contains(
                    new KeyValue(
                            SchemaInfoGenerator.PROP_KEYS[i],
                            SchemaInfoGenerator.PROP_VALUES[i])
            );
        }
    }

}
