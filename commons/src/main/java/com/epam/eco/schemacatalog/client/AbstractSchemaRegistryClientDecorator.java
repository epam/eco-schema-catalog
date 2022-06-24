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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaReference;
import io.confluent.kafka.schemaregistry.client.rest.entities.SubjectVersion;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

/**
 * @author Andrei_Tytsik
 */
@SuppressWarnings("deprecation")
public abstract class AbstractSchemaRegistryClientDecorator implements SchemaRegistryClient {

    private final SchemaRegistryClient schemaRegistryClient;

    protected AbstractSchemaRegistryClientDecorator(SchemaRegistryClient schemaRegistryClient) {
        Validate.notNull(schemaRegistryClient, "SchemaRegistryClient is null");

        this.schemaRegistryClient = schemaRegistryClient;
    }

    @Override
    public Optional<ParsedSchema> parseSchema(String schemaType, String schemaString, List<SchemaReference> references) {
        return decorated().parseSchema(schemaType, schemaString, references);
    }

    @Deprecated
    public int register(String subject, Schema schema) throws IOException, RestClientException {
        return decorated().register(subject, schema);
    }

    @Override
    public int register(String subject, ParsedSchema schema) throws IOException, RestClientException {
        return decorated().register(subject, schema);
    }

    @Deprecated
    public int register(String subject, Schema schema, int version, int id) throws IOException, RestClientException {
        return decorated().register(subject, schema, version, id);
    }

    @Override
    public int register(String subject, ParsedSchema schema, int version, int id) throws IOException, RestClientException {
        return decorated().register(subject, schema, version, id);
    }

    @Deprecated
    public Schema getByID(int id) throws IOException, RestClientException {
        return decorated().getByID(id);
    }

    @Deprecated
    public Schema getById(int id) throws IOException, RestClientException {
        return decorated().getById(id);
    }

    @Override
    public ParsedSchema getSchemaById(int id) throws IOException, RestClientException {
        return decorated().getSchemaById(id);
    }

    @Deprecated
    public Schema getBySubjectAndID(String subject, int id) throws IOException, RestClientException {
        return decorated().getBySubjectAndID(subject, id);
    }

    @Deprecated
    public Schema getBySubjectAndId(String subject, int id) throws IOException, RestClientException {
        return decorated().getBySubjectAndId(subject, id);
    }

    @Override
    public ParsedSchema getSchemaBySubjectAndId(String subject, int id) throws IOException, RestClientException {
        return decorated().getSchemaBySubjectAndId(subject, id);
    }

    @Override
    public List<ParsedSchema> getSchemas(String subjectPrefix, boolean lookupDeletedSchema, boolean latestOnly) throws IOException, RestClientException {
        return decorated().getSchemas(subjectPrefix, lookupDeletedSchema, latestOnly);
    }

    @Override
    public Collection<String> getAllSubjectsById(int id) throws IOException, RestClientException {
        return decorated().getAllSubjectsById(id);
    }

    @Override
    public Collection<SubjectVersion> getAllVersionsById(int id) throws IOException, RestClientException {
        return decorated().getAllVersionsById(id);
    }

    @Override
    public Collection<String> getAllSubjectsByPrefix(String subjectPrefix) throws IOException, RestClientException {
        return decorated().getAllSubjectsByPrefix(subjectPrefix);
    }

    @Override
    public SchemaMetadata getLatestSchemaMetadata(String subject) throws IOException, RestClientException {
        return decorated().getLatestSchemaMetadata(subject);
    }

    @Override
    public SchemaMetadata getSchemaMetadata(String subject, int version) throws IOException, RestClientException {
        return decorated().getSchemaMetadata(subject, version);
    }

    @Deprecated
    public int getVersion(String subject, Schema schema) throws IOException, RestClientException {
        return decorated().getVersion(subject, schema);
    }

    @Override
    public int getVersion(String subject, ParsedSchema schema) throws IOException, RestClientException {
        return decorated().getVersion(subject, schema);
    }

    @Override
    public List<Integer> getAllVersions(String subject) throws IOException, RestClientException {
        return decorated().getAllVersions(subject);
    }

    @Deprecated
    public boolean testCompatibility(String subject, Schema schema) throws IOException, RestClientException {
        return decorated().testCompatibility(subject, schema);
    }

    @Override
    public boolean testCompatibility(String subject, ParsedSchema schema) throws IOException, RestClientException {
        return decorated().testCompatibility(subject, schema);
    }

    @Override
    public List<String> testCompatibilityVerbose(String subject, ParsedSchema schema) throws IOException, RestClientException {
        return decorated().testCompatibilityVerbose(subject, schema);
    }

    @Override
    public String updateCompatibility(String subject, String compatibility) throws IOException, RestClientException {
        return decorated().updateCompatibility(subject, compatibility);
    }

    @Override
    public String getCompatibility(String subject) throws IOException, RestClientException {
        return decorated().getCompatibility(subject);
    }

    @Override
    public void deleteCompatibility(String subject) throws IOException, RestClientException {
        decorated().deleteCompatibility(subject);
    }

    @Override
    public Collection<String> getAllSubjects() throws IOException, RestClientException {
        return decorated().getAllSubjects();
    }

    @Deprecated
    public int getId(String subject, Schema schema) throws IOException, RestClientException {
        return decorated().getId(subject, schema);
    }

    @Override
    public int getId(String subject, ParsedSchema schema) throws IOException, RestClientException {
        return decorated().getId(subject, schema);
    }

    @Override
    public List<Integer> deleteSubject(String subject) throws IOException, RestClientException {
        return decorated().deleteSubject(subject);
    }

    @Override
    public List<Integer> deleteSubject(Map<String, String> requestProperties, String subject) throws IOException, RestClientException {
        return decorated().deleteSubject(requestProperties, subject);
    }

    @Override
    public Integer deleteSchemaVersion(String subject, String version) throws IOException, RestClientException {
        return decorated().deleteSchemaVersion(subject, version);
    }

    @Override
    public Integer deleteSchemaVersion(Map<String, String> requestProperties, String subject, String version) throws IOException, RestClientException {
        return decorated().deleteSchemaVersion(requestProperties, subject, version);
    }

    @Override
    public String setMode(String mode) throws IOException, RestClientException {
        return decorated().setMode(mode);
    }

    @Override
    public String setMode(String mode, String subject) throws IOException, RestClientException {
        return decorated().setMode(mode, subject);
    }

    @Override
    public String getMode() throws IOException, RestClientException {
        return decorated().getMode();
    }

    @Override
    public String getMode(String subject) throws IOException, RestClientException {
        return decorated().getMode(subject);
    }

    @Override
    public void deleteMode(String subject) throws IOException, RestClientException {
        decorated().deleteMode(subject);
    }

    @Override
    public void reset() {
        decorated().reset();
    }

    @Override
    public int hashCode() {
        return decorated().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return decorated().equals(obj);
    }

    @Override
    public String toString() {
        return decorated().toString();
    }

    protected SchemaRegistryClient decorated() {
        return schemaRegistryClient;
    }

}
