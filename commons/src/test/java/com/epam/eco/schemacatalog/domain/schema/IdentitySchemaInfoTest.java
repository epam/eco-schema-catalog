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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

/**
 * @author Andrei_Tytsik
 */
public class IdentitySchemaInfoTest {

    @Test
    public void testEcoIdCalculated() {
        IdentitySchemaInfo schemaInfo = SchemaTestData.randomIdentitySchemaInfo();

        Assertions.assertNotNull(schemaInfo);
        Assertions.assertNotNull(schemaInfo.getEcoId());
    }

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        IdentitySchemaInfo origin = SchemaTestData.randomIdentitySchemaInfo();

        String json = JsonMapper.toJson(origin);
        Assertions.assertNotNull(json);

        IdentitySchemaInfo deserialized = JsonMapper.jsonToObject(json, IdentitySchemaInfo.class);
        Assertions.assertNotNull(deserialized);
        Assertions.assertEquals(origin, deserialized);
    }

}
