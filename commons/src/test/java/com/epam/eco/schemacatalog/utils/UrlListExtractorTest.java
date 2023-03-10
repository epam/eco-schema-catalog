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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.confluent.kafka.schemaregistry.client.rest.utils.UrlList;

/**
 * @author Andrei_Tytsik
 */
public class UrlListExtractorTest {

    @Test
    public void testUrlsExtractedFromMultipleElementsList() {
        UrlList urlList = new UrlList(Arrays.asList("url1", "url2", "url3"));

        List<String> urls = UrlListExtractor.extract(urlList);

        Assertions.assertNotNull(urls);
        Assertions.assertEquals(3, urls.size());
        Assertions.assertTrue(urls.contains("url1"));
        Assertions.assertTrue(urls.contains("url2"));
        Assertions.assertTrue(urls.contains("url3"));
    }

    @Test
    public void testUrlsExtractedFromOneElementList() {
        UrlList urlList = new UrlList("url");

        List<String> urls = UrlListExtractor.extract(urlList);

        Assertions.assertEquals("url", urlList.current());

        Assertions.assertNotNull(urls);
        Assertions.assertEquals(1, urls.size());
        Assertions.assertTrue(urls.contains("url"));
    }

}
