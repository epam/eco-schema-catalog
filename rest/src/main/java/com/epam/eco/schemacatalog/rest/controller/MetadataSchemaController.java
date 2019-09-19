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
package com.epam.eco.schemacatalog.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.Metadata;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBatchUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBrowser;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataType;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.format.HtmlPartFormatter;
import com.epam.eco.schemacatalog.domain.rest.request.MetadataRequest;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.store.SchemaCatalogStore;

/**
 * @author Raman_Babich
 */
@RestController
@RequestMapping("/api/metadata/schemas")
public class MetadataSchemaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataSchemaController.class);

    @Autowired
    private SchemaCatalogStore schemaCatalogStore;

    @RequestMapping(value = {"/{subject}/{version}/fields", "/{subject}/{version}/fields/"}, method = RequestMethod.GET)
    public List<Metadata> getFieldsMetadata(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version,
            @RequestParam(value = "htmlFormatted", required = false, defaultValue = "false") Boolean htmlFormatted) {
        FullSchemaInfo schemaInfo = schemaCatalogStore.getSchema(subject, version);
        MetadataBrowser<FullSchemaInfo> metadataBrowser = schemaInfo.getMetadataBrowser();
        List<Metadata> metadataList = metadataBrowser.getAsList(MetadataType.FIELD);
        if (!htmlFormatted) {
            return metadataList;
        } else {
            return metadataList.stream()
                    .map(metadata -> metadata.format(HtmlPartFormatter.INSTANCE))
                    .collect(Collectors.toList());
        }
    }

    @RequestMapping(value = {"/{subject}/{version}", "/{subject}/{version}/"}, method = RequestMethod.GET)
    public Metadata getSchemaMetadata(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version,
            @RequestParam(value = "htmlFormatted", required = false, defaultValue = "false") Boolean htmlFormatted) {
        FullSchemaInfo schemaInfo = schemaCatalogStore.getSchema(subject, version);
        MetadataBrowser<FullSchemaInfo> metadataBrowser = schemaInfo.getMetadataBrowser();
        Metadata metadata = metadataBrowser.getSchemaMetadata().orElse(null);
        if (!htmlFormatted) {
            return metadata;
        } else {
            return metadata == null ? null : metadata.format(HtmlPartFormatter.INSTANCE);
        }
    }

    @RequestMapping(value = {
            "/{subject}/{version}/fields/{schemaFullName}/{field}",
            "/{subject}/{version}/fields/{schemaFullName}/{field}/"},
            method = RequestMethod.GET)
    public Metadata getFieldMetadata(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version,
            @PathVariable("schemaFullName") String schemaFullName,
            @PathVariable("field") String field,
            @RequestParam(value = "htmlFormatted", required = false, defaultValue = "false") Boolean htmlFormatted) {
        FullSchemaInfo schemaInfo = schemaCatalogStore.getSchema(subject, version);
        Metadata metadata = schemaInfo.getMetadataBrowser().getFieldMetadata(schemaFullName, field).orElse(null);
        if (!htmlFormatted) {
            return metadata;
        } else {
            return metadata == null ? null : metadata.format(HtmlPartFormatter.INSTANCE);
        }
    }

    @RequestMapping(value = {"/{subject}/{version}", "/{subject}/{version}/"}, method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putSchemaMetadata(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version,
            @RequestBody MetadataRequest request) {
        MetadataKey key = SchemaMetadataKey.with(subject, version);
        schemaCatalogStore.updateMetadata(MetadataUpdateParams.builder().
                key(key).
                doc(request.getDoc()).
                attributes(request.getAttributes()).
                build());
    }

    @RequestMapping(value = {
            "/{subject}/{version}/fields/{schemaFullName}/{field}",
            "/{subject}/{version}/fields/{schemaFullName}/{field}/"},
            method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putFieldMetadata(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version,
            @PathVariable("schemaFullName") String schemaFullName,
            @PathVariable("field") String field,
            @RequestBody MetadataRequest request) {
        MetadataKey key = FieldMetadataKey.with(subject, version, schemaFullName, field);
        schemaCatalogStore.updateMetadata(MetadataUpdateParams.builder().
                key(key).
                doc(request.getDoc()).
                attributes(request.getAttributes()).
                build());
    }

    @RequestMapping(value = {"/$batch", "/$batch/"}, method = RequestMethod.PUT)
    public ResponseEntity<?> putMetadataBatch(
            @RequestBody Map<String, MetadataRequest> batch) {
        MetadataBatchUpdateParams.Builder builder = MetadataBatchUpdateParams.builder();
        Map<String, String> invalidMetadataKeys = new HashMap<>();
        for (Map.Entry<String, MetadataRequest> entry : batch.entrySet()) {
            MetadataKey key;
            try {
                key = JsonMapper.jsonToObject(entry.getKey(), MetadataKey.class);
            } catch (Exception ex) {
                invalidMetadataKeys.put(entry.getKey(), String.format("Metadata key is not parsable. %s", ex.getMessage()));
                LOGGER.warn(String.format(
                        "Metadata key '%s' is not parsable.",
                        entry.getKey().replaceAll("[\r\n]"," ")), ex);
                continue;
            }
            if (entry.getValue() == null) {
                builder.delete(key);
            } else {
                MetadataUpdateParams updateParams = MetadataUpdateParams.builder()
                        .key(key)
                        .doc(entry.getValue().getDoc())
                        .attributes(entry.getValue().getAttributes())
                        .build();
                builder.update(updateParams);
            }
        }
        schemaCatalogStore.updateMetadata(builder.build());
        if (invalidMetadataKeys.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.unprocessableEntity().body(invalidMetadataKeys);
    }


    @RequestMapping(value = {"/{subject}/{version}", "/{subject}/{version}/"}, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchemaMetadata(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version) {
        MetadataKey key = SchemaMetadataKey.with(subject, version);
        schemaCatalogStore.deleteMetadata(key);
    }

    @RequestMapping(value = {
            "/{subject}/{version}/fields/{schemaFullName}/{field}",
            "/{subject}/{version}/fields/{schemaFullName}/{field}/"},
            method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFieldMetadata(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version,
            @PathVariable("schemaFullName") String schemaFullName,
            @PathVariable("field") String field) {
        MetadataKey key = FieldMetadataKey.with(subject, version, schemaFullName, field);
        schemaCatalogStore.deleteMetadata(key);
    }

}
