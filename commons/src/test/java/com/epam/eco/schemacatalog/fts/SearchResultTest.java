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
package com.epam.eco.schemacatalog.fts;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.domain.schema.IdentitySchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SchemaInfo;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

/**
 * @author Andrei_Tytsik
 */
public class SearchResultTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        SearchResult<SchemaInfo> origin = new SearchResult<>(
                Arrays.asList(
                        SchemaTestData.randomIdentitySchemaInfo(),
                        SchemaTestData.randomIdentitySchemaInfo(),
                        SchemaTestData.randomIdentitySchemaInfo(),
                        SchemaTestData.randomIdentitySchemaInfo(),
                        SchemaTestData.randomIdentitySchemaInfo()),
                10,
                10,
                1000L,
                null);

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        SearchResult<IdentitySchemaInfo> deserialized = JsonMapper.jsonToObject(json, SearchResult.class);

        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

    @Test
    public void testPagingFieldsValid() {
        SearchResult<IdentitySchemaInfo> searchResult = new SearchResult<>(
                Collections.emptyList(),
                1,
                5,
                100L,
                null);
        Assert.assertEquals(20, searchResult.getTotalPages());
        Assert.assertTrue(searchResult.isHasPreviousPage());
        Assert.assertTrue(searchResult.isHasNextPage());
        Assert.assertFalse(searchResult.isFirstPage());
        Assert.assertFalse(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                0L,
                null);
        Assert.assertEquals(0, searchResult.getTotalPages());
        Assert.assertFalse(searchResult.isHasPreviousPage());
        Assert.assertFalse(searchResult.isHasNextPage());
        Assert.assertTrue(searchResult.isFirstPage());
        Assert.assertTrue(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                1L,
                null);
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertFalse(searchResult.isHasPreviousPage());
        Assert.assertFalse(searchResult.isHasNextPage());
        Assert.assertTrue(searchResult.isFirstPage());
        Assert.assertTrue(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                7L,
                null);
        Assert.assertEquals(2, searchResult.getTotalPages());
        Assert.assertFalse(searchResult.isHasPreviousPage());
        Assert.assertTrue(searchResult.isHasNextPage());
        Assert.assertTrue(searchResult.isFirstPage());
        Assert.assertFalse(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                11L,
                null);
        Assert.assertEquals(3, searchResult.getTotalPages());
        Assert.assertFalse(searchResult.isHasPreviousPage());
        Assert.assertTrue(searchResult.isHasNextPage());
        Assert.assertTrue(searchResult.isFirstPage());
        Assert.assertFalse(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                11L,
                5L,
                null);
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertFalse(searchResult.isHasPreviousPage());
        Assert.assertFalse(searchResult.isHasNextPage());
        Assert.assertTrue(searchResult.isFirstPage());
        Assert.assertTrue(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                1,
                5,
                100000000L,
                17L,
                null);
        Assert.assertEquals(4, searchResult.getTotalPages());
        Assert.assertTrue(searchResult.isHasPreviousPage());
        Assert.assertTrue(searchResult.isHasNextPage());
        Assert.assertFalse(searchResult.isFirstPage());
        Assert.assertFalse(searchResult.isLastPage());
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments1() {
        new SearchResult<>(
                null,
                1,
                1,
                1L,
                null);
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments2() {
        new SearchResult<>(
                Collections.emptyList(),
                -1,
                1,
                1L,
                null);
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments3() {
        new SearchResult<>(
                Collections.emptyList(),
                1,
                0,
                1L,
                null);
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments5() {
        new SearchResult<>(
                Collections.emptyList(),
                1,
                1,
                1L,
                0L,
                null);
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments6() {
        new SearchResult<>(
                Collections.emptyList(),
                1,
                2,
                1L,
                1L,
                null);
    }

}
