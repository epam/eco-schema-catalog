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
package com.epam.eco.schemacatalog.domain.schema;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.commons.avro.io.parsing.GrammarError;

/**
 * @author Andrei_Tytsik
 */
public final class SchemaCompatibilityError {

    private final String path;
    private final String message;

    public SchemaCompatibilityError(
            @JsonProperty("path") String path,
            @JsonProperty("message") String message) {
        Validate.notBlank(message, "Message is blank");

        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return path + ": " + message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        SchemaCompatibilityError that = (SchemaCompatibilityError)obj;
        return
                Objects.equals(this.path, that.path) &&
                Objects.equals(this.message, that.message);
    }

    public static SchemaCompatibilityError from(GrammarError error) {
        Validate.notNull(error, "Error object is null");

        return new SchemaCompatibilityError(error.getPath(), error.getMessage());
    }

}
