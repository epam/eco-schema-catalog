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
package com.epam.eco.schemacatalog.domain.schema;

import org.junit.jupiter.api.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Andrei_Tytsik
 */
class BasicSchemaInfoTest {

    @Test
    void testAvroSchemaAvailable() {
        BasicSchemaInfo schemaInfo = SchemaTestData.randomBasicSchemaInfo();

        assertNotNull(schemaInfo);
        assertNotNull(schemaInfo.getSchemaAvro());
        assertNotNull(schemaInfo.getSchemaFieldInfosAsList());
        assertFalse(schemaInfo.getSchemaFieldInfosAsList().isEmpty());
    }

    @Test
    void testSerializedToJsonAndBack() {
        BasicSchemaInfo origin = SchemaTestData.randomBasicSchemaInfo();

        String json = JsonMapper.toJson(origin);
        assertNotNull(json);

        BasicSchemaInfo deserialized = JsonMapper.jsonToObject(json, BasicSchemaInfo.class);
        assertNotNull(deserialized);
        assertEquals(origin, deserialized);
    }

    @Test
    void testCasted() {
        BasicSchemaInfo origin = SchemaTestData.randomBasicSchemaInfo();

        IdentitySchemaInfo schemaInfo = IdentitySchemaInfo.cast(origin);
        assertNotNull(schemaInfo);
        assertNotEquals(origin, schemaInfo);
    }

}
