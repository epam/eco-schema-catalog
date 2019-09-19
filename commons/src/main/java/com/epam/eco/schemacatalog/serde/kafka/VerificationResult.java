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
package com.epam.eco.schemacatalog.serde.kafka;

import org.apache.commons.lang3.Validate;

/**
 * @author Andrei_Tytsik
 */
public final class VerificationResult {

    public enum Status {
        SKIPPABLE,
        PASSED,
        NOT_PASSED
    }

    private final Status status;
    private final String comment;

    public VerificationResult(Status status) {
        this(status, null);
    }

    public VerificationResult(Status status, String comment) {
        Validate.notNull(status, "Status is null");

        this.status = status;
        this.comment = comment;
    }

    public Status getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public static VerificationResult with(Status status) {
        return new VerificationResult(status);
    }

    public static VerificationResult with(Status status, String comment) {
        return new VerificationResult(status, comment);
    }

    @Override
    public String toString() {
        return String.format("status: %s, comment: %s", status, comment);
    }

}
