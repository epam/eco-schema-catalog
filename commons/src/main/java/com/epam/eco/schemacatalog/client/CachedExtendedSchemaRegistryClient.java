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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.epam.eco.commons.avro.modification.CachedSchemaModifications;
import com.epam.eco.commons.avro.modification.SchemaModification;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SubjectAndSchema;
import com.epam.eco.schemacatalog.domain.schema.SubjectAndVersion;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;
import com.epam.eco.schemacatalog.utils.UrlListExtractor;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.client.rest.entities.Config;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

/**
 * @author Raman_Babich
 */
public class CachedExtendedSchemaRegistryClient extends CachedSchemaRegistryClient implements ExtendedSchemaRegistryClient {

    private static final int DEFAULT_IDENTITY_MAP_CAPACITY = 10000;

    private final SchemaRegistryServiceInfo schemaRegistryServiceInfo;
    private final RestService _restService;

    private final Map<SubjectAndVersion, BasicSchemaInfo> schemaCache = new ConcurrentHashMap<>();
    private final Set<String> subjectCache = ConcurrentHashMap.newKeySet();
    private final Map<SubjectAndSchema, Boolean> writableSchemaCache = new ConcurrentHashMap<>();
    private final Map<SubjectAndVersion, Boolean> writableVersionCache = new ConcurrentHashMap<>();

    public CachedExtendedSchemaRegistryClient(List<String> baseUrls) {
        this(baseUrls, DEFAULT_IDENTITY_MAP_CAPACITY);
    }

    public CachedExtendedSchemaRegistryClient(List<String> baseUrls, int identityMapCapacity) {
        super(new ExtendedRestService(baseUrls), identityMapCapacity);

        this.schemaRegistryServiceInfo = SchemaRegistryServiceInfo.with(baseUrls);
        this._restService = readRestServiceField();
    }

    public CachedExtendedSchemaRegistryClient(RestService restService) {
        this(restService, DEFAULT_IDENTITY_MAP_CAPACITY);
    }

    public CachedExtendedSchemaRegistryClient(
            RestService restService,
            int identityMapCapacity) {
        super(new ExtendedRestService(restService), identityMapCapacity);

        this.schemaRegistryServiceInfo = SchemaRegistryServiceInfo.with(
                UrlListExtractor.extract(restService.getBaseUrls()));
        this._restService = readRestServiceField();
    }

    public CachedExtendedSchemaRegistryClient(String baseUrl) {
        this(baseUrl, DEFAULT_IDENTITY_MAP_CAPACITY);
    }

    public CachedExtendedSchemaRegistryClient(String baseUrl, int identityMapCapacity) {
        super(new ExtendedRestService(baseUrl), identityMapCapacity);

        this.schemaRegistryServiceInfo = SchemaRegistryServiceInfo.with(baseUrl);
        this._restService = readRestServiceField();
    }

    private RestService readRestServiceField() {
        try {
            return (RestService)FieldUtils.readField(this, "restService", true);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        }
    }

    @Override
    public SchemaRegistryServiceInfo getServiceInfo() {
        return schemaRegistryServiceInfo;
    }

    @Override
    public Collection<String> getAllSubjectsUnchecked() {
        return getAllCachedSubjectsOrRefreshUnchecked(true);
    }

