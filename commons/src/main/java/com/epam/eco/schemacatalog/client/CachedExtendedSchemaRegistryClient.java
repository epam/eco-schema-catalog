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

import com.epam.eco.commons.avro.modification.CachedSchemaModifications;
import com.epam.eco.commons.avro.modification.SchemaModification;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.domain.schema.SubjectAndSchema;
import com.epam.eco.schemacatalog.domain.schema.SubjectAndVersion;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;
import com.epam.eco.schemacatalog.utils.UrlListExtractor;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

/**
 * @author Raman_Babich
 */
public class CachedExtendedSchemaRegistryClient extends EcoCachedSchemaRegistryClient implements ExtendedSchemaRegistryClient {

    private static final int DEFAULT_IDENTITY_MAP_CAPACITY = 10000;

    private final SchemaRegistryServiceInfo schemaRegistryServiceInfo;

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
    }

    public CachedExtendedSchemaRegistryClient(String baseUrl) {
        this(baseUrl, DEFAULT_IDENTITY_MAP_CAPACITY);
    }

    public CachedExtendedSchemaRegistryClient(String baseUrl, int identityMapCapacity) {
        super(new ExtendedRestService(baseUrl), identityMapCapacity);

        this.schemaRegistryServiceInfo = SchemaRegistryServiceInfo.with(baseUrl);
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
    public List<Integer> getAllVersionsUnchecked(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        try {
            return getAllVersions(subject);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Deprecated
    public Schema getBySubjectAndVersion(String subject, int version) {
        return getSchemaInfo(subject, version).getSchemaAvro();
    }

    @Override
    public ParsedSchema getSchemaBySubjectAndVersion(String subject, int version) {
        return getSchemaInfo(subject, version).getParsedSchema();
    }

    @Deprecated
    public AvroCompatibilityLevel getGlobalCompatibilityLevel() {
        return AvroCompatibilityLevel.forName(getGlobalLevelOfCompatibility().name());
    }

    @Override
    public CompatibilityLevel getGlobalLevelOfCompatibility() {
        return getCompatibilityLevelOrNullIfNotFound(null);
    }

    @Deprecated
    public Optional<AvroCompatibilityLevel> getCompatibilityLevel(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        Optional<CompatibilityLevel> optional = getLevelOfCompatibility(subject);
        return optional.map(compatibilityLevel -> AvroCompatibilityLevel.forName(compatibilityLevel.name()));
    }

    @Override
    public Optional<CompatibilityLevel> getLevelOfCompatibility(String subject) {
        return Optional.ofNullable(getCompatibilityLevelOrNullIfNotFound(subject));
    }

    @Deprecated
    public AvroCompatibilityLevel getEffectiveCompatibilityLevel(String subject) {
        return AvroCompatibilityLevel.forName(getEffectiveLevelOfCompatibility(subject).name());
    }

    @Override
    public CompatibilityLevel getEffectiveLevelOfCompatibility(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        CompatibilityLevel compatibilityLevel = getCompatibilityLevelOrNullIfNotFound(subject);
        if (compatibilityLevel == null) {
            compatibilityLevel = getCompatibilityLevelOrNullIfNotFound(null); // global
        }

        if (compatibilityLevel == null) {
            throw new RuntimeException(
                    String.format(
                            "Can't determine effective compatibility level for subject '%s'",
                            subject));
        }

        return compatibilityLevel;
    }

    @Override
    public Mode getModeValue() {
        try {
            return Mode.valueOf(getMode());
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<Mode> getModeValue(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return Optional.ofNullable(getModeValueOrNullIfNotFound(subject));
    }

    @Override
    public Mode getEffectiveModeValue(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        Mode mode = getModeValueOrNullIfNotFound(subject);
        if (mode == null) {
            mode = getModeValue(); // global
        }
        return mode;
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

        List<Integer> versions = getAllVersionsUnchecked(subject);
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

    @Deprecated
    public void updateCompatibility(String subject, AvroCompatibilityLevel compatibilityLevel) {
        updateCompatibility(subject, CompatibilityLevel.forName(compatibilityLevel.name));
    }

    @Override
    public void updateCompatibility(String subject, CompatibilityLevel compatibilityLevel) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(compatibilityLevel, "Compatibility level is null");

        try {
            updateCompatibility(subject, compatibilityLevel.name());
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateMode(String subject, Mode mode) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(mode, "Mode is null");

        try {
            setMode(mode.name(), subject);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean subjectExists(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return
                getAllCachedSubjectsOrRefreshUnchecked(false).contains(subject) ||
                getAllCachedSubjectsOrRefreshUnchecked(true).contains(subject);
    }

    @Override
    public List<Integer> deleteSubjectUnchecked(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        try {
            return deleteSubject(subject);
        } catch (IOException | RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer deleteSchema(String subject, int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        try {
            return deleteSchemaVersion(subject, Integer.toString(version));
        } catch (IOException | RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> deleteSubject(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        List<Integer> versions = super.deleteSubject(requestProperties, subject);

        removeFromSchemaCache(subject, null);
        removeFromSubjectCache(subject);
        removeFromWritableVersionCache(subject, null);
        removeFromWritableSchemaCache(subject, null);

        return versions;
    }

    @Override
    public Integer deleteSchemaVersion(
            Map<String, String> requestProperties,
            String subject,
            String version) throws IOException, RestClientException {
        Integer versionInt = Integer.valueOf(version);
        ParsedSchema schema = getSchemaBySubjectAndVersion(subject, versionInt);

        Integer deleted = super.deleteSchemaVersion(requestProperties, subject, version);

        removeFromSchemaCache(subject, versionInt);
        removeFromWritableVersionCache(subject, versionInt);
        removeFromWritableSchemaCache(subject, schema);

        return deleted;
    }

    @Deprecated
    public boolean testCompatibilityUnchecked(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        return testCompatibilityUnchecked(subject, new AvroSchema(schema));
    }

    @Override
    public boolean testCompatibilityUnchecked(String subject, ParsedSchema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        try {
            return testCompatibility(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Deprecated
    public int registerUnchecked(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        return registerUnchecked(subject, new AvroSchema(schema));
    }

    @Override
    public int registerUnchecked(String subject, ParsedSchema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        try {
            return register(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Deprecated
    public int getVersionUnchecked(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        return getVersionUnchecked(subject, new AvroSchema(schema));
    }

    @Override
    public int getVersionUnchecked(String subject, ParsedSchema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        try {
            return getVersion(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Deprecated
    public boolean checkSchemaWritable(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        return checkSchemaWritable(subject, new AvroSchema(schema));
    }

    @Deprecated
    public boolean checkSchemaWritable(String subject, ParsedSchema schema) {
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

    private boolean checkSchemaExists(String subject, ParsedSchema schema) {
        try {
            getId(subject, schema);
            return true;
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
            getSchemaMetadataOrLatest(subject, version);
            return true;
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

    private Set<String> getAllCachedSubjectsOrRefreshUnchecked(boolean forceRefresh) {
        try {
            if (forceRefresh) {
                subjectCache.addAll(getAllSubjects());
            }
            return subjectCache;
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void replicateCompatibilityIfNeeded(String sourceSubject, String destinationSubject) {
        CompatibilityLevel sourceCompatibilityLevel =
                getCompatibilityLevelOrNullIfNotFound(sourceSubject);
        if (sourceCompatibilityLevel == null) {
            return;
        }

        CompatibilityLevel destinationCompatibilityLevel =
                getCompatibilityLevelOrNullIfNotFound(destinationSubject);
        if (destinationCompatibilityLevel != null) {
            return;
        }

        updateCompatibility(destinationSubject, sourceCompatibilityLevel);
    }

    private BasicSchemaInfo getCachedSchemaInfoOrRefresh(
            String subject,
            Integer version,
            Consumer<BasicSchemaInfo> initConsumer) {
        if (version != null) {
            SubjectAndVersion key = SubjectAndVersion.with(subject, version);
            return schemaCache.computeIfAbsent(
                    key,
                    k -> getSchemaMetadataAndConvertToInfo(subject, version, initConsumer));
        } else {
            BasicSchemaInfo schemaInfo = getSchemaMetadataAndConvertToInfo(subject, null, initConsumer);
            schemaCache.put(
                    SubjectAndVersion.of(schemaInfo),
                    schemaInfo);
            return schemaInfo;
        }
    }

    private BasicSchemaInfo getSchemaMetadataAndConvertToInfo(
            String subject,
            Integer version,
            Consumer<BasicSchemaInfo> initConsumer) {
        SchemaMetadata schemaMetadata = getSchemaMetadataOrLatestUnchecked(subject, version);
        BasicSchemaInfo schemaInfo = toSchemaInfo(subject, schemaMetadata);
        if (initConsumer != null) {
            initConsumer.accept(schemaInfo);
        }
        return schemaInfo;
    }

    private SchemaMetadata getSchemaMetadataOrLatestUnchecked(String subject, Integer version) {
        try {
            return getSchemaMetadataOrLatest(subject, version);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private SchemaMetadata getSchemaMetadataOrLatest(
            String subject,
            Integer version) throws IOException, RestClientException {
        if (version != null) {
            return getSchemaMetadata(subject, version.intValue());
        } else {
            return getLatestSchemaMetadata(subject);
        }
    }

    private CompatibilityLevel getCompatibilityLevelOrNullIfNotFound(String subject) {
        CompatibilityLevel compatibilityLevel = null;
        try {
            compatibilityLevel = CompatibilityLevel.forName(getCompatibility(subject));
        } catch (RestClientException rce) {
            if (subject == null || !isNotFoundError(rce)) {
                throw new RuntimeException(rce);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return compatibilityLevel;
    }

    private Mode getModeValueOrNullIfNotFound(String subject) {
        Mode mode = null;
        try {
            mode = Mode.valueOf(getMode(subject));
        } catch (RestClientException rce) {
            if (subject == null || !isNotFoundError(rce)) {
                throw new RuntimeException(rce);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return mode;
    }

    private BasicSchemaInfo toSchemaInfo(String subject, SchemaMetadata schemaMetadata) {
        return BasicSchemaInfo.builder().
                subject(subject).
                version(schemaMetadata.getVersion()).
                schemaRegistryId(schemaMetadata.getId()).
                schemaJson(schemaMetadata.getSchema()).
                build();
    }

    private void removeFromSchemaCache(String subject, Integer version) {
        if (version == null) {
            schemaCache.keySet().removeIf(sav -> sav.getSubject().equals(subject));
        } else {
            schemaCache.remove(SubjectAndVersion.with(subject, version));
        }
    }

    private void removeFromSubjectCache(String subject) {
        subjectCache.remove(subject);
    }

    private void removeFromWritableVersionCache(String subject, Integer version) {
        if (version == null) {
            writableVersionCache.keySet().removeIf(sav -> sav.getSubject().equals(subject));
        } else {
            writableVersionCache.remove(SubjectAndVersion.with(subject, version));
        }
    }

    private void removeFromWritableSchemaCache(String subject, ParsedSchema schema) {
        if (schema == null) {
            writableSchemaCache.keySet().removeIf(sas -> sas.getSubject().equals(subject));
        } else {
            writableSchemaCache.remove(SubjectAndSchema.with(subject, schema));
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

}
