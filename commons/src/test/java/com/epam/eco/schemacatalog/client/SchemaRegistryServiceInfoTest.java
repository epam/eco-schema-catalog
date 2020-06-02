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

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;

/**
 * @author Andrei_Tytsik
 */
public class SchemaRegistryServiceInfoTest {

    @Test(expected=Exception.class)
    public void testInstantiationFailsOnInvalidArguments1() throws Exception {
        SchemaRegistryServiceInfo.with((String)null);
    }

    @Test(expected=Exception.class)
    public void testInstantiationFailsOnInvalidArguments2() throws Exception {
        SchemaRegistryServiceInfo.with((List<String>)null);
    }

    @Test(expected=Exception.class)
    public void testInstantiationFailsOnInvalidArguments3() throws Exception {
        SchemaRegistryServiceInfo.with(Collections.singletonList(null));
    }

    @Test
    public void testInfoAttributesResolved() throws Exception {
        SchemaRegistryServiceInfo info = SchemaRegistryServiceInfo.with(
                Arrays.asList("url1", "url2", "url3"));

        Assert.assertNotNull(info.getVersion());
        Assert.assertNotNull(info.getBaseUrls());
        Assert.assertEquals(3, info.getBaseUrls().size());

        Assert.assertTrue(info.getBaseUrls().contains("url1"));
        Assert.assertTrue(info.getBaseUrls().contains("url2"));
        Assert.assertTrue(info.getBaseUrls().contains("url3"));
    }

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        SchemaRegistryServiceInfo origin = SchemaRegistryServiceInfo.with(
                Arrays.asList("url1", "url2", "url3"));

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        SchemaRegistryServiceInfo deserialized = JsonMapper.jsonToObject(json, SchemaRegistryServiceInfo.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

}
