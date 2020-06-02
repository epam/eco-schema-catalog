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
package com.epam.eco.schemacatalog.utils;

import org.apache.commons.lang3.Validate;

import com.epam.eco.schemacatalog.domain.schema.SubjectAndVersion;

/**
 * @author Andrei_Tytsik
 */
public abstract class EcoIdUtils {

    private static final String FORMAT = "%s[%d]";

    private EcoIdUtils() {
    }

    public static String formatId(SubjectAndVersion subjectAndVersion) {
        Validate.notNull(subjectAndVersion, "SubjectAndVersion is null");

        return formatId(subjectAndVersion.getSubject(), subjectAndVersion.getVersion());
    }

    public static String formatId(String subject, int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        return String.format(FORMAT, subject, version);
    }

}
