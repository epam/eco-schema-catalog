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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.epam.eco.commons.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
public class SchemaRegistryServiceInfoTest {

    @Test
    public void testInstantiationFailsOnInvalidArguments1() {
        assertThrows(
                Exception.class,
                () -> SchemaRegistryServiceInfo.with((String)null)
        );
    }

    @Test
    public void testInstantiationFailsOnInvalidArguments2() {
        assertThrows(
                Exception.class,
                () -> SchemaRegistryServiceInfo.with((List<String>)null)
        );
    }

    @Test
    public void testInstantiationFailsOnInvalidArguments3() {
        assertThrows(
                Exception.class,
                () -> SchemaRegistryServiceInfo.with(Collections.singletonList(null))
        );
    }

    @Test
    public void testInfoAttributesResolved() {
        SchemaRegistryServiceInfo info = SchemaRegistryServiceInfo.with(
                Arrays.asList("url1", "url2", "url3"));

        Assertions.assertNotNull(info.getVersion());
        Assertions.assertNotNull(info.getBaseUrls());
        Assertions.assertEquals(3, info.getBaseUrls().size());

        Assertions.assertTrue(info.getBaseUrls().contains("url1"));
        Assertions.assertTrue(info.getBaseUrls().contains("url2"));
        Assertions.assertTrue(info.getBaseUrls().contains("url3"));
    }

    @Test
    public void testSerializedToJsonAndBack() {
        SchemaRegistryServiceInfo origin = SchemaRegistryServiceInfo.with(
                Arrays.asList("url1", "url2", "url3"));

        String json = JsonMapper.toJson(origin);
        Assertions.assertNotNull(json);

        SchemaRegistryServiceInfo deserialized = JsonMapper.jsonToObject(json, SchemaRegistryServiceInfo.class);
        Assertions.assertNotNull(deserialized);
        Assertions.assertEquals(origin, deserialized);
    }

}
