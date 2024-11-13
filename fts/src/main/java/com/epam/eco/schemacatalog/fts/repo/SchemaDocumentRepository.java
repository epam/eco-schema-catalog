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
package com.epam.eco.schemacatalog.fts.repo;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.epam.eco.schemacatalog.fts.SchemaDocument;

/**
 * @author Andrei_Tytsik
 */
public interface SchemaDocumentRepository extends ElasticsearchRepository<SchemaDocument, String>, SchemaDocumentRepositoryCustom {

    SchemaDocument findOneBySubjectAndVersion(String subject, Integer version);

    Page<SchemaDocument> findByRootNamespace(String rootNamespace, Pageable pageable);

    Page<SchemaDocument> findByRootNamespaceIn(Collection<String> rootNamespaces, Pageable pageable);

    Page<SchemaDocument> findByRootNamespaceAndVersionLatest(
            String rootNamespace,
            Boolean versionLatest,
            Pageable pageable);

    Page<SchemaDocument> findByRootNamespaceInAndVersionLatest(
            Collection<String> rootNamespaces,
            Boolean versionLatest,
            Pageable pageable);

    Page<SchemaDocument> findByRootNamespaceInAndVersionLatestAndSubjectNotIn(
            Collection<String> rootNamespaces,
            Boolean versionLatest,
            Collection<String> subjects,
            Pageable pageable);

    Page<SchemaDocument> findByRootNamespaceInAndSubjectNotIn(
            Collection<String> rootNamespaces,
            Collection<String> subjects,
            Pageable pageable);

}
