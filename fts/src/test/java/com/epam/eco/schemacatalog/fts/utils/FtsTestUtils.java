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
package com.epam.eco.schemacatalog.fts.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.avro.Schema;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.epam.eco.schemacatalog.fts.KeyValue;
import com.epam.eco.schemacatalog.fts.SchemaDocument;
import com.epam.eco.schemacatalog.fts.repo.SchemaDocumentRepository;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Yahor Urban
 */
public class FtsTestUtils {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(FtsTestUtils.class);

    public static void checkSchemasExistence(SchemaDocumentRepository repository, long count, String... namespaces) {
        await().atMost(1200, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> {
                    Page<SchemaDocument> result = repository.findByRootNamespaceInAndVersionLatest(
                            Arrays.asList(namespaces),
                            null,
                            PageRequest.of(0, 100));
                    return result.getTotalElements();
                }, equalTo(count));
    }

    public static List<Schema> extractSchemas(Schema parent) {
        List<Schema> result = new ArrayList<>();
        result.add(parent);
        parent.getFields().forEach(f -> {
            if (f.schema().getType().equals(Schema.Type.RECORD)) {
                result.addAll(extractSchemas(f.schema()));
            }
        });
        return result;
    }

    public static Set<String> extractPaths(Schema parent) {
        Set<String> result = new HashSet<>();
        List<List<String>> paths = extractPaths(parent, null);

        paths.forEach(pathList -> {
            StringBuilder builder = new StringBuilder();
            pathList.forEach(path -> builder.append(path).append("."));
            builder.deleteCharAt(builder.length() - 1);
            result.add(builder.toString());
        });
        return result;
    }

    @SuppressWarnings("unchecked")
    private static List<List<String>> extractPaths(Schema parent, LinkedList<String> temp) {
        if (temp == null) {
            temp = new LinkedList<>();
        }
        List<List<String>> result = new LinkedList<>();
        for (Schema.Field field : parent.getFields()) {
            if (field.schema().getType().equals(Schema.Type.RECORD)) {
                LinkedList<String> list = (LinkedList<String>) temp.clone();
                list.add(field.name());
                result.add(list);
                result.addAll(extractPaths(field.schema(), list));
            } else {
                List<String> list = (List<String>) temp.clone();
                list.add(field.name());
                result.add(list);
            }
        }
        return result;
    }

    public static List<String> extractNames(Schema parent) {
        List<String> names = new ArrayList<>();
        extractSchemas(parent).forEach(schema -> {
            names.add(schema.getName());
            schema.getFields().forEach(field -> names.add(field.name()));
        });
        return names;
    }

    public static List<String> extractFullNames(Schema parent) {
        List<String> fullNames = new ArrayList<>();
        extractSchemas(parent).forEach(schema -> fullNames.add(schema.getFullName()));
        return fullNames;
    }

    public static List<String> extractDocs(Schema parent) {
        List<String> docs = new ArrayList<>();
        extractSchemas(parent).forEach(schema -> docs.add(schema.getDoc()));
        return docs;
    }

    public static Set<KeyValue> extractProps(Schema parent) {
        Map<String, Object> props = new LinkedHashMap<>();
        Set<KeyValue> propertySet = new HashSet<>();
        extractSchemas(parent).forEach(schema -> {
            props.putAll(schema.getObjectProps());
            schema.getFields().forEach(field -> props.putAll(field.getObjectProps()));
        });
        props.forEach((key, value) -> propertySet.add(new KeyValue(key, String.valueOf(value))));
        return propertySet;
    }

    public static String generateRandomText(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(RandomStringUtils.randomAlphanumeric(15)).append(" ");
        }
        return builder.toString().trim();
    }
}
