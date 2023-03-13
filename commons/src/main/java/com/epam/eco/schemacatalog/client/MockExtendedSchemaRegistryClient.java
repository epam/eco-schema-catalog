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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import com.epam.eco.commons.avro.modification.CachedSchemaModifications;
import com.epam.eco.commons.avro.modification.SchemaModification;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.rest.entities.Config;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

import static java.util.Collections.emptyMap;

/**
 * @author Aliaksei_Valyaev
 */
public final class MockExtendedSchemaRegistryClient
        extends MockSchemaRegistryClient
        implements ExtendedSchemaRegistryClient {

    @Override
    public SchemaRegistryServiceInfo getServiceInfo() {
        return SchemaRegistryServiceInfo.with("fake-url.com");
    }

    @Deprecated
    public Schema getBySubjectAndVersion(String subject, int version) {
        return getSchemaInfo(subject, version).getSchemaAvro();
    }

    @Override
    public ParsedSchema getSchemaBySubjectAndVersion(String subject, int version) {
        return getSchemaInfo(subject, version).getParsedSchema();
    }

    @Override
    public CompatibilityLevel getGlobalCompatibilityLevel() {
        return retrieveCompatibility(null);
    }

    @Override
    public Optional<CompatibilityLevel> getCompatibilityLevel(String subject) {
        Validate.notBlank(subject, "Subject is blank");
        return Optional.ofNullable(retrieveCompatibility(subject));
    }

    @Override
    public CompatibilityLevel getEffectiveCompatibilityLevel(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        CompatibilityLevel compatibilityLevel = retrieveCompatibility(subject);
        if (compatibilityLevel == null) {
            compatibilityLevel = retrieveCompatibility(null); // global
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
        return retrieveMode();
    }

    @Override
    public Optional<Mode> getModeValue(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return Optional.ofNullable(retrieveMode(subject));
    }

    @Override
    public Mode getEffectiveModeValue(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        Mode mode = retrieveMode(subject);
        if (mode == null) {
            mode = retrieveMode();
        }
        return mode;
    }

    @Override
    public BasicSchemaInfo getSchemaInfo(String subject, int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        return getCachedSchemaInfoOrRetrieve(subject, version, null);
    }

    @Override
    public BasicSchemaInfo getLatestSchemaInfo(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return getCachedSchemaInfoOrRetrieve(subject, null, null);
    }

    @Override
    public SubjectSchemas<BasicSchemaInfo> getSubjectSchemaInfos(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        List<BasicSchemaInfo> schemaInfos = new ArrayList<>();

        List<Integer> versions;
        try {
            versions = getAllVersions(subject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        versions.forEach(version -> schemaInfos.add(getCachedSchemaInfoOrRetrieve(subject, version, null)));

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

        return getCachedSchemaInfoOrRetrieve(
                destinationSubject,
                destinationVersion,
                schemaInfo -> replicateCompatibilityIfNeeded(sourceSubject, destinationSubject));
    }

    @Override
    public void updateCompatibility(String subject, CompatibilityLevel compatibilityLevel) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(compatibilityLevel, "Compatibility level is null");

        updateCompatibilityUnchecked(subject, compatibilityLevel);
    }

    @Override
    public void updateMode(String subject, Mode mode) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(mode, "Mode is null");

        updateModeUnchecked(subject, mode);
    }

    @Override
    public boolean subjectExists(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return getSchemaCache().containsKey(subject);
    }

    @Override
    public List<Integer> deleteSubjectUnchecked(String subject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer deleteSchema(String subject, int version) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public int getVersionUnchecked(String subject, Schema schema) {
        return getVersionUnchecked(subject, new AvroSchema(schema));
    }

    @Override
    public int getVersionUnchecked(String subject, ParsedSchema schema) {
        Map<ParsedSchema, Integer> versionCache;
        if (getVersionsCache().containsKey(subject)) {
            versionCache = getVersionsCache().get(subject);
            for (Map.Entry<ParsedSchema, Integer> entry : versionCache.entrySet()) {
                if (entry.getKey().toString().equals(schema.toString())) {
                    return entry.getValue();
                }
            }
        }
        throw new RuntimeException("Cannot get version from schema registry!");
    }

    @Deprecated
    public boolean testCompatibilityUnchecked(String subject, Schema schema) {
        try {
            return testCompatibility(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean testCompatibilityUnchecked(String subject, ParsedSchema schema) {
        try {
            return testCompatibility(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Deprecated
    public int registerUnchecked(String subject, Schema schema) {
        return registerUnchecked(subject, new AvroSchema(schema));
    }

    @Override
    public int registerUnchecked(String subject, ParsedSchema schema) {
        Map<ParsedSchema, Integer> schemaIdMap;
        if (getSchemaCache().containsKey(subject)) {
            schemaIdMap = getSchemaCache().get(subject);
        } else {
            schemaIdMap = new IdentityHashMap<>();
            getSchemaCache().put(subject, schemaIdMap);
        }

        Integer id = schemaIdMap.get(schema);
        if (id == null) {
            id = getIdFromRegistry(subject, schema);
            schemaIdMap.put(schema, id);
            getIdCache().get(":.:").put(id, schema);
        }
        return id;
    }

    @Override
    public Collection<String> getAllSubjectsUnchecked() {
        try {
            return getAllSubjects();
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Integer> getAllVersionsUnchecked(String subject) {
        return new ArrayList<>(getVersionsCache().getOrDefault(subject, emptyMap()).values());
    }

    @Deprecated
    public boolean checkSchemaWritable(String subject, Schema schema) {
        return true;
    }

    @Override
    public boolean checkSchemaWritable(String subject, ParsedSchema schema) {
        return true;
    }

    @Override
    public boolean checkSchemaWritable(String subject, int version) {
        return true;
    }

    @Override
    public boolean checkLatestSchemaWritable(String subject) {
        return true;
    }

    private void replicateCompatibilityIfNeeded(String sourceSubject, String destinationSubject) {
        CompatibilityLevel sourceCompatibilityLevel =
                retrieveCompatibility(sourceSubject);
        if (sourceCompatibilityLevel == null) {
            return;
        }

        CompatibilityLevel destinationCompatibilityLevel =
                retrieveCompatibility(destinationSubject);
        if (destinationCompatibilityLevel != null) {
            return;
        }

        updateCompatibilityUnchecked(destinationSubject, sourceCompatibilityLevel);
    }

    private BasicSchemaInfo getCachedSchemaInfoOrRetrieve(
            String subject,
            Integer version,
            Consumer<BasicSchemaInfo> initConsumer) {
        return retrieveSchemaAndConvertToInfo(subject, version, initConsumer);
    }

    private BasicSchemaInfo retrieveSchemaAndConvertToInfo(
            String subject,
            Integer version,
            Consumer<BasicSchemaInfo> initConsumer) {
        SchemaMetadata schemaMetadata;
        if (version == null) {
            try {
                schemaMetadata = getLatestSchemaMetadata(subject);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                schemaMetadata = super.getSchemaMetadata(subject, version);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        BasicSchemaInfo schemaInfo = toSchemaInfo(subject, schemaMetadata);
        if (initConsumer != null) {
            initConsumer.accept(schemaInfo);
        }
        return schemaInfo;
    }

    private Mode retrieveMode() {
        try {
            String mode = super.getMode();
            return mode != null ? Mode.valueOf(mode) : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CompatibilityLevel retrieveCompatibility(String subject) {
        String compatibility = null;
        try {
            compatibility = super.getCompatibility(subject);
        } catch (RestClientException e) {
            // if it is not found then keep compatibility == null
            if (!isNotFoundError(e)) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            if (compatibility == null) {
                compatibility = super.getCompatibility(null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return compatibility != null ? CompatibilityLevel.forName(compatibility) : null;
    }

    private Mode retrieveMode(String subject) {
        try {
            String mode = super.getMode(subject);
            return mode != null ? Mode.valueOf(mode) : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getIdFromRegistry(String subject, ParsedSchema schema) {
        Map<Integer, ParsedSchema> idSchemaMap;
        if (getIdCache().containsKey(subject)) {
            idSchemaMap = getIdCache().get(subject);
            for (Map.Entry<Integer, ParsedSchema> entry : idSchemaMap.entrySet()) {
                if (entry.getValue().toString().equals(schema.toString())) {
                    return entry.getKey();
                }
            }
        }

        return (int) callClientMethod(
                "getIdFromRegistry",
                new Class[] {String.class, ParsedSchema.class, boolean.class, int.class},
                subject, schema, Boolean.TRUE, -1);
    }

    private void updateCompatibilityUnchecked(String subject, CompatibilityLevel compatibilityLevel) {
        try {
            updateCompatibility(subject, compatibilityLevel.name());
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void updateModeUnchecked(String subject, Mode mode) {
        try {
            setMode(mode.name(), subject);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private BasicSchemaInfo toSchemaInfo(
            String subject,
            SchemaMetadata schemaMetadata) {
        return BasicSchemaInfo.builder().
                subject(subject).
                version(schemaMetadata.getVersion()).
                schemaRegistryId(schemaMetadata.getId()).
                schemaJson(schemaMetadata.getSchema()).
                build();
    }

    @SuppressWarnings("unused")
    private CompatibilityLevel toCompatibilityLevel(Config configEntity) {
        return CompatibilityLevel.forName(configEntity.getCompatibilityLevel());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<ParsedSchema, Integer>> getVersionsCache() {
        return getClientPrivateMap("versionCache");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<ParsedSchema, Integer>> getSchemaCache() {
        return getClientPrivateMap("schemaCache");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<Integer, ParsedSchema>> getIdCache() {
        return getClientPrivateMap("idCache");
    }

    @SuppressWarnings("rawtypes")
    private Map getClientPrivateMap(String name) {
        try {
            Field field = MockSchemaRegistryClient.class.getDeclaredField(name);
            field.setAccessible(true);
            Map map = (Map) field.get(this);
            return map == null ? emptyMap() : map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object callClientMethod(String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            Method method = MockSchemaRegistryClient.class
                    .getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(this, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNotFoundError(RestClientException rce) {
        /*
         * See io.confluent.kafka.schemaregistry.rest.exceptions.Errors
         * public static final int SUBJECT_NOT_FOUND_ERROR_CODE = 40401;
         * public static final int VERSION_NOT_FOUND_ERROR_CODE = 40402;
         * public static final int SCHEMA_NOT_FOUND_ERROR_CODE = 40403;
         * public static final int SUBJECT_LEVEL_COMPATIBILITY_NOT_CONFIGURED_ERROR_CODE = 40408;
         */
        return rce.getErrorCode() == 40401
                || rce.getErrorCode() == 40402
                || rce.getErrorCode() == 40403
                || rce.getErrorCode() == 40408;
    }
}
