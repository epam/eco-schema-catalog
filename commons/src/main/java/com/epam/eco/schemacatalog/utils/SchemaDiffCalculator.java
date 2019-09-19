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
package com.epam.eco.schemacatalog.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;

import com.epam.eco.commons.avro.AvroUtils;
import com.epam.eco.commons.avro.modification.SchemaModifications;
import com.epam.eco.commons.avro.modification.SortSchemaFields;
import com.epam.eco.commons.diff.Diff;
import com.epam.eco.commons.diff.DiffCalculator;
import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Schemafull;

/**
 * @author Andrei_Tytsik
 */
public abstract class SchemaDiffCalculator {

    private SchemaDiffCalculator() {
    }

    public static Diff calculate(
            BasicSchemaInfo original,
            BasicSchemaInfo revision,
            boolean ignoreFieldOrder) {
        List<String> originalLines = getJsonLines(original, ignoreFieldOrder);
        List<String> revisedLines = getJsonLines(revision, ignoreFieldOrder);

        return DiffCalculator.calculate(getName(original), originalLines, getName(revision), revisedLines);
    }

    private static String getName(SchemaInfo schemaInfo) {
        if (schemaInfo != null) {
            return schemaInfo.getEcoId();
        } else {
            return null;
        }
    }

    private static List<String> getJsonLines(
            Schemafull schemaInfo,
            boolean ignoreFieldOrder) {
        if (schemaInfo == null) {
            return null;
        }

        return Arrays.asList(StringUtils.split(
                    JsonMapper.toPrettyJson(getSchemaJson(schemaInfo, ignoreFieldOrder)), '\n'));
    }

    private static String getSchemaJson(
            Schemafull schemaInfo,
            boolean ignoreFieldOrder) {
        Schema schema = schemaInfo.getSchemaAvro();
        if (ignoreFieldOrder) {
            schema = SchemaModifications.of(new SortSchemaFields()).applyTo(schema);
        }
        return AvroUtils.schemaToJson(schema);
    }

}
