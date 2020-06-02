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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.testdata.MetadataTestData;

/**
 * @author Andrei_Tytsik
 */
public class MetadataBatchUpdateParamsTest {

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        MetadataBatchUpdateParams origin = MetadataBatchUpdateParams.builder().
                update(MetadataTestData.randomUpdateParams()).
                update(MetadataTestData.randomUpdateParams()).
                update(MetadataTestData.randomUpdateParams()).
                delete(MetadataTestData.randomKey()).
                update(MetadataTestData.randomUpdateParams()).
                delete(MetadataTestData.randomKey()).
                delete(MetadataTestData.randomKey()).
                build();

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        MetadataBatchUpdateParams deserialized = JsonMapper.jsonToObject(json, MetadataBatchUpdateParams.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

    @Test
    public void testConsistsOfExpectedUpdates() throws Exception {
        MetadataUpdateParams params1 = MetadataTestData.randomUpdateParams();
        MetadataKey key1 = params1.getKey();

        MetadataUpdateParams params2 = MetadataTestData.randomUpdateParams();
        MetadataKey key2 = params2.getKey();

        MetadataUpdateParams params3 = MetadataTestData.randomUpdateParams();
        MetadataKey key3 = params3.getKey();

        MetadataBatchUpdateParams batchParams = MetadataBatchUpdateParams.builder().
            update(params1).
            update(params2).
            update(params3).
            delete(key3).
            update(params2).
        build();

        Map<MetadataKey, MetadataUpdateParams> operations = batchParams.getOperations();

        Assert.assertNotNull(operations);
        Assert.assertEquals(3, operations.size());
        Assert.assertEquals(params1, operations.get(key1));
        Assert.assertEquals(params2, operations.get(key2));
        Assert.assertNull(operations.get(key3));
    }

    @Test
    public void testSizesExpected() throws Exception {
        MetadataUpdateParams params1 = MetadataTestData.randomUpdateParams();
        MetadataKey key1 = params1.getKey();

        MetadataUpdateParams params2 = MetadataTestData.randomUpdateParams();
        MetadataKey key2 = params2.getKey();

        MetadataUpdateParams params3 = MetadataTestData.randomUpdateParams();
        MetadataKey key3 = params3.getKey();

        MetadataBatchUpdateParams batchParams = MetadataBatchUpdateParams.builder().
            update(params1).
            update(params2).
            update(params3).
            update(params3).
            update(params2).
            update(params1).
            delete(key1).
            delete(key3).
            delete(key2).
            update(params1).
            update(params2).
            update(params3).
        build();

        Map<MetadataKey, MetadataUpdateParams> operations = batchParams.getOperations();

        Assert.assertNotNull(operations);
        Assert.assertEquals(3, operations.size());
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments1() throws Exception {
        new MetadataBatchUpdateParams(null);
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments2() throws Exception {
        new MetadataBatchUpdateParams(new HashMap<>());
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments3() throws Exception {
        MetadataBatchUpdateParams.builder().update(null);
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments4() throws Exception {
        MetadataBatchUpdateParams.builder().delete(null);
    }

}
