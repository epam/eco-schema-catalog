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
package com.epam.eco.schemacatalog.rest.view;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.format.DocFormatter;
import com.epam.eco.schemacatalog.domain.metadata.format.HtmlPartFormatter;
import com.epam.eco.schemacatalog.domain.metadata.format.ToStringPartFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Raman_Babich
 */
class FormattedMetadataValueTest {

    @Test
    void testSerializedToJsonAndBack() {
        Date date = new Date();
        MetadataValue value = MetadataValue.builder()
                .doc("doc")
                .attribute("a", "a")
                .updatedAt(date)
                .updatedBy("updateBy")
                .build();
        FormattedMetadataValue origin = FormattedMetadataValue.from(value, ToStringPartFormatter.INSTANCE);

        String json = JsonMapper.toJson(origin);
        assertNotNull(json);

        FormattedMetadataValue deserialized = JsonMapper.jsonToObject(json, FormattedMetadataValue.class);
        assertNotNull(deserialized);
        assertEquals(origin, deserialized);
    }

    @Test
    void testMetadataToFormattedMetadataConversion() {
        Date date = new Date();
        MetadataValue value = MetadataValue.builder()
                .doc("text @{link google|https://google.com} text")
                .attribute("a", "a")
                .updatedAt(date)
                .updatedBy("updateBy")
                .build();

        FormattedMetadataValue formattedValue = FormattedMetadataValue.from(value, HtmlPartFormatter.INSTANCE);

        assertEquals(value.getDoc(), formattedValue.getDoc());
        assertEquals(value.getUpdatedAt(), formattedValue.getUpdatedAt());
        assertEquals(value.getUpdatedBy(), formattedValue.getUpdatedBy());
        assertEquals(value.getAttributes(), formattedValue.getAttributes());
        String format = new DocFormatter("text @{link google|https://google.com} text").format(HtmlPartFormatter.INSTANCE);
        assertEquals(format, formattedValue.getFormattedDoc());
    }
}
