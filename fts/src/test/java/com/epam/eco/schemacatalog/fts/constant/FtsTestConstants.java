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
package com.epam.eco.schemacatalog.fts.constant;

/**
 * @author Yahor Urban
 */
public class FtsTestConstants {

    public static final String SCHEMA_TEMPLATE =
            "{\"type\":\"record\",\"name\":\"%s\",\"namespace\":\"%s\",\"doc\":\"%s\",\"fields\":"
                    + "["
                    + "{\"name\":\"%s\",\"type\":\"long\"},"
                    + "{\"name\":\"%s\",\"type\":[\"null\",\"long\"], \"%s\":\"%s\"},"
                    + "{\"name\":\"%s\",\"type\":[\"null\",{\"type\":\"int\",\"logicalType\":\"%s\"}]},"
                    + "{\"name\":\"%s\",\"type\":"
                    + "    {\"type\":\"record\",\"name\":\"%s\",\"namespace\":\"%s\",\"doc\":\"%s\",\"fields\":"
                    + "    ["
                    + "    {\"name\":\"%s\",\"type\":\"int\"},"
                    + "    {\"name\":\"%s\",\"type\":[\"null\",\"int\"]},"
                    + "    {\"name\":\"%s\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"%s\"}], \"%s\":\"%s\"},"
                    + "    {\"name\":\"%s\",\"type\":"
                    + "        {\"type\":\"record\",\"name\":\"%s\",\"namespace\":\"%s\",\"doc\":\"%s\",\"fields\":"
                    + "        ["
                    + "        {\"name\":\"%s\",\"type\":\"long\", \"%s\":\"%s\"},"
                    + "        {\"name\":\"%s\",\"type\":[\"null\",\"long\"]},"
                    + "        {\"name\":\"%s\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"%s\"}]},"
                    + "        {\"name\":\"%s\",\"type\":"
                    + "            {\"type\":\"record\",\"name\":\"%s\",\"namespace\":\"%s\",\"doc\":\"%s\",\"fields\":"
                    + "            ["
                    + "            {\"name\":\"%s\",\"type\":\"long\"},"
                    + "            {\"name\":\"%s\",\"type\":[\"null\",\"long\"]},"
                    + "            {\"name\":\"%s\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"%s\"}]},"
                    + "            {\"name\":\"%s\",\"type\":\"boolean\"},"
                    + "            {\"name\":\"%s\",\"type\":[\"null\",\"float\"]},"
                    + "            {\"name\":\"%s\",\"type\":\"double\"},"
                    + "            {\"name\":\"%s\",\"type\":[\"null\",\"bytes\"]},"
                    + "            {\"name\":\"%s\",\"type\":\"string\"},"
                    + "            {\"name\":\"%s\",\"type\":\"boolean\"},"
                    + "            {\"name\":\"%s\",\"type\":{\"type\":\"enum\",\"name\":\"enum1\",\"symbols\":[\"e1\", \"e2\", \"e3\", \"e4\"]}},"
                    + "            {\"name\":\"%s\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},"
                    + "            {\"name\":\"%s\",\"type\":{\"type\":\"map\",\"values\":\"long\"}, \"%s\":\"%s\"}"
                    + "            ]"
                    + "            }"
                    + "        }"
                    + "        ]"
                    + "        }"
                    + "    }"
                    + "    ]"
                    + "    }"
                    + "}"
                    + "]"
                    + "}";

    private FtsTestConstants() {
    }
}
