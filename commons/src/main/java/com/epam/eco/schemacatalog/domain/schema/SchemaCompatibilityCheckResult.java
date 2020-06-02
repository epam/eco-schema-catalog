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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andrei_Tytsik
 */
public final class SchemaCompatibilityCheckResult {

    private final String subject;
    private final List<SchemaCompatibilityError> errors;
    // add more fields later (at least version of existing schema where compatibility fails)

    public SchemaCompatibilityCheckResult(String subject) { // no errors
        this(subject, null);
    }

    @JsonCreator
    public SchemaCompatibilityCheckResult(
            @JsonProperty("subject") String subject,
            @JsonProperty("errors") List<SchemaCompatibilityError> errors) {
        Validate.notBlank(subject, "Subject is blank");

        this.subject = subject;
        this.errors =
                !CollectionUtils.isEmpty(errors) ?
                Collections.unmodifiableList(new ArrayList<>(errors)) :
                Collections.emptyList();
    }

    public String getSubject() {
        return subject;
    }
    public List<SchemaCompatibilityError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !CollectionUtils.isEmpty(errors);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", errors: " + errors +
                "}";
    }

}
