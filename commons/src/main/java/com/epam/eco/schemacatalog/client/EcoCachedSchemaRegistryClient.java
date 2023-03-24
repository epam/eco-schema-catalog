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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.SchemaProvider;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaProvider;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.client.rest.Versions;
import io.confluent.kafka.schemaregistry.client.rest.entities.Config;
import io.confluent.kafka.schemaregistry.client.rest.entities.Mode;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaReference;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString;
import io.confluent.kafka.schemaregistry.client.rest.entities.SubjectVersion;
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.ConfigUpdateRequest;
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.ModeUpdateRequest;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.schemaregistry.client.security.basicauth.BasicAuthCredentialProvider;
import io.confluent.kafka.schemaregistry.client.security.basicauth.BasicAuthCredentialProviderFactory;

/**
 * @author Andrei_Tytsik
 */
public class EcoCachedSchemaRegistryClient implements SchemaRegistryClient {

    public static final Map<String, String> DEFAULT_REQUEST_PROPERTIES;
    static {
        Map<String, String> requestProps = new HashMap<String, String>();
        requestProps.put("Content-Type", Versions.SCHEMA_REGISTRY_V1_JSON_WEIGHTED);
        DEFAULT_REQUEST_PROPERTIES = Collections.unmodifiableMap(requestProps);
    }

    private final Map<Integer, ParsedSchema> schemaCache = new ConcurrentHashMap<>();
    private final Map<String, SubjectCache> subjectCache = new ConcurrentHashMap<>();

    private final int maxSchemasPerSubject;

    private final RestService restService;
    private final Map<String, SchemaProvider> providers = new HashMap<>();

    public EcoCachedSchemaRegistryClient(String baseUrl, int maxSchemasPerSubject) {
        this(new ExtendedRestService(baseUrl), maxSchemasPerSubject);
    }

    public EcoCachedSchemaRegistryClient(
            Map<String, SchemaProvider> providers,
            String baseUrl,
            int maxSchemasPerSubject) {
        this(providers, new ExtendedRestService(baseUrl), maxSchemasPerSubject);
    }

    public EcoCachedSchemaRegistryClient(List<String> baseUrls, int maxSchemasPerSubject) {
        this(new ExtendedRestService(baseUrls), maxSchemasPerSubject);
    }

    public EcoCachedSchemaRegistryClient(
            Map<String, SchemaProvider> providers,
            List<String> baseUrls,
            int maxSchemasPerSubject) {
        this(providers, new ExtendedRestService(baseUrls), maxSchemasPerSubject, null);
    }

    public EcoCachedSchemaRegistryClient(RestService restService, int maxSchemasPerSubject) {
        this(restService, maxSchemasPerSubject, null);
    }

    public EcoCachedSchemaRegistryClient(
            Map<String, SchemaProvider> providers,
            RestService restService,
            int maxSchemasPerSubject) {
        this(providers, restService, maxSchemasPerSubject, null);
    }

    public EcoCachedSchemaRegistryClient(String baseUrl, int maxSchemasPerSubject, Map<String, ?> configs) {
        this(new ExtendedRestService(baseUrl), maxSchemasPerSubject, configs);
    }

    public EcoCachedSchemaRegistryClient(
            Map<String, SchemaProvider> providers,
            String baseUrl,
            int maxSchemasPerSubject,
            Map<String, ?> configs) {
        this(providers, new ExtendedRestService(baseUrl), maxSchemasPerSubject, configs);
    }

    public EcoCachedSchemaRegistryClient(List<String> baseUrls, int maxSchemasPerSubject, Map<String, ?> configs) {
        this(new ExtendedRestService(baseUrls), maxSchemasPerSubject, configs);
    }

    public EcoCachedSchemaRegistryClient(
            Map<String, SchemaProvider> providers,
            List<String> baseUrls,
            int maxSchemasPerSubject,
            Map<String, ?> configs) {
        this(providers, new ExtendedRestService(baseUrls), maxSchemasPerSubject, configs);
    }

    public EcoCachedSchemaRegistryClient(
            RestService restService,
            int maxSchemasPerSubject,
            Map<String, ?> configs) {
        this(
                Collections.singletonMap("AVRO", new AvroSchemaProvider()),
                restService,
                maxSchemasPerSubject,
                configs);
    }

