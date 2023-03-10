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
package com.epam.eco.schemacatalog.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.SchemaValidationException;
import org.apache.avro.SchemaValidator;
import org.apache.avro.SchemaValidatorBuilder;
import org.apache.commons.lang3.Validate;

import com.epam.eco.commons.avro.validation.DetailedSchemaValidationException;
import com.epam.eco.commons.avro.validation.DetailedValidateCanBeRead;
import com.epam.eco.commons.avro.validation.DetailedValidateCanRead;
import com.epam.eco.commons.avro.validation.DetailedValidateMutualRead;

import io.confluent.kafka.schemaregistry.CompatibilityChecker;
import io.confluent.kafka.schemaregistry.CompatibilityLevel;
import io.confluent.kafka.schemaregistry.ParsedSchema;

/**
 * Uses "detailed" validators instead of binary ones. See {@link CompatibilityChecker}
 * for original implementation.
 *
 * @author Andrei_Tytsik
 */
public final class DetailedAvroCompatibilityChecker {

    private static final SchemaValidator BACKWARD_VALIDATOR =
        new SchemaValidatorBuilder().strategy(new DetailedValidateCanRead()).validateLatest();

    private static final SchemaValidator FORWARD_VALIDATOR =
        new SchemaValidatorBuilder().strategy(new DetailedValidateCanBeRead()).validateLatest();

    private static final SchemaValidator FULL_VALIDATOR =
        new SchemaValidatorBuilder().strategy(new DetailedValidateMutualRead()).validateLatest();

    private static final SchemaValidator BACKWARD_TRANSITIVE_VALIDATOR =
        new SchemaValidatorBuilder().strategy(new DetailedValidateCanRead()).validateAll();

    private static final SchemaValidator FORWARD_TRANSITIVE_VALIDATOR =
        new SchemaValidatorBuilder().strategy(new DetailedValidateCanBeRead()).validateAll();

    private static final SchemaValidator FULL_TRANSITIVE_VALIDATOR =
        new SchemaValidatorBuilder().strategy(new DetailedValidateMutualRead()).validateAll();

    private static final SchemaValidator NO_OP_VALIDATOR = (schema, schemas) -> {/* do nothing */};

    private static final Map<CompatibilityLevel, DetailedAvroCompatibilityChecker> MAPPING =
            new EnumMap<>(CompatibilityLevel.class);
    static {
        MAPPING.put(
                CompatibilityLevel.NONE,
                new DetailedAvroCompatibilityChecker(NO_OP_VALIDATOR));
        MAPPING.put(
                CompatibilityLevel.BACKWARD,
                new DetailedAvroCompatibilityChecker(BACKWARD_VALIDATOR));
        MAPPING.put(
                CompatibilityLevel.BACKWARD_TRANSITIVE,
                new DetailedAvroCompatibilityChecker(BACKWARD_TRANSITIVE_VALIDATOR));
        MAPPING.put(
                CompatibilityLevel.FORWARD,
                new DetailedAvroCompatibilityChecker(FORWARD_VALIDATOR));
        MAPPING.put(
                CompatibilityLevel.FORWARD_TRANSITIVE,
                new DetailedAvroCompatibilityChecker(FORWARD_TRANSITIVE_VALIDATOR));
        MAPPING.put(
                CompatibilityLevel.FULL,
                new DetailedAvroCompatibilityChecker(FULL_VALIDATOR));
        MAPPING.put(
                CompatibilityLevel.FULL_TRANSITIVE,
                new DetailedAvroCompatibilityChecker(FULL_TRANSITIVE_VALIDATOR));
    }

    private final SchemaValidator validator;

    private DetailedAvroCompatibilityChecker(SchemaValidator validator) {
        this.validator = validator;
    }

    @Deprecated
    public void testCompatibility(
            Schema newSchema,
            Schema latestSchema) throws DetailedSchemaValidationException {
        testCompatibility(newSchema, Collections.singletonList(latestSchema));
    }

    public void testCompatibility(
            Schema newSchema,
            ParsedSchema latestSchema) throws DetailedSchemaValidationException {
        testCompatibility(
                newSchema,
                Collections.singletonList((Schema)latestSchema.rawSchema()));
    }

    public void testCompatibility(
            ParsedSchema newSchema,
            Schema latestSchema) throws DetailedSchemaValidationException {
        testCompatibility(
                (Schema) newSchema.rawSchema(),
                Collections.singletonList(latestSchema));
    }

    public void testCompatibility(
            ParsedSchema newSchema,
            ParsedSchema latestSchema) throws DetailedSchemaValidationException {
        testCompatibility(
                (Schema) newSchema.rawSchema(),
                Collections.singletonList((Schema)latestSchema.rawSchema()));
    }

    public void testCompatibility(
            Schema newSchema,
            List<Schema> previousSchemas) throws DetailedSchemaValidationException {
        Validate.notNull(newSchema, "New Schema is null");
        Validate.notNull(previousSchemas, "Collection of Previous Schemas is null");

        List<Schema> previousSchemasCopy = new ArrayList<>(previousSchemas);
        try {
            // Validator checks in list order, but checks should occur in
            // reverse chronological order
            Collections.reverse(previousSchemasCopy);
            validator.validate(newSchema, previousSchemasCopy);
        } catch (SchemaValidationException sve) {
            throw (DetailedSchemaValidationException)sve;
        }
    }

    public void testCompatibility(
            ParsedSchema newSchema,
            List<?> previousSchemas) throws DetailedSchemaValidationException {
        Validate.notNull(newSchema, "New Schema is null");
        Validate.notNull(previousSchemas, "Collection of Previous Schemas is null");

        Schema rawNewSchema = (Schema) newSchema.rawSchema();

        if (!previousSchemas.isEmpty()) {
            Object o = previousSchemas.get(0);
            if (o instanceof ParsedSchema) {
                List<Schema> parsedSchemas = previousSchemas.stream()
                        .map(ps -> (ParsedSchema)ps)
                        .map(ParsedSchema::rawSchema)
                        .map(s -> (Schema)s)
                        .collect(Collectors.toList());

                testCompatibility(rawNewSchema, parsedSchemas);
            }
            if (o instanceof Schema) {
                List<Schema> parsedSchemas = previousSchemas.stream()
                        .map(s -> (Schema)s)
                        .collect(Collectors.toList());
                testCompatibility(rawNewSchema, parsedSchemas);
            }
        }
    }

    public static DetailedAvroCompatibilityChecker forLevel(CompatibilityLevel compatibilityLevel) {
        Validate.notNull(compatibilityLevel, "Compatibility Level is null");

        DetailedAvroCompatibilityChecker checker = MAPPING.get(compatibilityLevel);
        if (checker == null) {
            throw new IllegalArgumentException(
                    String.format("Compatibility Checker for level '%s' not found", compatibilityLevel));
        }

        return checker;
    }

}
