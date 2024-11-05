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
package com.epam.eco.schemacatalog.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.epam.eco.commons.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrei_Tytsik
 */
class SchemaRegistryServiceInfoTest {

    @Test
    void testInstantiationFailsOnInvalidArguments1() {
        assertThrows(
                Exception.class,
                () -> SchemaRegistryServiceInfo.with((String) null)
        );
    }

    @Test
    void testInstantiationFailsOnInvalidArguments2() {
        assertThrows(
                Exception.class,
                () -> SchemaRegistryServiceInfo.with((List<String>) null)
        );
    }

    @Test
    void testInstantiationFailsOnInvalidArguments3() {
        assertThrows(
                Exception.class,
                () -> SchemaRegistryServiceInfo.with(Collections.singletonList(null))
        );
    }

    @Test
    void testInfoAttributesResolved() {
        SchemaRegistryServiceInfo info = SchemaRegistryServiceInfo.with(
                Arrays.asList("url1", "url2", "url3"));

        assertNotNull(info.getVersion());
        assertNotNull(info.getBaseUrls());
        assertEquals(3, info.getBaseUrls().size());

        assertTrue(info.getBaseUrls().contains("url1"));
        assertTrue(info.getBaseUrls().contains("url2"));
        assertTrue(info.getBaseUrls().contains("url3"));
    }

    @Test
    void testSerializedToJsonAndBack() {
        SchemaRegistryServiceInfo origin = SchemaRegistryServiceInfo.with(
                Arrays.asList("url1", "url2", "url3"));

        String json = JsonMapper.toJson(origin);
        assertNotNull(json);

        SchemaRegistryServiceInfo deserialized = JsonMapper.jsonToObject(json, SchemaRegistryServiceInfo.class);
        assertNotNull(deserialized);
        assertEquals(origin, deserialized);
    }

}