    public EcoCachedSchemaRegistryClient(
            Map<String, SchemaProvider> providers,
            RestService restService,
            int maxSchemasPerSubject,
            Map<String, ?> configs) {
        Validate.notNull(restService, "RestService is null");
        Validate.isTrue(maxSchemasPerSubject > 0, "MaxSchemasPerSubject is negative or zero");

        this.maxSchemasPerSubject = maxSchemasPerSubject;
        this.restService =
                restService instanceof ExtendedRestService ?
                restService :
                new ExtendedRestService(restService);

        this.providers.putAll(providers);

        configureRestService(configs);
    }

    private void configureRestService(Map<String, ?> configs) {
        if (configs != null) {
            String credentialSourceConfig =
                    (String) configs.get(SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE);
            if (credentialSourceConfig != null && !credentialSourceConfig.isEmpty()) {
                BasicAuthCredentialProvider basicAuthCredentialProvider = BasicAuthCredentialProviderFactory.
                        getBasicAuthCredentialProvider(credentialSourceConfig, configs);
                restService.setBasicAuthCredentialProvider(basicAuthCredentialProvider);
            }
        }
    }

    @Override
    public Optional<ParsedSchema> parseSchema(String schemaType, String schemaString, List<SchemaReference> references) {
        if (schemaType == null) {
            schemaType = "AVRO";
        }

        SchemaProvider schemaProvider = this.providers.get(schemaType);
        if (schemaProvider == null) {
            return Optional.empty();
        } else {
            return schemaProvider.parseSchema(schemaString, references);
        }
    }

    @Deprecated
    public int register(String subject, Schema schema) throws IOException, RestClientException {
        return this.register(subject, new AvroSchema(schema));
    }

