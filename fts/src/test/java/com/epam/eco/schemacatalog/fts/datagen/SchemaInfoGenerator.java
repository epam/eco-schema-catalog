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
package com.epam.eco.schemacatalog.fts.datagen;

import java.util.Random;

import org.apache.avro.Schema;

import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
public class SchemaInfoGenerator {

    public static final String[] NAMES = {"name1", "name2", "name3", "name4", "enum1"};
    public static final String[] NAMESPACES = {"namespace1", "namespace2", "namespace3", "namespace4"};
    public static final String[] FULLNAMES = {NAMESPACES[0] + "." + NAMES[0], NAMESPACES[1] + "." + NAMES[1], NAMESPACES[2] + "." + NAMES[2], NAMESPACES[3] + "." + NAMES[3], NAMESPACES[3] + "." + NAMES[4]};
    public static final String[] FIELD_NAMES = {"f1","f2","f3","f4","f5","f6","f7","f8","f9","f10","f11","f12","f13","f14","f15","f16","f17","f18","f19","f20","f21","f22","f23","f24"};
    public static final String[] DOCS = {"doc1", "doc2", "doc3", "doc4"};
    public static final String[] LOGICAL_TYPES = {"time-millis", "timestamp-millis", "time-micros", "timestamp-micros"};
    public static final String[] PROP_KEYS = {"prop_key1", "prop_key2", "prop_key3", "prop_key4"};
    public static final String[] PROP_VALUES = {"prop_value1", "prop_value2", "prop_value3", "prop_value4"};

    public static final String[] PATHS = {
            FIELD_NAMES[0],
            FIELD_NAMES[1],
            FIELD_NAMES[2],
            FIELD_NAMES[3],

            FIELD_NAMES[3] + "." + FIELD_NAMES[4],
            FIELD_NAMES[3] + "." + FIELD_NAMES[5],
            FIELD_NAMES[3] + "." + FIELD_NAMES[6],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7],

            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[8],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[9],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[10],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11],

            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[12],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[13],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[14],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[15],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[16],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[17],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[18],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[19],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[20],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[21],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[22],
            FIELD_NAMES[3] + "." + FIELD_NAMES[7] + "." + FIELD_NAMES[11] + "." + FIELD_NAMES[23]

    };

    public static final String SCHEMA =
            "{\"type\":\"record\",\"name\":\"" + NAMES[0] + "\",\"namespace\":\"" + NAMESPACES[0] + "\",\"doc\":\"" + DOCS[0] + "\",\"fields\":"
                    + "["
                    + "{\"name\":\"" + FIELD_NAMES[0] + "\",\"type\":\"long\"},"
                    + "{\"name\":\"" + FIELD_NAMES[1] + "\",\"type\":[\"null\",\"long\"], \"" + PROP_KEYS[0] + "\":\"" + PROP_VALUES[0] + "\"},"
                    + "{\"name\":\"" + FIELD_NAMES[2] + "\",\"type\":[\"null\",{\"type\":\"int\",\"logicalType\":\"" + LOGICAL_TYPES[0] + "\"}]},"
                    + "{\"name\":\"" + FIELD_NAMES[3] + "\",\"type\":"
                    + "    {\"type\":\"record\",\"name\":\"" + NAMES[1] + "\",\"namespace\":\"" + NAMESPACES[1] + "\",\"doc\":\"" + DOCS[1] + "\",\"fields\":"
                    + "    ["
                    + "    {\"name\":\"" + FIELD_NAMES[4] + "\",\"type\":\"int\"},"
                    + "    {\"name\":\"" + FIELD_NAMES[5] + "\",\"type\":[\"null\",\"int\"]},"
                    + "    {\"name\":\"" + FIELD_NAMES[6] + "\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"" + LOGICAL_TYPES[1] + "\"}], \"" + PROP_KEYS[1] + "\":\"" + PROP_VALUES[1] + "\"},"
                    + "    {\"name\":\"" + FIELD_NAMES[7] + "\",\"type\":"
                    + "        {\"type\":\"record\",\"name\":\"" + NAMES[2] + "\",\"namespace\":\"" + NAMESPACES[2] + "\",\"doc\":\"" + DOCS[2] + "\",\"fields\":"
                    + "        ["
                    + "        {\"name\":\"" + FIELD_NAMES[8] + "\",\"type\":\"long\", \"" + PROP_KEYS[2] + "\":\"" + PROP_VALUES[2] + "\"},"
                    + "        {\"name\":\"" + FIELD_NAMES[9] + "\",\"type\":[\"null\",\"long\"]},"
                    + "        {\"name\":\"" + FIELD_NAMES[10] + "\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"" + LOGICAL_TYPES[2] + "\"}]},"
                    + "        {\"name\":\"" + FIELD_NAMES[11] + "\",\"type\":"
                    + "            {\"type\":\"record\",\"name\":\"" + NAMES[3] + "\",\"namespace\":\"" + NAMESPACES[3] + "\",\"doc\":\"" + DOCS[3] + "\",\"fields\":"
                    + "            ["
                    + "            {\"name\":\"" + FIELD_NAMES[12] + "\",\"type\":\"long\"},"
                    + "            {\"name\":\"" + FIELD_NAMES[13] + "\",\"type\":[\"null\",\"long\"]},"
                    + "            {\"name\":\"" + FIELD_NAMES[14] + "\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"" + LOGICAL_TYPES[3] + "\"}]},"
                    + "            {\"name\":\"" + FIELD_NAMES[15] + "\",\"type\":\"boolean\"},"
                    + "            {\"name\":\"" + FIELD_NAMES[16] + "\",\"type\":[\"null\",\"float\"]},"
                    + "            {\"name\":\"" + FIELD_NAMES[17] + "\",\"type\":\"double\"},"
                    + "            {\"name\":\"" + FIELD_NAMES[18] + "\",\"type\":[\"null\",\"bytes\"]},"
                    + "            {\"name\":\"" + FIELD_NAMES[19] + "\",\"type\":\"string\"},"
                    + "            {\"name\":\"" + FIELD_NAMES[20] + "\",\"type\":\"" + FULLNAMES[1] + "\"},"
                    + "            {\"name\":\"" + FIELD_NAMES[21] + "\",\"type\":{\"type\":\"enum\",\"name\":\"" + NAMES[4] + "\",\"symbols\":[\"e1\", \"e2\", \"e3\", \"e4\"]}},"
                    + "            {\"name\":\"" + FIELD_NAMES[22] + "\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},"
                    + "            {\"name\":\"" + FIELD_NAMES[23] + "\",\"type\":{\"type\":\"map\",\"values\":\"long\"}, \"" + PROP_KEYS[3] + "\":\"" + PROP_VALUES[3] + "\"}"
                    + "            ]"
                    + "            }"
                    + "        }"
                    + "        ]"
                    + "        }"
                    + "    }"
                    + "    ]"
                    + "    }"
                    + "}"
                    + "]"
                    + "}";

    public static final Schema AVRO_SCHEMA = new Schema.Parser().parse(SCHEMA);

    public static FullSchemaInfo randomFull(String subject, int latestSchemaVersion) {
        return FullSchemaInfo.builder()
                .subject(subject)
                .schemaJson(SCHEMA)
                .version(Math.max(1, new Random().nextInt(latestSchemaVersion)))
                .schemaRegistryId(new Random().nextInt(100))
                .compatibilityLevel(AvroCompatibilityLevel.BACKWARD)
                .mode(Mode.READWRITE)
                .deleted(new Random().nextBoolean())
                .build();
    }

}
