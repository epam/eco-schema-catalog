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
package com.epam.eco.schemacatalog.rest.controller;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.eco.schemacatalog.domain.metadata.format.TagType;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.rest.convert.TagTypeConverter;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

/**
 * @author Raman_Babich
 */
@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/tag-types")
    public Object getMetadataDocTagType(
            @RequestParam(value = "detailed", required = false, defaultValue = "false") Boolean detailed) {
        if (detailed) {
            return Arrays.stream(TagType.values())
                    .map(TagTypeConverter::toTagDescription)
                    .collect(Collectors.toList());
        }
        return TagType.values();
    }

    @GetMapping("/compatibility-levels")
    public CompatibilityLevel[] getCompatibilityLevel() {
        return CompatibilityLevel.values();
    }

    @GetMapping("/schema-modes")
    public Mode[] getSchemaModes() {
        return Mode.values();
    }

}
