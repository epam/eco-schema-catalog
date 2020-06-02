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
package com.epam.eco.schemacatalog.rest.view;

import org.apache.avro.Schema;
import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;

/**
 * @author Raman_Babich
 */
public class NamedSchemaFieldTypeTest {

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        SchemaFieldType origin = NamedSchemaFieldType.builder()
                .name("Test")
                .namespace("com.epam.eco.schemacatalog.domain")
                .type(Schema.Type.RECORD)
                .logicalType(null)
                .build();

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        SchemaFieldType deserialized = JsonMapper.jsonToObject(json, SchemaFieldType.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

}
