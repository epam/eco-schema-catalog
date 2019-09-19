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
package com.epam.eco.schemacatalog.rest.view;

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;

/**
 * @author Raman_Babich
 */
public class TagDescriptionTest {

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        TagDescription origin = TagDescription.builder()
                .name("NAME")
                .addParamName("param-1")
                .addParamType(String.class)
                .addParamName("param-2")
                .addParamType(Integer.class)
                .template("template")
                .description("Description")
                .build();

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        TagDescription deserialized = JsonMapper.jsonToObject(json, TagDescription.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

}
