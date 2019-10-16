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
package com.epam.eco.schemacatalog.fts;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SubjectAndVersion;
import com.epam.eco.schemacatalog.fts.convert.SchemaDocumentConverter;
import com.epam.eco.schemacatalog.fts.repo.SchemaDocumentRepository;
import com.epam.eco.schemacatalog.store.SchemaCatalogStoreUpdateListener;

/**
 * @author Andrei_Tytsik
 */
public class SchemaDocumentIndexer implements SchemaCatalogStoreUpdateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaDocumentIndexer.class);

    private static final int MAX_BULK_SIZE = 1000;

    @Autowired
    private SchemaDocumentRepository repository;

    @Override
    public void onSchemasUpdated(Collection<FullSchemaInfo> schemas) {
        List<FullSchemaInfo> schemaList =
                schemas instanceof List ?
                (List<FullSchemaInfo>)schemas :
                new ArrayList<>(schemas);

        List<List<FullSchemaInfo>> schemaBulks = ListUtils.partition(schemaList, MAX_BULK_SIZE);
        for (List<FullSchemaInfo> schemaBulk : schemaBulks) {
            try {
                List<SchemaDocument> documents = schemaBulk.stream().
                        filter(s -> !schemaFiltered(s)).
                        map(SchemaDocumentConverter::convert).
                        collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(documents)) {
                    repository.saveAll(documents);
                }
            } catch (Exception ex) {
                LOGGER.error("Failed to index schemas", ex);
            }
        }
    }

    @Override
    public void onSchemasDeleted(Collection<SubjectAndVersion> subjectAndVersions) {
        List<SchemaDocument> schemaList = subjectAndVersions.stream().
                map(SchemaDocumentConverter::convert).
                collect(Collectors.toList());

        try {
            repository.deleteAll(schemaList);
        } catch (Exception ex) {
            LOGGER.error("Failed to delete schemas", ex);
        }
    }

    private static boolean schemaFiltered(FullSchemaInfo schemaInfo) {
        for (SchemaFilter filter : SchemaFilter.values()) {
            FilterResult result = filter.test(schemaInfo);
            if (!result.pass) {
                LOGGER.warn(
                        "Skipping schema subject={} version={}, reason={}",
                        StringUtils.abbreviate(schemaInfo.getSubject(), 100),
                        schemaInfo.getVersion(),
                        result.message);
                return true;
            }
        }
        return false;
    }

    private static enum SchemaFilter {

        ID_TOO_LONG {

            private static final int MAX_SIZE_BYTES = 512;

            @Override
            public FilterResult test(FullSchemaInfo schemaInfo) {
                int sizeInBytes = schemaInfo.getEcoId().getBytes(StandardCharsets.UTF_8).length;
                return FilterResult.of(
                        sizeInBytes <= MAX_SIZE_BYTES,
                        () -> String.format(
                                "Id is too long (%d>%d bytes)", sizeInBytes, MAX_SIZE_BYTES));
            }

        };

        public abstract FilterResult test(FullSchemaInfo schemaInfo);

    }

    private static class FilterResult {

        private static final FilterResult PASS = new FilterResult(true, null);

        private final boolean pass;
        private final String message;

        public FilterResult(boolean pass, String message) {
            this.pass = pass;
            this.message = message;
        }

        public static FilterResult of(boolean pass, Supplier<String> message) {
            if (pass) {
                return PASS;
            } else {
                return new FilterResult(false, message.get());
            }
        }

    }

}
