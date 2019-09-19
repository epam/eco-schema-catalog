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
package com.epam.eco.schemacatalog.client;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.avro.Schema;

import com.epam.eco.commons.avro.modification.SchemaModification;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;

/**
 * @author Andrei_Tytsik
 */
public interface ExtendedSchemaRegistryClient extends SchemaRegistryClient {
    SchemaRegistryServiceInfo getServiceInfo();
    Collection<String> getAllSubjectsUnchecked();
    List<Integer> getAllVersions(String subject);
    Schema getBySubjectAndVersion(String subject, int version);
    Optional<AvroCompatibilityLevel> getCompatibilityLevel(String subject);
    AvroCompatibilityLevel getEffectiveCompatibilityLevel(String subject);
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
    void updateCompatibility(String subject, AvroCompatibilityLevel compatibilityLevel);
    boolean subjectExists(String subject);
    List<Integer> deleteSubject(String subject);
    Integer deleteSchema(String subject, int version);
    boolean testCompatibilityUnchecked(String subject, Schema schema);
    int registerUnchecked(String subject, Schema schema);
    int getVersionUnchecked(String subject, Schema schema);
    boolean checkSchemaWritable(String subject, Schema schema);
    boolean checkSchemaWritable(String subject, int version);
    boolean checkLatestSchemaWritable(String subject);
}
