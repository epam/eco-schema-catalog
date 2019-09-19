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
package com.epam.eco.schemacatalog.domain.metadata;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;

/**
 * @author Andrei_Tytsik
 */
public class MetadataUpdateParamsTest {

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        MetadataUpdateParams origin = MetadataUpdateParams.builder().
                key(SchemaMetadataKey.with("subject148", 42)).
                doc("doc doc doc doc doc doc doc doc doc").
                appendAttribute("attr1", "attr1_value").
                appendAttribute("attr2", 412).
                appendAttribute("attr3", Collections.singletonList("item123")).
                build();

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        MetadataUpdateParams deserialized = JsonMapper.jsonToObject(json, MetadataUpdateParams.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

}
