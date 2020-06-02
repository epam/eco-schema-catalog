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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.StringEscapeUtils;

/**
 * @author Raman_Babich
 */
public class HtmlPartFormatter implements PartFormatter {

    public static final HtmlPartFormatter INSTANCE = new HtmlPartFormatter();

    private static final Map<Class<? extends Part>, Function<Part, String>> FORMAT_FUNCTIONS =
            new IdentityHashMap<>();
    static {
        FORMAT_FUNCTIONS.put(Tag.class, part -> {
            Tag tag = (Tag)part;
            if (TagType.LINK == tag.getType()) {
                return formatHtmlA(
                        tag.getParams().get(1).toString(),
                        tag.getParams().get(0).toString());
            } else if (TagType.FIELD_LINK == tag.getType()) {
                return formatHtmlA(
                        tag.getParams().get(5).toString() + "#" + tag.getParams().get(4),
                        tag.getParams().get(0).toString());
            } else if (TagType.SCHEMA_LINK == tag.getType()) {
                return formatHtmlA(
                        tag.getParams().get(3).toString(),
                        tag.getParams().get(0).toString());
            } else if (TagType.FOREIGN_KEY == tag.getType()) {
                return formatHtmlA(
                        tag.getParams().get(5).toString() + "#" + tag.getParams().get(4),
                        tag.getParams().get(0).toString());
            } else {
                return formatHtmlText(tag.toString());
            }
        });
        FORMAT_FUNCTIONS.put(Text.class, part -> formatHtmlText(part.toString()));
    }

    private HtmlPartFormatter() {
    }

    @Override
    public String format(Part part) {
        Validate.notNull(part, "Part is null");

        Function<Part, String> function = FORMAT_FUNCTIONS.get(part.getClass());
        if (function == null) {
            throw new RuntimeException(
                    String.format("No HTML format function found for part '%s'", part));
        }

        return function.apply(part);
    }

    private static String formatHtmlA(String href, String title) {
        href = escapeHtmlAHref(href);
        title = escapeHtml(title);
        return String.format(
                "<a href=\"%s\" target=\"_blank\" rel=\"noopener noreferrer\">%s</a>",
                href, title);
    }

    private static String formatHtmlText(String text) {
        return escapeHtml(text);
    }

    private static String escapeHtmlAHref(String href) {
        href = escapeHtml(href);
        return StringUtils.replaceIgnoreCase(href, "javascript:", "_no_javascript_");
    }

    private static String escapeHtml(String value) {
        return StringEscapeUtils.escapeHtml4(value);
    }

}
