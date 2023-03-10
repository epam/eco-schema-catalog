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
public class FullSchemaInfoTest {

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        FullSchemaInfo origin = SchemaTestData.randomFullSchemaInfo();

        String json = JsonMapper.toJson(origin);
        Assertions.assertNotNull(json);

        FullSchemaInfo deserialized = JsonMapper.jsonToObject(json, FullSchemaInfo.class);
        Assertions.assertNotNull(deserialized);
        Assertions.assertEquals(origin, deserialized);
    }

    @Test
    public void testCasted() {
        FullSchemaInfo origin = SchemaTestData.randomFullSchemaInfo();

        BasicSchemaInfo basicSchemaInfo = BasicSchemaInfo.cast(origin);
        Assertions.assertNotNull(basicSchemaInfo);
        Assertions.assertNotEquals(origin, basicSchemaInfo);

        IdentitySchemaInfo schemaInfo = IdentitySchemaInfo.cast(origin);
        Assertions.assertNotNull(schemaInfo);
        Assertions.assertNotEquals(origin, schemaInfo);
    }

}
