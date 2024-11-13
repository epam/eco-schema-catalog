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

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.epam.eco.commons.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Andrei_Tytsik
 */
class MetadataUpdateParamsTest {

    @Test
    void testSerializedToJsonAndBack() {
        MetadataUpdateParams origin = MetadataUpdateParams.builder().
                key(SchemaMetadataKey.with("subject148", 42)).
                doc("doc doc doc doc doc doc doc doc doc").
                appendAttribute("attr1", "attr1_value").
                appendAttribute("attr2", 412).
                appendAttribute("attr3", Collections.singletonList("item123")).
                build();

        String json = JsonMapper.toJson(origin);
        assertNotNull(json);

        MetadataUpdateParams deserialized = JsonMapper.jsonToObject(json, MetadataUpdateParams.class);
        assertNotNull(deserialized);
        assertEquals(origin, deserialized);
    }

}
