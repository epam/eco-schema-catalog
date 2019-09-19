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
package com.epam.eco.schemacatalog.domain.schema;

import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Andrei_Tytsik
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IdentitySchemaInfo.class, name = "ID"),
        @JsonSubTypes.Type(value = BasicSchemaInfo.class, name = "BASIC"),
        @JsonSubTypes.Type(value = LiteSchemaInfo.class, name = "LITE"),
        @JsonSubTypes.Type(value = FullSchemaInfo.class, name = "FULL")
})
public interface SchemaInfo {
    String getSubject();
    int getVersion();
    int getSchemaRegistryId();
    String getEcoId();

    @SuppressWarnings("unchecked")
    default <T extends SchemaInfo, R extends SchemaInfo> R transform(Function<T, R> function) {
        return function.apply((T) this);
    }
}
