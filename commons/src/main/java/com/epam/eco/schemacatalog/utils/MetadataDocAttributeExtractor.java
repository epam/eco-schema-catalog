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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;

import com.epam.eco.schemacatalog.domain.metadata.format.DocParser;
import com.epam.eco.schemacatalog.domain.metadata.format.Part;
import com.epam.eco.schemacatalog.domain.metadata.format.Tag;
import com.epam.eco.schemacatalog.domain.metadata.format.TagType;

/**
 * @author Raman_Babich
 */
public abstract class MetadataDocAttributeExtractor {

    private static final Pattern KEY_PATTERN = Pattern.compile("^([^\\.]+)\\.([^\\.]+)$");
    private static final String KEY_TEMPLATE = "%s.%s";

    private MetadataDocAttributeExtractor() {
    }

    public static Map<String, List<Object>> extract(String doc) {
        return extract(DocParser.parse(doc));
    }

    public static Map<String, List<Object>> extract(List<Part> parts) {
        Validate.notNull(parts, "Collection of parts is null");
        Validate.noNullElements(parts, "Collection of parts contains null elements");

        Map<String , List<Object>> attributes = new HashMap<>();
        for (Part part : parts) {
            if (!part.getClass().equals(Tag.class)) {
                continue;
            }

            Tag tag = (Tag)part;
            for (int paramIdx = 0; paramIdx < tag.getParams().size(); paramIdx++) {
                Object param = tag.getParams().get(paramIdx);
                if (param == null) {
                    continue;
                }

                String paramName = tag.getType().paramName(paramIdx);
                String attributeKey = formatAttributeKey(tag.getType(), paramName);

                List<Object> attributeValues = attributes.computeIfAbsent(attributeKey, k -> new ArrayList<>());

                attributeValues.add(param);
            }
        }
        return attributes;
    }

    public static String formatAttributeKey(TagType tagType, String paramName) {
        Validate.notNull(tagType, "Tag type is null");
        Validate.notBlank(paramName, "Param name is blank");

        if (tagType.paramIndex(paramName) == null) {
            throw new IllegalArgumentException(
                    String.format("Tag %s has no parameter '%s'", tagType, paramName));
        }

        return String.format(KEY_TEMPLATE, tagType.name(), paramName);
    }

    public static boolean isAttributeKey(String key) {
        Validate.notNull(key, "Key is null");

        Matcher matcher = KEY_PATTERN.matcher(key);
        if (!matcher.find()) {
            return false;
        }

        String typeName = matcher.group(1);
        String paramName = matcher.group(2);

        TagType tagType = TagType.forName(typeName);
        return tagType != null && tagType.paramIndex(paramName) != null;
    }

}
