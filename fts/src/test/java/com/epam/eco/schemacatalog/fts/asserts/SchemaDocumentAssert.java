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
package com.epam.eco.schemacatalog.fts.asserts;

import org.apache.avro.Schema;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.eco.schemacatalog.fts.FtsConstants;
import com.epam.eco.schemacatalog.fts.SchemaDocument;
import com.epam.eco.schemacatalog.fts.SearchResult;
import com.epam.eco.schemacatalog.fts.utils.FtsTestUtils;

/**
 * @author Yahor Urban
 */
public class SchemaDocumentAssert extends AbstractAssert<SchemaDocumentAssert, SchemaDocument> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaDocumentAssert.class);

    public SchemaDocumentAssert(SchemaDocument actual) {
        super(actual, SchemaDocumentAssert.class);
    }

    public SchemaDocumentAssert isFromSchema(Schema other) {
        isNotNull();

        if (other == null) {
            failWithMessage("Expected parameter 'other', got null");
        }

        LOGGER.info("Assert that the SchemaDocument [{}] is from the Schema [{}]", actual, other);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actual.getRootFullname()).describedAs(description(" [field=Fullname]")).isNotNull().isEqualTo(other.getFullName());
        softly.assertThat(actual.getName()).describedAs(description(" [field=Names]")).containsAll(FtsTestUtils.extractNames(other));
        softly.assertThat(actual.getFullname()).describedAs(description(" [field=Fullnames]")).containsAll(FtsTestUtils.extractFullNames(other));
        softly.assertThat(actual.getDoc()).describedAs(description(" [field=Docs]")).containsOnlyElementsOf(FtsTestUtils.extractDocs(other));
        softly.assertThat(actual.getProperty()).describedAs(description(" [field=Properties]")).containsAll(FtsTestUtils.extractProps(other));
        softly.assertThat(actual.getPath()).describedAs(description(" [field=Paths")).containsOnlyElementsOf(FtsTestUtils.extractPaths(other));
        softly.assertAll();

        return this;
    }

    public SchemaDocumentAssert hasCorrectAggregations(SearchResult<SchemaDocument> documents) {
        isNotNull();

        if (documents == null) {
            failWithMessage("Expected parameter 'schemaDocument', got null");
        }

        SchemaDocument schemaDocument = documents.getContent().get(0);
        long namespaceDocCount = documents.getAggregation(FtsConstants.FIELD_ROOT_NAMESPACE)
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals(schemaDocument.getRootNamespace()))
                .findFirst().orElseThrow(() -> new RuntimeException("Could not find aggregation with namespace: " + schemaDocument.getRootNamespace()))
                .getValue();

        long versionDocCount = documents.getAggregation(FtsConstants.FIELD_VERSION)
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals(schemaDocument.getVersion().toString()))
                .findFirst().orElseThrow(() -> new RuntimeException("Could not find aggregation with version: " + schemaDocument.getVersion()))
                .getValue();

        long versionLatestDocCount = documents.getAggregation(FtsConstants.FIELD_VERSION_LATEST)
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals("true"))
                .findFirst().orElseThrow(() -> new RuntimeException("Could not find aggregation with version latest: " + schemaDocument.getVersionLatest()))
                .getValue();

        long updatedByDocCount = documents.getAggregation(FtsConstants.FIELD_METADATA_UPDATED_BY)
                .entrySet()
                .stream()
                .filter(e -> schemaDocument.getMetadata().getUpdatedBy().contains(e.getKey()))
                .findFirst().orElseThrow(() -> new RuntimeException("Could not find aggregation with updatedBy: " + schemaDocument.getMetadata().getUpdatedBy()))
                .getValue();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(namespaceDocCount).isEqualTo(1);
        softly.assertThat(versionDocCount).isPositive();
        softly.assertThat(versionLatestDocCount).isPositive();
        softly.assertThat(updatedByDocCount).isEqualTo(1);
        softly.assertAll();

        return this;
    }

    private String description() {
        return info.descriptionText() == null ? "" : info.descriptionText();
    }

    private String description(String text) {
        return new StringBuilder().append(description()).append(text).toString();
    }

}