    @Override
    public List<Integer> getAllVersions(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        try {
            return _restService.getAllVersions(subject);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Schema getBySubjectAndVersion(String subject, int version) {
        return getSchemaInfo(subject, version).getSchemaAvro();
    }

    @Override
    public Optional<AvroCompatibilityLevel> getCompatibilityLevel(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return Optional.ofNullable(getConfigEntityAndConvertToCompatibility(subject));
    }

    @Override
    public AvroCompatibilityLevel getEffectiveCompatibilityLevel(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        AvroCompatibilityLevel compatilityLevel = getConfigEntityAndConvertToCompatibility(subject);
        if (compatilityLevel == null) {
            compatilityLevel = getConfigEntityAndConvertToCompatibility(null); // global
        }

        if (compatilityLevel == null) {
            throw new RuntimeException(
                    String.format(
                            "Can't determine effective compatibility level for subject '%s'",
                            subject));
        }

        return compatilityLevel;
    }

    @Override
    public BasicSchemaInfo getSchemaInfo(String subject, int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        return getCachedSchemaInfoOrRefresh(subject, version, null);
    }

    @Override
    public BasicSchemaInfo getLatestSchemaInfo(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return getCachedSchemaInfoOrRefresh(subject, null, null);
    }

    @Override
    public SubjectSchemas<BasicSchemaInfo> getSubjectSchemaInfos(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        List<BasicSchemaInfo> schemaInfos = new ArrayList<>();

        List<Integer> versions = getAllVersions(subject);
        versions.forEach(
                version -> schemaInfos.add(getCachedSchemaInfoOrRefresh(subject, version, null)));

        return SubjectSchemas.with(schemaInfos);
    }

    @Override
    public BasicSchemaInfo modifyAndRegisterSchema(
            String sourceSubject,
            int sourceVersion,
            String destinationSubject,
            SchemaModification... modifications) {
        return modifyAndRegisterSchema(
                sourceSubject,
                sourceVersion,
                destinationSubject,
                modifications != null ? Arrays.asList(modifications) : null);
    }

    @Override
    public BasicSchemaInfo modifyAndRegisterSchema(
            String sourceSubject,
            int sourceVersion,
            String destinationSubject,
            List<SchemaModification> modifications) {
        return modifyAndRegisterSchema(
                sourceSubject,
                getBySubjectAndVersion(sourceSubject, sourceVersion),
                destinationSubject,
                modifications);
    }

    @Override
    public BasicSchemaInfo modifyAndRegisterSchema(
            String sourceSubject,
            Schema sourceSchema,
            String destinationSubject,
            SchemaModification... modifications) {
        return modifyAndRegisterSchema(
                sourceSubject,
                sourceSchema,
                destinationSubject,
                modifications != null ? Arrays.asList(modifications) : null);
    }

    @Override
    public BasicSchemaInfo modifyAndRegisterSchema(
            String sourceSubject,
            Schema sourceSchema,
            String destinationSubject,
            List<SchemaModification> modifications) {
        Validate.notNull(sourceSchema, "Source schema is null");
        Validate.notBlank(sourceSubject, "Source subject is blank");
        Validate.notBlank(destinationSubject, "Destination subject is blank");

        Schema destinationSchema =
                CachedSchemaModifications.of(modifications).applyTo(sourceSchema);

        registerUnchecked(destinationSubject, destinationSchema);
        int destinationVersion = getVersionUnchecked(destinationSubject, destinationSchema);

        return getCachedSchemaInfoOrRefresh(
                destinationSubject,
                destinationVersion,
                schemaInfo -> replicateCompatibilityIfNeeded(sourceSubject, destinationSubject));
    }

    @Override
    public void updateCompatibility(String subject, AvroCompatibilityLevel compatibilityLevel) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(compatibilityLevel, "Compatibility level is null");

        updateCompatibilityUnchecked(subject, compatibilityLevel);
    }

    @Override
    public boolean subjectExists(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return
                getAllCachedSubjectsOrRefreshUnchecked(false).contains(subject) ||
                getAllCachedSubjectsOrRefreshUnchecked(true).contains(subject);
    }

    @Override
    public List<Integer> deleteSubject(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        try {
            return _restService.deleteSubject(subject);
        } catch (IOException | RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer deleteSchema(String subject, int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        try {
            return _restService.deleteSchemaVersion(subject, Integer.toString(version));
        } catch (IOException | RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean testCompatibilityUnchecked(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        try {
            return testCompatibility(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int registerUnchecked(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        try {
            return register(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int getVersionUnchecked(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        try {
            return getVersion(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean checkSchemaWritable(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        return writableSchemaCache.computeIfAbsent(
                SubjectAndSchema.with(subject, schema),
                k -> checkSchemaExists(subject, schema));
    }

    @Override
    public boolean checkSchemaWritable(String subject, int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        return writableVersionCache.computeIfAbsent(
                SubjectAndVersion.with(subject, version),
                k -> checkSchemaExists(subject, version));
    }

    @Override
    public boolean checkLatestSchemaWritable(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return checkSchemaExists(subject, (Integer)null);
    }

    private boolean checkSchemaExists(String subject, Schema schema) {
        try {
            return getSchemaEntity(subject, schema) != null;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } catch (RestClientException rce) {
            if (isNotFoundError(rce)) {
                return false;
            } else {
                throw new RuntimeException(rce);
            }
        }
    }

    private boolean checkSchemaExists(String subject, Integer version) {
        try {
            if (version != null) {
                return getSchemaEntity(subject, version) != null;
            } else {
                return getSchemaEntity(subject, (Integer)null) != null;
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } catch (RestClientException rce) {
            if (isNotFoundError(rce)) {
                return false;
            } else {
                throw new RuntimeException(rce);
            }
        }
    }

    private boolean isNotFoundError(RestClientException rce) {
        /*
         * See io.confluent.kafka.schemaregistry.rest.exceptions.Errors
         * public static final int SUBJECT_NOT_FOUND_ERROR_CODE = 40401;
         * public static final int VERSION_NOT_FOUND_ERROR_CODE = 40402;
         * public static final int SCHEMA_NOT_FOUND_ERROR_CODE = 40403;
         */
        return
                rce.getErrorCode() == 40401 ||
                rce.getErrorCode() == 40402 ||
                rce.getErrorCode() == 40403;
    }

    private Set<String> getAllCachedSubjectsOrRefreshUnchecked(boolean forceRefresh) {
        try {
            return getAllCachedSubjectsOrRefresh(forceRefresh);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Set<String> getAllCachedSubjectsOrRefresh(boolean forceRefresh) throws IOException, RestClientException {
        if (forceRefresh) {
            subjectCache.addAll(getAllSubjects());
        }
        return subjectCache;
    }

    private void replicateCompatibilityIfNeeded(String sourceSubject, String destinationSubject) {
        AvroCompatibilityLevel sourceCompatibilityLevel =
                getConfigEntityAndConvertToCompatibility(sourceSubject);
        if (sourceCompatibilityLevel == null) {
            return;
        }

        AvroCompatibilityLevel destinationCompatibilityLevel =
                getConfigEntityAndConvertToCompatibility(destinationSubject);
        if (destinationCompatibilityLevel != null) {
            return;
        }

        updateCompatibilityUnchecked(destinationSubject, sourceCompatibilityLevel);
    }

    private BasicSchemaInfo getCachedSchemaInfoOrRefresh(
            String subject,
            Integer version,
            Consumer<BasicSchemaInfo> initConsumer) {
        if (version != null) {
            SubjectAndVersion key = SubjectAndVersion.with(subject, version);
            return schemaCache.computeIfAbsent(
                    key,
                    k -> getSchemaEntityAndConvertToInfo(subject, version, initConsumer));
        } else {
            BasicSchemaInfo schemaInfo = getSchemaEntityAndConvertToInfo(subject, null, initConsumer);
            schemaCache.put(
                    SubjectAndVersion.of(schemaInfo),
                    schemaInfo);
            return schemaInfo;
        }
    }

    private BasicSchemaInfo getSchemaEntityAndConvertToInfo(
            String subject,
            Integer version,
            Consumer<BasicSchemaInfo> initConsumer) {
        io.confluent.kafka.schemaregistry.client.rest.entities.Schema schemaEntity =
                getSchemaEntityUnchecked(subject, version);
        BasicSchemaInfo schemaInfo = toSchemaInfo(schemaEntity);
        if (initConsumer != null) {
            initConsumer.accept(schemaInfo);
        }
        return schemaInfo;
    }

    private io.confluent.kafka.schemaregistry.client.rest.entities.Schema getSchemaEntityUnchecked(
            String subject,
            Integer version) {
        try {
            return getSchemaEntity(subject, version);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private io.confluent.kafka.schemaregistry.client.rest.entities.Schema getSchemaEntity(
            String subject,
            Integer version) throws IOException, RestClientException {
        if (version != null) {
            return _restService.getVersion(subject, version);
        } else {
            return _restService.getLatestVersion(subject);
        }
    }

    @SuppressWarnings("unused")
    private io.confluent.kafka.schemaregistry.client.rest.entities.Schema getSchemaEntityUnchecked(
            String subject,
            Schema schema) {
        try {
            return getSchemaEntity(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private io.confluent.kafka.schemaregistry.client.rest.entities.Schema getSchemaEntity(
            String subject,
            Schema schema) throws IOException, RestClientException {
        return _restService.lookUpSubjectVersion(schema.toString(), subject);
    }

    private AvroCompatibilityLevel getConfigEntityAndConvertToCompatibility(String subject) {
        Config configEntity = getConfigEntityUnchecked(subject);
        return configEntity != null ? toCompatibilityLevel(configEntity) : null;
    }

    private Config getConfigEntityUnchecked(String subject) {
        Config configEntity = null;
        try {
            configEntity = _restService.getConfig(subject);
        } catch (RestClientException rce) {
            /*
             * See io.confluent.kafka.schemaregistry.rest.exceptions.Errors
             * public static final int SUBJECT_NOT_FOUND_ERROR_CODE = 40401;
             */
            // supress 40401 "config not found" exception if subject isn't global
            if (subject == null || rce.getErrorCode() != 40401) {
                throw new RuntimeException(rce);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return configEntity;
    }

    private void updateCompatibilityUnchecked(String subject, AvroCompatibilityLevel compatibilityLevel) {
        try {
            updateCompatibility(subject, compatibilityLevel.name());
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private BasicSchemaInfo toSchemaInfo(
            io.confluent.kafka.schemaregistry.client.rest.entities.Schema schemaEntity) {
        return BasicSchemaInfo.builder().
                subject(schemaEntity.getSubject()).
                version(schemaEntity.getVersion()).
                schemaRegistryId(schemaEntity.getId()).
                schemaJson(schemaEntity.getSchema()).
                build();
    }

    private AvroCompatibilityLevel toCompatibilityLevel(Config configEntity) {
        return AvroCompatibilityLevel.forName(configEntity.getCompatibilityLevel());
    }

}
