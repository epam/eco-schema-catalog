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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @author Andrei_Tytsik
 */
public class TagParser {

    public static final String TAG_START = "{@";
    public static final String TAG_END = "}";
    public static final String TYPE_END = " ";
    public static final String PARAM_DELIMITER = "|";

    private TagParser() {
    }

    public static Tag parse(String text) {
        Validate.notNull(text, "Text is null");

        if (!isValidNotation(text)) {
            throw new IllegalArgumentException("Not a valid tag notation");
        }

        TagType type = parseType(text);
        List<Object> params = parseParams(type, text);
        return new Tag(type, params);
    }

    private static boolean isValidNotation(String text) {
        return text.startsWith(TAG_START) && text.endsWith(TAG_END);
    }

    private static TagType parseType(String text) {
        int startIdx = TAG_START.length();
        int endIdx = StringUtils.indexOfAny(text, TYPE_END, TAG_END);

        if (endIdx <= startIdx) {
            throw new RuntimeException("Tag type is missing");
        }

        String name = text.substring(startIdx, endIdx);
        TagType type = TagType.forName(name);
        if (type == null) {
            throw new RuntimeException(String.format("Unknown tag type '%s'", name));
        }

        return type;
    }

    private static List<Object> parseParams(TagType type, String text) {
        if (type.paramCount() == 0) {
            return Collections.emptyList();
        }

        List<Object> params = Arrays.asList(new Object[type.paramCount()]);

        int startIdx = text.indexOf(TYPE_END);
        if (startIdx < 0) {
            return params;
        }

        startIdx += TYPE_END.length();

        int endIdx = text.indexOf(TAG_END, startIdx);
        if (endIdx < 0) {
            return params;
        }

        if (startIdx >= endIdx) {
            return params;
        }

        String paramsText = text.substring(startIdx, endIdx);
        String[] paramTokens = StringUtils.splitPreserveAllTokens(paramsText, PARAM_DELIMITER);
        for (int i = 0; i < params.size(); i++) {
            if (paramTokens.length <= i) {
                break;
            }

            Object param = parseParam(paramTokens[i], type.paramType(i));
            param = unescapeParam(param);
            params.set(i, param);
        }
        return params;
    }

    private static Object parseParam(String paramToken, Class<?> type) {
        if (StringUtils.isEmpty(paramToken)) {
            return null;
        }

        if (String.class == type) {
            return StringUtils.isEmpty(paramToken) ? null : paramToken;
        } else if (Byte.class == type || byte.class == type) {
            return Byte.valueOf(paramToken);
        } else if (Short.class == type || short.class == type) {
            return Short.valueOf(paramToken);
        } else if (Integer.class == type || int.class == type) {
            return Integer.valueOf(paramToken);
        } else if (Long.class == type || long.class == type) {
            return Long.valueOf(paramToken);
        } else if (Float.class == type || float.class == type) {
            return Float.valueOf(paramToken);
        } else if (Double.class == type || double.class == type) {
            return Double.valueOf(paramToken);
        } else if (Boolean.class == type || boolean.class == type) {
            return Boolean.valueOf(paramToken);
        } else {
            throw new RuntimeException(
                    String.format("Parameter type %s is not supported", type.getName()));
        }
    }

    private static Object unescapeParam(Object param) {
        if (param == null || param.getClass() != String.class) {
            return param;
        }
        return Unescaper.unescape((String)param);
    }

}
