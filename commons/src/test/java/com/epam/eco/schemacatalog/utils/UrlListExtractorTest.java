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
package com.epam.eco.schemacatalog.utils;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.confluent.kafka.schemaregistry.client.rest.utils.UrlList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrei_Tytsik
 */
class UrlListExtractorTest {

    @Test
    void testUrlsExtractedFromMultipleElementsList() {
        UrlList urlList = new UrlList(Arrays.asList("url1", "url2", "url3"));

        List<String> urls = UrlListExtractor.extract(urlList);

        assertNotNull(urls);
        assertEquals(3, urls.size());
        assertTrue(urls.contains("url1"));
        assertTrue(urls.contains("url2"));
        assertTrue(urls.contains("url3"));
    }

    @Test
    void testUrlsExtractedFromOneElementList() {
        UrlList urlList = new UrlList("url");

        List<String> urls = UrlListExtractor.extract(urlList);

        assertEquals("url", urlList.current());

        assertNotNull(urls);
        assertEquals(1, urls.size());
        assertTrue(urls.contains("url"));
    }

}
