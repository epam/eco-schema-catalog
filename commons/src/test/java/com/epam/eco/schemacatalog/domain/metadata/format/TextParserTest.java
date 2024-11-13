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


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
class TextParserTest {

    @Test
    void testTextIsParsed() {
        Text text = TextParser.parse("");
        assertNotNull(text);
        assertEquals("", text.getText());

        text = TextParser.parse("text");
        assertNotNull(text);
        assertEquals("text", text.getText());

        text = TextParser.parse(" text ");
        assertNotNull(text);
        assertEquals(" text ", text.getText());
    }

    @Test
    void testParserFailsOnIllegalArgument() {
        assertThrows(
                Exception.class,
                () -> TextParser.parse(null)
        );
    }

}
