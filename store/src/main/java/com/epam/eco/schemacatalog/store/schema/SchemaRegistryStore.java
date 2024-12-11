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
package com.epam.eco.schemacatalog.store.schema;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.avro.Schema;

import com.epam.eco.schemacatalog.domain.schema.SubjectAndVersion;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;


/**
 * @author Andrei_Tytsik
 */
public interface SchemaRegistryStore {
    List<String> getAllSubjects();

    List<SchemaEntity> getAllSchemas();

    List<SchemaEntity> getSchemas(String subject);

    List<SchemaEntity> getSchemas(Collection<String> subjects);

    SchemaEntity getLatestSchema(String subject);

    SchemaEntity getSchema(String subject, Integer version);

    CompatibilityLevel getSubjectCompatibility(String subject);

    boolean schemaExists(SubjectAndVersion subjectAndVersion);

    boolean schemaExists(String subject, int version);

    boolean subjectExists(String subject);

    boolean testSchemaCompatible(String subject, Schema schema);

    boolean testSchemaCompatible(String subject, ParsedSchema schema);

    SchemaEntity registerSchema(String subject, Schema schema);

    SchemaEntity registerSchema(String subject, ParsedSchema schema);

    void deleteSubject(String subject);

    void deleteSchema(SubjectAndVersion subjectAndVersion);

    void deleteSchema(String subject, int version);

    void updateSubjectCompatibility(String subject, CompatibilityLevel compatibilityLevel);

    void registerListener(SchemaRegistryStoreUpdateListener listener);

    void deleteCompatibility(String subject) throws RestClientException, IOException;
}
