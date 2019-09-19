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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrei_Tytsik
 */
public enum TagType {

    LINK(
            new String[]{"title", "link"},
            new Class<?>[]{String.class, String.class},
            "{@link title|link}",
            "Link"),

    SCHEMA_LINK(
            new String[]{"title", "subject", "version", "link"},
            new Class<?>[]{String.class, String.class, Integer.class, String.class},
            "{@schema_link title|subject|version|link}",
            "Link to some schema from Schema Catalog"),

    FIELD_LINK(
            new String[]{"title", "subject", "version", "schemaFullName", "field", "link"},
            new Class<?>[]{String.class, String.class, Integer.class, String.class, String.class, String.class},
            "{@field_link title|subject|version|schemaFullName|field|link}",
            "Link to some schema field from Schema Catalog"),

    FOREIGN_KEY(
            new String[]{"title", "subject", "version", "schemaFullName", "field", "link"},
            new Class<?>[]{String.class, String.class, Integer.class, String.class, String.class, String.class},
            "{@foreign_key title|subject|version|schemaFullName|field|link}",
            "Foreign key"),

    SQL_TABLE(
            new String[]{"database", "schema", "table"},
            new Class<?>[]{String.class, String.class, String.class},
            "{@sql_table database|schema|table}",
            "SQL table identifiers");

    private final List<String> paramNames;
    private final List<Class<?>> paramTypes;
    private final Map<String, Integer> paramIdxs;
    private final String template;
    private final String description;

    TagType(String[] paramNames, Class<?>[] paramTypes, String template, String description) {
        List<String> paramNamesList = new ArrayList<>(paramNames.length);
        List<Class<?>> paramTypesList = new ArrayList<>(paramNames.length);
        Map<String, Integer> paramIdxsMap = new HashMap<>(paramNames.length);

        for (int idx = 0; idx < paramNames.length; idx++) {
            String paramName = paramNames[idx];
            Class<?> paramType = paramTypes[idx];
            paramNamesList.add(paramName);
            paramTypesList.add(paramType);
            paramIdxsMap.put(paramName, idx);
        }

        this.paramNames = Collections.unmodifiableList(paramNamesList);
        this.paramTypes = Collections.unmodifiableList(paramTypesList);
        this.paramIdxs = Collections.unmodifiableMap(paramIdxsMap);
        this.template = template;
        this.description = description;
    }

    public List<String> paramNames() {
        return paramNames;
    }

    public String paramName(int idx) {
        return paramNames.get(idx);
    }

    public List<Class<?>> paramTypes() {
        return paramTypes;
    }

    public Class<?> paramType(int idx) {
        return paramTypes.get(idx);
    }

    public Class<?> paramType(String name) {
        return paramTypes.get(paramIndex(name));
    }

    public Integer paramIndex(String name) {
        return paramIdxs.get(name);
    }

    public int paramCount() {
        return paramNames.size();
    }

    public String template() {
        return template;
    }

    public String description() {
        return description;
    }

    public static TagType forName(String name) {
        for (TagType type : TagType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

}
