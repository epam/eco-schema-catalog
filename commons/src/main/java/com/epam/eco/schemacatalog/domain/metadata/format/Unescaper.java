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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @author Andrei_Tytsik
 */
public class Unescaper {

    private static final Map<String, String> ENTITIES = new HashMap<>();
    static {
        // http://www.theasciicode.com.ar/
        ENTITIES.put("&vert;", "|");
        ENTITIES.put("&#124;", "|");
        ENTITIES.put("&lbrace;", "{");
        ENTITIES.put("&#123;", "{");
        ENTITIES.put("&rbrace;", "}");
        ENTITIES.put("&#125;", "}");
    }

    private Unescaper() {
    }

    public static String unescape(String text) {
        Validate.notNull(text, "Text is null");

        for (Map.Entry<String, String> entry : ENTITIES.entrySet()) {
            text = StringUtils.replace(text, entry.getKey(), entry.getValue());
        }
        return text;
    }

}