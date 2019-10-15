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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.eco.commons.diff.Diff;
import com.epam.eco.schemacatalog.domain.metadata.format.DocFormatter;
import com.epam.eco.schemacatalog.domain.metadata.format.DocParser;
import com.epam.eco.schemacatalog.domain.metadata.format.HtmlPartFormatter;
import com.epam.eco.schemacatalog.domain.metadata.format.Part;
import com.epam.eco.schemacatalog.domain.rest.response.MessageResponse;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.LiteSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SchemaRegisterParams;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;
import com.epam.eco.schemacatalog.fts.JsonSearchQuery;
import com.epam.eco.schemacatalog.fts.SearchParams;
import com.epam.eco.schemacatalog.fts.SearchResult;
import com.epam.eco.schemacatalog.fts.repo.SchemaDocumentRepository;
import com.epam.eco.schemacatalog.rest.convert.SchemaDocumentConverter;
import com.epam.eco.schemacatalog.rest.utils.UrlDecoderUtils;
import com.epam.eco.schemacatalog.store.SchemaCatalogStore;
import com.epam.eco.schemacatalog.utils.SchemaDiffCalculator;

/**
 * @author Raman_Babich
 */
@RestController
@RequestMapping("/api/queries")
public class QueryController {

    @Autowired
    private SchemaCatalogStore schemaCatalogStore;

    @Autowired
    private SchemaDocumentRepository schemaDocumentRepository;

    @GetMapping("/schemas-by-params")
    public SearchResult<LiteSchemaInfo> getSchemasByParams(SearchParams params) {
        return schemaDocumentRepository.searchByParams(params)
                .map(SchemaDocumentConverter::toLiteSchemaInfo);
    }

    @PostMapping("/schemas-by-params")
    public SearchResult<LiteSchemaInfo> postSchemasByParams(@RequestBody SearchParams params) {
        return schemaDocumentRepository.searchByParams(params)
                .map(SchemaDocumentConverter::toLiteSchemaInfo);
    }

    @GetMapping("/schemas-by-query")
    public SearchResult<LiteSchemaInfo> getSchemasByFtsQuery(
            @RequestParam("query") String query,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        String decodedQuery = UrlDecoderUtils.decodeUrlParam(query);
        return schemaDocumentRepository.searchByQuery(new JsonSearchQuery(decodedQuery, page, pageSize))
                .map(SchemaDocumentConverter::toLiteSchemaInfo);
    }

    @PostMapping("/schemas-by-query")
    public SearchResult<LiteSchemaInfo> postSchemasByFtsQuery(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            @RequestBody String query) {
        return schemaDocumentRepository.searchByQuery(new JsonSearchQuery(query, page, pageSize))
                .map(SchemaDocumentConverter::toLiteSchemaInfo);
    }

    @GetMapping("/schemas-diff")
    public ResponseEntity<?> getSchemasDiff(
            @RequestParam("subject") String subject,
            @RequestParam(value = "originalVersion", required = false) Integer originalVersion,
            @RequestParam(value = "revisionVersion", required = false) Integer revisionVersion,
            @RequestParam(value = "full", required = false, defaultValue = "false") Boolean full,
            @RequestParam(value = "ignoreFieldOrder", required = false, defaultValue = "false") Boolean ignoreFieldOrder) {
        if (originalVersion != null && revisionVersion != null && full) {
            return ResponseEntity.unprocessableEntity()
                    .body(MessageResponse.with("Parameters 'originalVersion', 'revisionVersion' or 'full'(true)" +
                            " should be given, but not both at the same time"));
        }
        if (full) {
            return createSchemaFullDiff(subject, ignoreFieldOrder);
        }
        if (originalVersion != null && revisionVersion != null) {
            return createSchemaDiff(subject, originalVersion, revisionVersion, ignoreFieldOrder);
        }
        return ResponseEntity.unprocessableEntity()
                .body(MessageResponse.with("Parameters 'versionOriginal', 'versionRevised' or 'full'(true)" +
                        " should be given"));
    }

    @PostMapping("/schemas-compatibility-test")
    public ResponseEntity<?> postSchemasCompatibilityTest(
            @RequestParam(value = "detailed", required = false, defaultValue = "false") Boolean detailed,
            @RequestBody SchemaRegisterParams params) {
        if (detailed) {
            return ResponseEntity.ok(schemaCatalogStore.testSchemaCompatibleDetailed(params));
        } else {
            return ResponseEntity.ok(schemaCatalogStore.testSchemaCompatible(params));
        }
    }

    @PostMapping(value = "/metadata-doc-parts", consumes = "text/plain")
    public List<Part> postMetadataDocParts(@RequestBody String doc) {
        return DocParser.parse(doc);
    }

    @PostMapping(value = "/html-formatted-metadata-doc", consumes = "text/plain")
    public String postHtmlFormattedMetadataDoc(@RequestBody String doc) {
        return new DocFormatter(doc).format(HtmlPartFormatter.INSTANCE);
    }

    @PostMapping("/metadata-doc-parts/$batch")
    public Map<Integer, List<Part>> postMetadataDocPartsBatch(@RequestBody Map<Integer, String> docs) {
        return docs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> DocParser.parse(entry.getValue())));
    }

    @PostMapping("/html-formatted-metadata-doc/$batch")
    public Map<Integer, String> postHtmlFormattedMetadataDocBatch(@RequestBody Map<Integer, String> docs) {
        Map<Integer, String> response = new HashMap<>();
        docs.forEach((key, value) -> response.put(key, new DocFormatter(value).format(HtmlPartFormatter.INSTANCE)));
        return response;
    }

    private ResponseEntity<List<String>> createSchemaDiff(String subject, int originalVersion, int revisionVersion, boolean ignoreFieldOrder) {
        FullSchemaInfo originalSchemaInfo = schemaCatalogStore.getSchema(subject, originalVersion);
        FullSchemaInfo revisionSchemaInfo = schemaCatalogStore.getSchema(subject, revisionVersion);
        Diff schemaDiff = SchemaDiffCalculator.calculate(originalSchemaInfo, revisionSchemaInfo, ignoreFieldOrder);
        return ResponseEntity.ok().body(schemaDiff.getDiff());
    }

    private ResponseEntity<List<List<String>>> createSchemaFullDiff(String subject, boolean ignoreFieldOrder) {
        SubjectSchemas<FullSchemaInfo> subjectSchemas = schemaCatalogStore.getSubjectSchemas(subject);
        List<Diff> schemasDiffs = new ArrayList<>(subjectSchemas.size());
        FullSchemaInfo originalSchemaInfo = null;
        for (FullSchemaInfo revisionSchemaInfo : subjectSchemas.getSchemas()) {
            Diff schemaDiff = SchemaDiffCalculator.calculate(originalSchemaInfo, revisionSchemaInfo, ignoreFieldOrder);
            schemasDiffs.add(schemaDiff);
            originalSchemaInfo = revisionSchemaInfo;
        }

        Collections.reverse(schemasDiffs);

        return ResponseEntity.ok().body(schemasDiffs.stream()
                .map(Diff::getDiff)
                .collect(Collectors.toList()));
    }

}
