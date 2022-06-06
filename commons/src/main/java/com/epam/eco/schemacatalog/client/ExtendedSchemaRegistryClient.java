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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.avro.Schema;

import com.epam.eco.commons.avro.modification.SchemaModification;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;

/**
 * @author Andrei_Tytsik
 */
public interface ExtendedSchemaRegistryClient extends SchemaRegistryClient {
    SchemaRegistryServiceInfo getServiceInfo();
    Collection<String> getAllSubjectsUnchecked();
    List<Integer> getAllVersionsUnchecked(String subject);
    @Deprecated
    Schema getBySubjectAndVersion(String subject, int version);
    ParsedSchema getSchemaBySubjectAndVersion(String subject, int version);
    @Deprecated
    AvroCompatibilityLevel getGlobalCompatibilityLevel();
    CompatibilityLevel getGlobalLevelOfCompatibility();
    @Deprecated
    Optional<AvroCompatibilityLevel> getCompatibilityLevel(String subject);
    Optional<CompatibilityLevel> getLevelOfCompatibility(String subject);
    @Deprecated
    AvroCompatibilityLevel getEffectiveCompatibilityLevel(String subject);
    CompatibilityLevel getEffectiveLevelOfCompatibility(String subject);
    Mode getModeValue();
    Optional<Mode> getModeValue(String subject);
    Mode getEffectiveModeValue(String subject);
    BasicSchemaInfo getSchemaInfo(String subject, int version);
    BasicSchemaInfo getLatestSchemaInfo(String subject);
    SubjectSchemas<BasicSchemaInfo> getSubjectSchemaInfos(String subject);
    BasicSchemaInfo modifyAndRegisterSchema(
            String sourceSubject,
            int sourceVersion,
            String destinationSubject,
            SchemaModification... modifications);
    BasicSchemaInfo modifyAndRegisterSchema(
            String sourceSubject,
            int sourceVersion,
            String destinationSubject,
            List<SchemaModification> modifications);
    BasicSchemaInfo modifyAndRegisterSchema(
            String sourceSubject,
            Schema sourceSchema,
            String destinationSubject,
            SchemaModification... modifications);
    BasicSchemaInfo modifyAndRegisterSchema(
            String sourceSubject,
            Schema sourceSchema,
            String destinationSubject,
            List<SchemaModification> modifications);
    @Deprecated
    void updateCompatibility(String subject, AvroCompatibilityLevel compatibilityLevel);
    void updateCompatibility(String subject, CompatibilityLevel compatibilityLevel);
    void updateMode(String subject, Mode mode);
    boolean subjectExists(String subject);
    List<Integer> deleteSubjectUnchecked(String subject);
    Integer deleteSchema(String subject, int version);
    @Deprecated
    boolean testCompatibilityUnchecked(String subject, Schema schema);
    boolean testCompatibilityUnchecked(String subject, ParsedSchema schema);
    @Deprecated
    int registerUnchecked(String subject, Schema schema);
    int registerUnchecked(String subject, ParsedSchema schema);
    @Deprecated
    int getVersionUnchecked(String subject, Schema schema);
    int getVersionUnchecked(String subject, ParsedSchema schema);
    @Deprecated
    boolean checkSchemaWritable(String subject, Schema schema);
    boolean checkSchemaWritable(String subject, ParsedSchema schema);
    boolean checkSchemaWritable(String subject, int version);
    boolean checkLatestSchemaWritable(String subject);
}
