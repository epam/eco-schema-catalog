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
public class UnescaperTest {

    @Test
    public void testCodesAreUnescaped() {
        String text = Unescaper.unescape(
                "&lbrace;&#123;&lbrace;::&vert;&#124;&vert;&#124;&vert;&#124;&vert;&#124;&vert;&#124;::&rbrace;&#125;&rbrace;");
        Assertions.assertEquals("{{{::||||||||||::}}}", text);
    }

}
