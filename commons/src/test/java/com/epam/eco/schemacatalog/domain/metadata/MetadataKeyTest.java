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
package com.epam.eco.schemacatalog.domain.metadata;

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;

/**
 * @author Andrei_Tytsik
 */
public class MetadataKeyTest {

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        MetadataKey origin1 = SchemaMetadataKey.with("subject1", 42);
        MetadataKey origin2 = FieldMetadataKey.with("subject2", 42, "schemaFullName", "field3");

        String json1 = JsonMapper.toJson(origin1);
        Assert.assertNotNull(json1);

        String json2 = JsonMapper.toJson(origin2);
        Assert.assertNotNull(json2);

        MetadataKey deserialized1 = JsonMapper.jsonToObject(json1, MetadataKey.class);
        Assert.assertNotNull(deserialized1);
        Assert.assertEquals(origin1, deserialized1);

        MetadataKey deserialized2 = JsonMapper.jsonToObject(json2, MetadataKey.class);
        Assert.assertNotNull(deserialized2);
        Assert.assertEquals(origin2, deserialized2);
    }

}
