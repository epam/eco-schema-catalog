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
package com.epam.eco.schemacatalog.rest.view;

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Raman_Babich
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PrimitiveSchemaFieldType.class, name = "PRIMITIVE"),
        @JsonSubTypes.Type(value = NamedSchemaFieldType.class, name = "NAMED"),
        @JsonSubTypes.Type(value = ParameterizedSchemaFieldType.class, name = "PARAMETERIZED")
})
public interface SchemaFieldType {

    String TYPE_NAME_WITH_LOGICAL_TYPE_FORMAT = "%s(%s)";

    String getFullName();
    Schema.Type getType();
    String getLogicalType();

}