    @Override
    public int register(String subject, ParsedSchema schema) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        SubjectCache subjectCache = getSubjectCache(subject);
        subjectCache.lock();
        try {
            Integer id = subjectCache.getIdBySchema(schema);
            if (id != null) {
                return id;
            }

            id = registerAndGetId(subject, schema);
            subjectCache.addSchemaWithId(schema, id);
            return id;
        } finally {
            subjectCache.unlock();
        }
    }

    @Deprecated
    public int register(
            String subject,
            Schema schema,
            int version,
            int id) throws IOException, RestClientException {
        return this.register(subject, new AvroSchema(schema), version, id);
    }

    @Override
    public int register(
            String subject,
            ParsedSchema schema,
            int version,
            int id) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        SubjectCache subjectCache = getSubjectCache(subject);
        subjectCache.lock();
        try {
            Integer cachedId = subjectCache.getIdBySchema(schema);
            if (cachedId != null) {
                if (id >= 0 && id != cachedId) {
                    throw new IllegalStateException(
                            "Schema already registered with id " + cachedId + " instead of input id " + id);
                }
                return cachedId;
            }

            int retrievedId =
                    id >= 0 ?
                    registerAndGetId(subject, schema, version, id) :
                    registerAndGetId(subject, schema);
            subjectCache.addSchemaWithId(schema, retrievedId);
            return retrievedId;
        } finally {
            subjectCache.unlock();
        }
    }

    @Deprecated
    public Schema getByID(int id) throws IOException, RestClientException {
        return getById(id);
    }

    @Deprecated
    public Schema getById(int id) throws IOException, RestClientException {
        return (Schema) getSchemaById(id).rawSchema();
    }

    @Override
    public ParsedSchema getSchemaById(int id) throws IOException, RestClientException {
        return getSchemaBySubjectAndId(null, id);
    }

    @Deprecated
    public Schema getBySubjectAndID(String subject, int id) throws IOException, RestClientException {
        return getBySubjectAndId(subject, id);
    }

    @Deprecated
    public Schema getBySubjectAndId(String subject, int id) throws IOException, RestClientException {
        return (Schema) getSchemaBySubjectAndId(subject, id).rawSchema();
    }

    @Override
    public ParsedSchema getSchemaBySubjectAndId(String subject, int id) throws IOException, RestClientException {
        Validate.isTrue(id >= 0, "Id is negative");

        if (subject == null) { // global
            return getSchemaByIdFromRegistry(id);
        }

        SubjectCache subjectCache = getSubjectCache(subject);
        subjectCache.lock();
        try {
            ParsedSchema schema = subjectCache.getSchemaById(id);
            if (schema != null) {
                return schema;
            }

            schema = getSchemaByIdFromRegistry(id);
            subjectCache.addSchemaWithId(schema, id);
            return schema;
        } finally {
            subjectCache.unlock();
        }
    }

    @Override
    public List<ParsedSchema> getSchemas(
            String subjectPrefix,
            boolean lookupDeletedSchema,
            boolean latestOnly) throws IOException, RestClientException {
        return getSchemasFromRegistry(subjectPrefix, lookupDeletedSchema, latestOnly);
    }

    @Override
    public Collection<String> getAllSubjectsById(int id) throws IOException, RestClientException {
        return getAllSubjectsByIdFromRegistry(id);
    }

    @Override
    public Collection<SubjectVersion> getAllVersionsById(int id) throws IOException, RestClientException {
        return restService.getAllVersionsById(id);
    }

    @Override
    public io.confluent.kafka.schemaregistry.client.rest.entities.Schema getByVersion(String subject, int version, boolean lookupDeletedSchema) {
        try {
            return restService.getVersion(subject, version, lookupDeletedSchema);
        } catch (IOException | RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<String> getAllSubjectsByPrefix(String subjectPrefix) throws IOException, RestClientException {
        return restService.getAllSubjects(subjectPrefix, false);
    }

    @Override
    public SchemaMetadata getLatestSchemaMetadata(String subject) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");

        return getSchemaMetadataFromRegistry(subject, null);
    }

    @Override
    public SchemaMetadata getSchemaMetadata(
            String subject,
            int version) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is negative");

        return getSchemaMetadataFromRegistry(subject, version);
    }

    @Deprecated
    public int getVersion(String subject, Schema schema) throws IOException, RestClientException {
        return getVersion(subject, new AvroSchema(schema));
    }

    @Override
    public int getVersion(String subject, ParsedSchema schema, boolean normalize)
            throws IOException, RestClientException {
        return getVersion(subject, schema);
    }

    @Override
    public int getVersion(String subject, ParsedSchema schema) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        SubjectCache subjectCache = getSubjectCache(subject);
        subjectCache.lock();
        try {
            Integer version = subjectCache.getVersionBySchema(schema);
            if (version != null) {
                return version;
            }

            version = getSchemaVersionFromRegistry(subject, schema);
            subjectCache.addSchemaWithVersion(schema, version);
            return version;
        } finally {
            subjectCache.unlock();
        }
    }

    @Override
    public List<Integer> getAllVersions(String subject) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");

        return restService.getAllVersions(subject);
    }

    @Deprecated
    public boolean testCompatibility(
            String subject,
            Schema schema) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        return restService.testCompatibility(schema.toString(), subject, "latest").isEmpty();
    }

    @Override
    public boolean testCompatibility(
            String subject,
            ParsedSchema schema) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        return restService.testCompatibility(schema.toString(), subject, "latest").isEmpty();
    }

    @Override
    public List<String> testCompatibilityVerbose(
            String subject,
            ParsedSchema schema) throws IOException, RestClientException {
        return this.restService.testCompatibility(
                schema.canonicalString(),
                schema.schemaType(),
                schema.references(),
                subject,
                "latest",
                true);
    }

    @Override
    public String updateCompatibility(
            String subject,
            String compatibility) throws IOException, RestClientException {
        Validate.notNull(compatibility, "Null compatibility level.");
        Validate.notNull(CompatibilityLevel.forName(compatibility), "Compatibility %s is unknown", compatibility);
        ConfigUpdateRequest response = restService.updateCompatibility(compatibility, subject);
        return response.getCompatibilityLevel();
    }

    @Override
    public String getCompatibility(String subject) throws IOException, RestClientException {
        Config response = restService.getConfig(subject);
        return response.getCompatibilityLevel();
    }

    @Override
    public void deleteCompatibility(String subject) throws IOException, RestClientException {
        restService.deleteSubjectConfig(subject);
    }

    @Override
    public Collection<String> getAllSubjects() throws IOException, RestClientException {
        return restService.getAllSubjects();
    }

    @Deprecated
    public int getId(String subject, Schema schema) throws IOException, RestClientException {
        return getId(subject, new AvroSchema(schema));
    }

    @Override
    public int getId(String subject, ParsedSchema schema, boolean normalize)
            throws IOException, RestClientException {
        return getId(subject, schema);
    }

    @Override
    public int getId(String subject, ParsedSchema schema) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        SubjectCache subjectCache = getSubjectCache(subject);
        subjectCache.lock();
        try {
            Integer id = subjectCache.getIdBySchema(schema);
            if (id != null) {
                return id;
            }

            id = getSchemaIdFromRegistry(subject, schema);
            subjectCache.addSchemaWithId(schema, id);
            return id;
        } finally {
            subjectCache.unlock();
        }
    }

    @Override
    public List<Integer> deleteSubject(String subject, boolean isPermanent)
            throws IOException, RestClientException {
        return deleteSubject(DEFAULT_REQUEST_PROPERTIES, subject);
    }

    @Override
    public List<Integer> deleteSubject(String subject) throws IOException, RestClientException {
        return deleteSubject(DEFAULT_REQUEST_PROPERTIES, subject);
    }

    @Override
    public List<Integer> deleteSubject(Map<String,
            String> requestProperties, String subject, boolean isPermanent)
            throws IOException, RestClientException {
        return deleteSubject(DEFAULT_REQUEST_PROPERTIES, subject);
    }

    @Override
    public List<Integer> deleteSubject(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");

        SubjectCache subjectCache = getSubjectCache(subject);
        subjectCache.lock();
        try {
            subjectCache.clear();
            return restService.deleteSubject(requestProperties, subject);
        } finally {
            subjectCache.unlock();
        }
    }

    @Override
    public Integer deleteSchemaVersion(
            String subject,
            String version,
            boolean isPermanent)
            throws IOException, RestClientException {
        return deleteSchemaVersion(DEFAULT_REQUEST_PROPERTIES, subject, version);
    }

    @Override
    public Integer deleteSchemaVersion(
            String subject,
            String version) throws IOException, RestClientException {
        return deleteSchemaVersion(DEFAULT_REQUEST_PROPERTIES, subject, version);
    }

    @Override
    public Integer deleteSchemaVersion(
            Map<String, String> requestProperties,
            String subject,
            String version,
            boolean isPermanent)
            throws IOException, RestClientException {
        return deleteSchemaVersion(DEFAULT_REQUEST_PROPERTIES, subject, version);
    }

    @Override
    public Integer deleteSchemaVersion(
            Map<String, String> requestProperties,
            String subject,
            String version) throws IOException, RestClientException {
        Validate.notBlank(subject, "Subject is blank");

        int versionInt = Integer.parseInt(version);
        Validate.isTrue(versionInt >= 0 , "Version is negative");

        SubjectCache subjectCache = getSubjectCache(subject);
        subjectCache.lock();
        try {
            ParsedSchema schema = getSchemaByVersionFromRegistryQuietly(subject, versionInt);
            subjectCache.removeSchema(schema);
            subjectCache.removeSchemaByVersion(versionInt);
            return restService.deleteSchemaVersion(requestProperties, subject, version);
        } finally {
            subjectCache.unlock();
        }
    }

    @Override
    public String setMode(String mode) throws IOException, RestClientException {
        ModeUpdateRequest response = restService.setMode(mode);
        return response.getMode();
    }

    @Override
    public String setMode(String mode, String subject) throws IOException, RestClientException {
        ModeUpdateRequest response = restService.setMode(mode, subject);
        return response.getMode();
    }

    @Override
    public String getMode() throws IOException, RestClientException {
        Mode response = restService.getMode();
        return response.getMode();
    }

    @Override
    public String getMode(String subject) throws IOException, RestClientException {
        Mode response = restService.getMode(subject);
        return response.getMode();
    }

    @Override
    public void deleteMode(String subject) throws IOException, RestClientException {
        restService.deleteSubjectMode(subject);
    }

    @Override
    public void reset() {
        schemaCache.clear();
        subjectCache.clear();
    }

    private List<String> getAllSubjectsByIdFromRegistry(int id) throws IOException, RestClientException {
        return this.restService.getAllSubjectsById(id);
    }

    private List<ParsedSchema> getSchemasFromRegistry(
            String subjectPrefix,
            boolean lookupDeletedSchema,
            boolean latestOnly) throws IOException, RestClientException {
        return restService.getSchemas(subjectPrefix, lookupDeletedSchema, latestOnly).stream()
                .map(schema -> parseSchema(schema.getSchemaType(), schema.getSchema(), schema.getReferences()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private SubjectCache getSubjectCache(String subject) {
        return subjectCache.computeIfAbsent(subject, key -> new SubjectCache(subject));
    }

    private int registerAndGetId(
            String subject,
            ParsedSchema schema) throws IOException, RestClientException {
        int id = restService.registerSchema(schema.toString(), subject);
        schemaCache.put(id, schema);
        return id;
    }

    private int registerAndGetId(
            String subject,
            ParsedSchema schema,
            int version,
            int id) throws IOException, RestClientException {
        return restService.registerSchema(schema.toString(), subject, version, id);
    }

    private ParsedSchema getSchemaByIdFromRegistry(int id) throws IOException, RestClientException {
        ParsedSchema schema = schemaCache.get(id);
        if (schema != null) {
            return schema;
        }

        SchemaString schemaString = restService.getId(id);
        schema = new AvroSchema(schemaString.getSchemaString());
        schemaCache.put(id, schema);
        return schema;
    }

    private ParsedSchema getSchemaByVersionFromRegistryQuietly(String subject, int version) {
        try {
            io.confluent.kafka.schemaregistry.client.rest.entities.Schema response =
                    restService.getVersion(subject, version);
            ParsedSchema schema = new AvroSchema(response.getSchema());
            schemaCache.put(response.getId(), schema);
            return schema;
        } catch (IOException | RestClientException ex) {
            return null;
        }
    }

    private int getSchemaIdFromRegistry(
            String subject,
            ParsedSchema schema) throws IOException, RestClientException {
        io.confluent.kafka.schemaregistry.client.rest.entities.Schema response =
                restService.lookUpSubjectVersion(schema.toString(), subject, false);
        schemaCache.putIfAbsent(response.getId(), schema);
        return response.getId();
    }

    private int getSchemaVersionFromRegistry(
            String subject,
            ParsedSchema schema) throws IOException, RestClientException {
        io.confluent.kafka.schemaregistry.client.rest.entities.Schema response =
                restService.lookUpSubjectVersion(schema.toString(), subject, true);
        schemaCache.putIfAbsent(response.getId(), schema);
        return response.getVersion();
    }

    private SchemaMetadata getSchemaMetadataFromRegistry(
            String subject,
            Integer version) throws IOException, RestClientException {
        io.confluent.kafka.schemaregistry.client.rest.entities.Schema response;
        if (version != null) {
            response = restService.getVersion(subject, version);
        } else {
            response = restService.getLatestVersion(subject);
        }
        int id = response.getId();
        int versionActual = response.getVersion();
        String schema = response.getSchema();
        return new SchemaMetadata(id, versionActual, schema);
    }

    private ParsedSchema convert(Schema schema) {
        return new AvroSchema(schema);
    }

    private class SubjectCache {

        private final String subject;
        private final Map<ParsedSchema, Integer> schemaIds = new HashMap<>();
        private final Map<Integer, ParsedSchema> idSchemas = new HashMap<>();
        private final Map<ParsedSchema, Integer> schemaVersions = new HashMap<>();
        private final Map<Integer, ParsedSchema> versionSchemas = new HashMap<>();

        private final Lock lock = new ReentrantLock();

        public void lock() {
            lock.lock();
        }

        public void unlock() {
            lock.unlock();
        }

        public SubjectCache(String subject) {
            this.subject = subject;
        }

        public ParsedSchema getSchemaById(int id) {
            return idSchemas.get(id);
        }

        public Integer getIdBySchema(ParsedSchema schema) {
            return schemaIds.get(schema);
        }

        public Integer getVersionBySchema(ParsedSchema schema) {
            return schemaVersions.get(schema);
        }

        public void addSchemaWithId(ParsedSchema schema, int id) {
            if (schemaIds.size() >= maxSchemasPerSubject) {
                throw new IllegalStateException("Too many schema objects created for " + subject + "!");
            }

            schemaIds.put(schema, id);
            idSchemas.put(id, schema);
        }

        public void addSchemaWithVersion(ParsedSchema schema, int version) {
            if (schemaVersions.size() >= maxSchemasPerSubject) {
                throw new IllegalStateException("Too many schema objects created for " + subject + "!");
            }

            schemaVersions.put(schema, version);
            versionSchemas.put(version, schema);
        }

        public void removeSchemaByVersion(int version) {
            removeSchema(versionSchemas.get(version));
        }

        public void removeSchema(ParsedSchema schema) {
            if (schema == null) {
                return;
            }

            Integer id = schemaIds.remove(schema);
            if (id != null) {
                idSchemas.remove(id);
            }

            Integer version = schemaVersions.remove(schema);
            if (version != null) {
                versionSchemas.remove(version);
            }
        }

        public void clear() {
            schemaIds.clear();
            idSchemas.clear();
            schemaVersions.clear();
            versionSchemas.clear();
        }

    }

}
