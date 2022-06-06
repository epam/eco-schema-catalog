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
package com.epam.eco.schemacatalog.fts;

import com.epam.eco.schemacatalog.domain.schema.Mode;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
public abstract class FtsConstants {

    public static final String FIELD_ECO_ID = "ecoId";
    public static final String FIELD_SCHEMA_REGISTRY_ID = "schemaRegistryId";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_VERSION_LATEST = "versionLatest";
    public static final String FIELD_COMPATIBILITY = "compatibility";
    public static final String FIELD_MODE = "mode";
    public static final String FIELD_ROOT_NAME = "rootName";
    public static final String FIELD_ROOT_NAMESPACE = "rootNamespace";
    public static final String FIELD_ROOT_FULLNAME = "rootFullname";
    public static final String FIELD_DELETED = "deleted";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_NAMESPACE = "namespace";
    public static final String FIELD_FULLNAME = "fullname";
    public static final String FIELD_DOC = "doc";
    public static final String FIELD_LOGICAL_TYPE = "logicalType";
    public static final String FIELD_METADATA = "metadata";
    public static final String FIELD_METADATA_DOC = FIELD_METADATA + ".doc";
    public static final String FIELD_METADATA_ATTRIBUTE = FIELD_METADATA + ".attribute";
    public static final String FIELD_METADATA_ATTRIBUTE_KEY = FIELD_METADATA_ATTRIBUTE + ".key";
    public static final String FIELD_METADATA_ATTRIBUTE_VALUE = FIELD_METADATA_ATTRIBUTE + ".value";
    public static final String FIELD_METADATA_UPDATED_BY = FIELD_METADATA + ".updatedBy";
    public static final String FIELD_PROPERTY = "property";
    public static final String FIELD_PROPERTY_KEY = FIELD_PROPERTY + ".key";
    public static final String FIELD_PROPERTY_VALUE = FIELD_PROPERTY + ".value";

    public static final int DEFAULT_VERSION_AGGREGATION_SIZE = 100;
    public static final int DEFAULT_VERSION_LATEST_AGGREGATION_SIZE = 2;
    public static final int DEFAULT_COMPATIBILITY_AGGREGATION_SIZE = CompatibilityLevel.values().length;
    public static final int DEFAULT_MODE_AGGREGATION_SIZE = Mode.values().length;
    public static final int DEFAULT_NAMESPACE_AGGREGATION_SIZE = 1000;
    public static final int DEFAULT_DELETED_AGGREGATION_SIZE = 10000;
    public static final int DEFAULT_METADATA_UPDATED_BY_AGGREGATION_SIZE = 10000;

    private FtsConstants() {
    }

    private static final AggregationParams DEFAULT_VERSION_AGGREGATION = new AggregationParams(
            FIELD_VERSION,
            FIELD_VERSION,
            DEFAULT_VERSION_AGGREGATION_SIZE);
    private static final AggregationParams DEFAULT_VERSION_LATEST_AGGREGATION = new AggregationParams(
            FIELD_VERSION_LATEST,
            FIELD_VERSION_LATEST,
            DEFAULT_VERSION_LATEST_AGGREGATION_SIZE);
    private static final AggregationParams DEFAULT_DELETED_AGGREGATION = new AggregationParams(
            FIELD_DELETED,
            FIELD_DELETED,
            DEFAULT_DELETED_AGGREGATION_SIZE);
    private static final AggregationParams DEFAULT_COMPATIBILITY_AGGREGATION = new AggregationParams(
            FIELD_COMPATIBILITY,
            FIELD_COMPATIBILITY,
            DEFAULT_COMPATIBILITY_AGGREGATION_SIZE);
    private static final AggregationParams DEFAULT_MODE_AGGREGATION = new AggregationParams(
            FIELD_MODE,
            FIELD_MODE,
            DEFAULT_MODE_AGGREGATION_SIZE);
    private static final AggregationParams DEFAULT_NAMESPANCE_AGGREGATION = new AggregationParams(
            FIELD_ROOT_NAMESPACE,
            FIELD_ROOT_NAMESPACE,
            DEFAULT_NAMESPACE_AGGREGATION_SIZE);
    private static final AggregationParams DEFAULT_METADATA_UPDATED_BY_AGGREGATION = new AggregationParams(
            FIELD_METADATA_UPDATED_BY,
            FIELD_METADATA_UPDATED_BY,
            DEFAULT_METADATA_UPDATED_BY_AGGREGATION_SIZE);

    public static AggregationParams defaultVersionAggregation() {
        return DEFAULT_VERSION_AGGREGATION.copyOf();
    }
    public static AggregationParams defaultVersionLatestAggregation() {
        return DEFAULT_VERSION_LATEST_AGGREGATION.copyOf();
    }
    public static AggregationParams defaultDeletedAggregation() {
        return DEFAULT_DELETED_AGGREGATION.copyOf();
    }
    public static AggregationParams defaultCompatibilityAggregation() {
        return DEFAULT_COMPATIBILITY_AGGREGATION.copyOf();
    }
    public static AggregationParams defaultModeAggregation() {
        return DEFAULT_MODE_AGGREGATION.copyOf();
    }
    public static AggregationParams defaultNamespaceAggregation() {
        return DEFAULT_NAMESPANCE_AGGREGATION.copyOf();
    }
    public static AggregationParams defaultMetadataUpdatedByAggregation() {
        return DEFAULT_METADATA_UPDATED_BY_AGGREGATION.copyOf();
    }

}
