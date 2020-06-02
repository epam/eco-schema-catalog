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
package com.epam.eco.schemacatalog.store;

import java.util.List;

import com.epam.eco.schemacatalog.domain.metadata.MetadataBatchUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SchemaCompatibilityCheckResult;
import com.epam.eco.schemacatalog.domain.schema.SchemaRegisterParams;
import com.epam.eco.schemacatalog.domain.schema.SubjectCompatibilityUpdateParams;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;


/**
 * @author Andrei_Tytsik
 */
public interface SchemaCatalogStore {
    List<String> getAllSubjects();
    List<FullSchemaInfo> getAllSchemas();
    FullSchemaInfo getSchema(String subject, int version);
    FullSchemaInfo getLatestSchema(String subject);
    SubjectSchemas<FullSchemaInfo> getSubjectSchemas(String subject);
    boolean testSchemaCompatible(SchemaRegisterParams params);
    SchemaCompatibilityCheckResult testSchemaCompatibleDetailed(SchemaRegisterParams params);
    FullSchemaInfo registerSchema(SchemaRegisterParams params);
    void updateSubject(SubjectCompatibilityUpdateParams params);
    void deleteSubject(String subject);
    void deleteSchema(String subject, int version);
    void updateMetadata(MetadataUpdateParams params);
    void updateMetadata(MetadataBatchUpdateParams params);
    void deleteMetadata(MetadataKey key);
}
