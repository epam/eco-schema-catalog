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
package com.epam.eco.schemacatalog.domain.metadata.format;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author Andrei_Tytsik
 */
public class DocFormatterTest {

    @Test
    public void testDocIsFormatted() {
        String text = DocFormatter.format(null, ToStringPartFormatter.INSTANCE);
        Assertions.assertNull(text);

        text = new DocFormatter("text{@link}text").format(p -> "X");
        Assertions.assertNotNull(text);
        Assertions.assertEquals("XXX", text);

        text = new DocFormatter("text{@link}text").format(p -> {
            if (p instanceof Text) {
                return "-TEXT-";
            } else if (p instanceof Tag) {
                return "-TAG-";
            } else {
                return null;
            }
        });
        Assertions.assertNotNull(text);
        Assertions.assertEquals("-TEXT--TAG--TEXT-", text);

        text = new DocFormatter("text{@link}text").format(p -> {
            if (p instanceof Text) {
                return p.toString();
            } else if (p instanceof Tag) {
                return "-TAG-";
            } else {
                return null;
            }
        });
        Assertions.assertNotNull(text);
        Assertions.assertEquals("text-TAG-text", text);
    }

}
