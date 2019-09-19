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
package com.epam.eco.schemacatalog.domain.metadata.format;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Andrei_Tytsik
 */
public final class DocFormatter {

    private static final PartFormatter DEFAULT_PART_FORMATTER = ToStringPartFormatter.INSTANCE;

    private final String doc;
    private final List<Part> parts;

    public DocFormatter(String doc) {
        this.doc = doc;
        this.parts =
                doc != null ?
                DocParser.parse(doc) :
                Collections.emptyList();
    }

    public String getDoc() {
        return doc;
    }
    public List<Part> getParts() {
        return parts;
    }

    public String format() {
        return format(DEFAULT_PART_FORMATTER);
    }

    public String format(PartFormatter partFormatter) {
        Validate.notNull(partFormatter, "Part Formatter is null");

        if (parts.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        parts.forEach(part -> builder.append(partFormatter.format(part)));
        return builder.toString();
    }

    public static String format(String doc) {
        return format(doc, null);
    }

    public static String format(String doc, PartFormatter partFormatter) {
        DocFormatter docFormatter = new DocFormatter(doc);
        return partFormatter != null ? docFormatter.format(partFormatter) : docFormatter.format();
    }

}
