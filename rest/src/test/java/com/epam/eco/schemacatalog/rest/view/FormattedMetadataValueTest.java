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

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.format.DocFormatter;
import com.epam.eco.schemacatalog.domain.metadata.format.HtmlPartFormatter;
import com.epam.eco.schemacatalog.domain.metadata.format.ToStringPartFormatter;

/**
 * @author Raman_Babich
 */
public class FormattedMetadataValueTest {

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        Date date = new Date();
        MetadataValue value = MetadataValue.builder()
                .doc("doc")
                .attribute("a", "a")
                .updatedAt(date)
                .updatedBy("updateBy")
                .build();
        FormattedMetadataValue origin = FormattedMetadataValue.from(value, ToStringPartFormatter.INSTANCE);

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        FormattedMetadataValue deserialized = JsonMapper.jsonToObject(json, FormattedMetadataValue.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

    @Test
    public void testMetadataToFormattedMetadataConversion() {
        Date date = new Date();
        MetadataValue value = MetadataValue.builder()
                .doc("text @{link google|https://google.com} text")
                .attribute("a", "a")
                .updatedAt(date)
                .updatedBy("updateBy")
                .build();

        FormattedMetadataValue formattedValue = FormattedMetadataValue.from(value, HtmlPartFormatter.INSTANCE);

        Assert.assertEquals(value.getDoc(), formattedValue.getDoc());
        Assert.assertEquals(value.getUpdatedAt(), formattedValue.getUpdatedAt());
        Assert.assertEquals(value.getUpdatedBy(), formattedValue.getUpdatedBy());
        Assert.assertEquals(value.getAttributes(), formattedValue.getAttributes());
        String format = new DocFormatter("text @{link google|https://google.com} text").format(HtmlPartFormatter.INSTANCE);
        Assert.assertEquals(format, formattedValue.getFormattedDoc());
    }
}
