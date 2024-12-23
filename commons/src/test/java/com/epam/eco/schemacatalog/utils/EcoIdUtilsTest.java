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

import org.junit.jupiter.api.Test;

import com.epam.eco.schemacatalog.domain.schema.SubjectAndVersion;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Andrei_Tytsik
 */
class EcoIdUtilsTest {

    @Test
    void testIdFormatted1() {
        String ecoId = EcoIdUtils.formatId("subject", 10);

        assertNotNull(ecoId);
    }

    @Test
    void testIdFormatted2() {
        String ecoId = EcoIdUtils.formatId(SubjectAndVersion.with("subject", 10));

        assertNotNull(ecoId);
    }

}
