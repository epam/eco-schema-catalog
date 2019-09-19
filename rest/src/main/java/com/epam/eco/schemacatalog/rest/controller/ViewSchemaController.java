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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.domain.metadata.format.HtmlPartFormatter;
import com.epam.eco.schemacatalog.domain.metadata.format.ToStringPartFormatter;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.IdentitySchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;
import com.epam.eco.schemacatalog.rest.utils.SchemaProfileCreator;
import com.epam.eco.schemacatalog.rest.view.SchemaProfile;
import com.epam.eco.schemacatalog.store.SchemaCatalogStore;

/**
 * @author Raman_Babich
 */
@RestController
@RequestMapping("/api/views/schemas")
public class ViewSchemaController {

    @Autowired
    private SchemaCatalogStore schemaCatalogStore;

    @Autowired
    private SchemaProfileCreator schemaProfileCreator;

    @RequestMapping(value = {"/profile/{subject}/{version}", "/profile/{subject}/{version}/"}, method = RequestMethod.GET)
    public SchemaProfile getSchemaProfile(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version,
            @RequestParam(value = "htmlFormattedMetadataDoc", required = false, defaultValue = "true") Boolean htmlFormattedMetadataDoc) {
        FullSchemaInfo schemaInfo = schemaCatalogStore.getSchema(subject, version);
        if (htmlFormattedMetadataDoc) {
            return schemaProfileCreator.createSchemaProfile(schemaInfo, HtmlPartFormatter.INSTANCE);
        }
        return schemaProfileCreator.createSchemaProfile(schemaInfo, ToStringPartFormatter.INSTANCE);
    }

    @RequestMapping(value = {"/json/{subject}", "/json/{subject}/"}, method = RequestMethod.GET)
    public ResponseEntity<Map<Integer, String>> getJsonSchemas(
            @PathVariable("subject") String subject,
            @RequestParam(value = "latest", required = false, defaultValue = "false") Boolean latest,
            @RequestParam(value = "pretty", required = false, defaultValue = "false") Boolean pretty) {
        if (!latest) {
            SubjectSchemas<FullSchemaInfo> subjectSchemas = schemaCatalogStore.getSubjectSchemas(subject);
            Map<Integer, String> jsonSchemas = new TreeMap<>();
            if (pretty) {
                subjectSchemas.getSchemasAsMap()
                        .forEach((version, schema) -> jsonSchemas.put(
                                version, JsonMapper.toPrettyJson(schema.getSchemaJson())));
            } else {
                subjectSchemas.getSchemasAsMap()
                        .forEach((version, schema) -> jsonSchemas.put(version, schema.getSchemaJson()));
            }
            return ResponseEntity.ok(jsonSchemas);
        }
        FullSchemaInfo schemaInfo = schemaCatalogStore.getLatestSchema(subject);
        Map<Integer, String> schema;
        if (pretty) {
            schema = Collections.singletonMap(
                    schemaInfo.getVersion(), JsonMapper.toPrettyJson(schemaInfo.getSchemaJson()));
        } else {
            schema = Collections.singletonMap(schemaInfo.getVersion(), schemaInfo.getSchemaJson());
        }
        return ResponseEntity.ok(schema);
    }

    @RequestMapping(value = {"/json/{subject}/{version}", "/json/{subject}/{version}/"}, method = RequestMethod.GET)
    public ResponseEntity<String> getJsonSchema(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version,
            @RequestParam(value = "pretty", required = false, defaultValue = "false") Boolean pretty) {
        FullSchemaInfo schemaInfo = schemaCatalogStore.getSchema(subject, version);
        String schemaJson;
        if (pretty) {
            schemaJson = JsonMapper.toPrettyJson(schemaInfo.getSchemaJson());
        } else{
            schemaJson = schemaInfo.getSchemaJson();
        }
        return ResponseEntity.ok(schemaJson);
    }

    @RequestMapping(value = {"/full/{subject}", "/full/{subject}/"}, method = RequestMethod.GET)
    public SubjectSchemas<FullSchemaInfo> getFullSubjectSchemas(
            @PathVariable("subject") String subject,
            @RequestParam(value = "htmlFormattedMetadataDoc", required = false, defaultValue = "true") Boolean htmlFormattedMetadataDoc) {
        SubjectSchemas<FullSchemaInfo> subjectSchemas = schemaCatalogStore.getSubjectSchemas(subject);
        if (htmlFormattedMetadataDoc) {
            return subjectSchemas.transform(fullSchemaInfo -> fullSchemaInfo
                    .toSchemaWithFormattedMetadata(HtmlPartFormatter.INSTANCE));
        }
        return subjectSchemas.transform(fullSchemaInfo -> fullSchemaInfo
                .toSchemaWithFormattedMetadata(ToStringPartFormatter.INSTANCE));
    }

    @RequestMapping(value = {"/full/{subject}/{version}", "/full/{subject}/{version}/"}, method = RequestMethod.GET)
    public FullSchemaInfo getFullSchemaInfo(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version,
            @RequestParam(value = "htmlFormattedMetadataDoc", required = false, defaultValue = "true") Boolean htmlFormattedMetadataDoc) {
        FullSchemaInfo schema = schemaCatalogStore.getSchema(subject, version);
        if (htmlFormattedMetadataDoc) {
            return schema.toSchemaWithFormattedMetadata(HtmlPartFormatter.INSTANCE);
        }
        return schema.toSchemaWithFormattedMetadata(ToStringPartFormatter.INSTANCE);
    }

    @RequestMapping(value = {"/basic/{subject}", "/basic/{subject}/"}, method = RequestMethod.GET)
    public SubjectSchemas<BasicSchemaInfo> getBasicSubjectSchemas(
            @PathVariable("subject") String subject) {
        SubjectSchemas<FullSchemaInfo> subjectSchemas = schemaCatalogStore.getSubjectSchemas(subject);
        List<BasicSchemaInfo> schemas = StreamSupport.stream(subjectSchemas.spliterator(), false)
                .map(FullSchemaInfo::toBasic)
                .collect(Collectors.toList());
        return SubjectSchemas.with(schemas);
    }

    @RequestMapping(value = {"/basic/{subject}/{version}", "/basic/{subject}/{version}/"}, method = RequestMethod.GET)
    public BasicSchemaInfo getBasicSchemaInfo(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version) {
        FullSchemaInfo schemaInfo = schemaCatalogStore.getSchema(subject, version);
        return schemaInfo.toBasic();
    }

    @RequestMapping(value = {"/identity/{subject}", "/identity/{subject}/"}, method = RequestMethod.GET)
    public SubjectSchemas<IdentitySchemaInfo> getIdentitySubjectSchemas(
            @PathVariable("subject") String subject) {
        SubjectSchemas<FullSchemaInfo> subjectSchemas = schemaCatalogStore.getSubjectSchemas(subject);
        List<IdentitySchemaInfo> schemas = StreamSupport.stream(subjectSchemas.spliterator(), false)
                .map(FullSchemaInfo::toIdentity)
                .collect(Collectors.toList());
        return SubjectSchemas.with(schemas);
    }

    @RequestMapping(value = {"/identity/{subject}/{version}", "/identity/{subject}/{version}/"}, method = RequestMethod.GET)
    public IdentitySchemaInfo getIdentitySchemaInfo(
            @PathVariable("subject") String subject,
            @PathVariable("version") Integer version) {
        FullSchemaInfo schemaInfo = schemaCatalogStore.getSchema(subject, version);
        return schemaInfo.toIdentity();
    }

}
