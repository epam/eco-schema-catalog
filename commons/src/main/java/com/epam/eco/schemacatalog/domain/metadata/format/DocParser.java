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
package com.epam.eco.schemacatalog.domain.metadata.format;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrei_Tytsik
 */
public class DocParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocParser.class);

    private static final int CACHE_MAX_SIZE = 1000;
    private static final Map<String, List<Part>> CACHE =
            Collections.synchronizedMap(new LRUMap<>(CACHE_MAX_SIZE));

    private DocParser() {
    }

    public static List<Part> parse(String text) {
        Validate.notNull(text, "Text is null");

        if (text.isEmpty()) {
            return Collections.singletonList(Text.EMPTY);
        }

        List<Part> parts = CACHE.get(text);
        if (parts != null) {
            return parts;
        }

        parts = new LinkedList<>();

        int idx = 0;
        while (idx < text.length()) {
            TagAndPosition tagAndPos = parseNextTag(text, idx);
            if (tagAndPos == null) {
                parts.add(parseText(text.substring(idx)));
                break;
            }

            if (idx < tagAndPos.position.getLeft()) {
                parts.add(parseText(text.substring(idx, tagAndPos.position.getLeft())));
            }

            parts.add(tagAndPos.tag);

            idx = tagAndPos.position.getRight() + 1;
        }

        List<Part> safeList = Collections.unmodifiableList(parts);
        CACHE.put(text, safeList);

        return safeList;
    }

    private static TagAndPosition parseNextTag(String text, int fromIdx) {
        while (fromIdx < text.length()) {
            Pair<Integer, Integer> pos = findNextTagPosition(text, fromIdx);
            if (pos == null) {
                break;
            }

            Tag tag = tryParseTag(text.substring(pos.getLeft(), pos.getRight() + 1));
            if (tag != null) {
                return new TagAndPosition(tag, pos);
            } else {
                fromIdx = pos.getRight();
            }
        }
        return null;
    }

    private static Pair<Integer, Integer> findNextTagPosition(String text, int fromIdx) {
        int startIdx = text.indexOf(TagParser.TAG_START, fromIdx);
        if (startIdx < 0) {
            return null;
        }

        int endIdx = text.indexOf(TagParser.TAG_END, startIdx);
        if (endIdx < 0) {
            return null;
        }

        return Pair.of(startIdx, endIdx);
    }

    private static Tag tryParseTag(String text) {
        try {
            return TagParser.parse(text);
        } catch (Exception ex) {
            LOGGER.warn("Failed to parse '{}' as tag: {}", text, ex.getMessage());
        }
        return null;
    }

    private static Text parseText(String text) {
        return TextParser.parse(text);
    }

    private static class TagAndPosition {

        final Tag tag;
        final Pair<Integer, Integer> position;

        public TagAndPosition(Tag tag, Pair<Integer, Integer> position) {
            this.tag = tag;
            this.position = position;
        }

    }

}
