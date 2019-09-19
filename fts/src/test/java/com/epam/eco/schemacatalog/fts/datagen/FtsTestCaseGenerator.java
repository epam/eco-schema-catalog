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
package com.epam.eco.schemacatalog.fts.datagen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import com.epam.eco.schemacatalog.domain.metadata.Metadata;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.fts.FtsConstants;
import com.epam.eco.schemacatalog.fts.SchemaDocument;
import com.epam.eco.schemacatalog.fts.SearchParams;
import com.epam.eco.schemacatalog.fts.convert.SchemaDocumentConverter;
import com.epam.eco.schemacatalog.fts.entity.FtsTestCase;

import static org.elasticsearch.index.query.QueryBuilders.fuzzyQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @author Yahor Urban
 */
public class FtsTestCaseGenerator {

    private static final String ATTR_LINK_TITLE = "LINK.title";
    private static final String ATTR_LINK_LINK = "LINK.link";
    private static final String METADATA_ATTR_KEY_AND_VALUE_QUERY = "metadata.attribute.key: %s AND metadata.attribute.value: %s";
    private static final String METADATA_DOC_AND_UPDATED_BY_QUERY =
            FtsConstants.FIELD_METADATA_DOC + ":\"%s\" AND " + FtsConstants.FIELD_METADATA_UPDATED_BY + ":\"%s\"";
    private static final String PROXIMITY_QUERY = "\"%s %s\"~%d";
    private static final String BOOLEAN_OPERATORS_QUERY = "(+\"%s\" -\"testWord\")";

    private static Random random = new Random();

    public static List<FtsTestCase> getRegExpTestCases(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();
        List<Metadata> metadataList = schemaInfo.getMetadataBrowser().getFieldMetadataAsList();

        String updatedBy = metadataList.get(random.nextInt(metadataList.size())).getValue().getUpdatedBy();
        SearchParams searchParams = new SearchParams();
        searchParams.setQuery("*");
        searchParams.setMetadataUpdatedByRegExp(updatedBy.replaceAll("\\w*\\s", ".*"));
        ftsTestCases.add(new FtsTestCase(schemaInfo, searchParams));

//        List linkTitle = (List) metadataList.get(random.nextInt(metadataList.size())).getValue().getAttribute(ATTR_LINK_TITLE);
//        searchParams = new SearchParams();
//        searchParams.setQuery(String.format(METADATA_ATTR_KEY_AND_VALUE_QUERY, ATTR_LINK_TITLE, ((String) linkTitle.get(0)).replaceAll("[A-Z]+", ".*")));
//        ftsTestCases.add(new FtsTestCase(schemaInfo, searchParams));

        return ftsTestCases;
    }

    @SuppressWarnings("rawtypes")
    public static List<FtsTestCase> getQueryStringTestCases(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();
        List<Metadata> metadataList = schemaInfo.getMetadataBrowser().getFieldMetadataAsList();

        List linkTitle = (List) metadataList.get(random.nextInt(metadataList.size())).getValue().getAttribute(ATTR_LINK_TITLE);
        SearchParams searchParams = new SearchParams();
        searchParams.setQuery(FtsConstants.FIELD_METADATA_ATTRIBUTE_VALUE + ": " + escapeSpecialCharacters(linkTitle.get(0).toString()));
        ftsTestCases.add(new FtsTestCase(schemaInfo, searchParams));

        List link = (List) metadataList.get(random.nextInt(metadataList.size())).getValue().getAttribute(ATTR_LINK_LINK);
        searchParams = new SearchParams();
        searchParams.setQuery(
                String.format(
                        METADATA_ATTR_KEY_AND_VALUE_QUERY,
                        escapeSpecialCharacters(ATTR_LINK_LINK),
                        escapeSpecialCharacters(link.get(0).toString())));
        ftsTestCases.add(new FtsTestCase(schemaInfo, searchParams));

        int randomInt = random.nextInt(metadataList.size());
        String doc = escapeSpecialCharacters(metadataList.get(randomInt).getValue().getDoc());
        String updatedBy = metadataList.get(randomInt).getValue().getUpdatedBy();
        searchParams = new SearchParams();
        searchParams.setQuery(String.format(METADATA_DOC_AND_UPDATED_BY_QUERY, doc, updatedBy));
        ftsTestCases.add(new FtsTestCase(schemaInfo, searchParams));

        // TODO commented out because of wrong usage of "_exists_"
        // see https://github.com/elastic/elasticsearch/issues/446
        // searchParams = new SearchParams();
        // searchParams.setQuery("_exists_:" + escapeSpecialCharacters(link.get(0).toString()));
        // ftsTestCases.add(new FtsTestCase(schemaInfo, searchParams));

        return ftsTestCases;
    }

    public static List<FtsTestCase> getTermsTestCases(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();
        SchemaDocument schemaDocument = SchemaDocumentConverter.convert(schemaInfo);

        List<String> subjectTerm = Collections.singletonList(schemaDocument.getSubject());
        SearchParams searchParams = new SearchParams();
        searchParams.setQuery("*");
        searchParams.setSubjectTerm(subjectTerm);
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        List<String> fullnameTerm = Collections.singletonList(schemaDocument.getRootFullname());
        searchParams = new SearchParams();
        searchParams.setQuery("*");
        searchParams.setFullnameTerm(fullnameTerm);
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        List<String> nameTerm = Collections.singletonList(schemaDocument.getRootName());
        searchParams = new SearchParams();
        searchParams.setQuery("*");
        searchParams.setNameTerm(nameTerm);
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        List<String> namespaceTerm = Collections.singletonList(schemaDocument.getRootNamespace());
        searchParams = new SearchParams();
        searchParams.setQuery("*");
        searchParams.setNamespaceTerm(namespaceTerm);
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        List<String> updatedByTerm = Collections.singletonList(schemaDocument.getMetadata().getUpdatedBy().iterator().next());
        searchParams = new SearchParams();
        searchParams.setQuery("*");
        searchParams.setMetadataUpdatedByTerm(updatedByTerm);
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        return ftsTestCases;
    }

    @SuppressWarnings("rawtypes")
    public static List<FtsTestCase> getJsonRegExpTestCases(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();
        SchemaDocument schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        List<Metadata> metadataList = schemaInfo.getMetadataBrowser().getFieldMetadataAsList();

        String doc = schemaDocument.getDoc().iterator().next().split("\\.\\s\\{@.+$")[0];
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(regexpQuery(FtsConstants.FIELD_DOC, doc.replaceAll("[A-Z]+", ".*").concat(".*")))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        String updatedBy = schemaDocument.getMetadata().getUpdatedBy().iterator().next();
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(regexpQuery(FtsConstants.FIELD_METADATA_UPDATED_BY, updatedBy.replaceAll("\\w*\\s", ".*")))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        List linkTitle = (List) metadataList.get(random.nextInt(metadataList.size())).getValue().getAttribute(ATTR_LINK_TITLE);
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(regexpQuery(FtsConstants.FIELD_METADATA_ATTRIBUTE_VALUE, ((String) linkTitle.get(0)).replaceAll("[A-Z]+", ".*")))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        return ftsTestCases;
    }

    public static List<FtsTestCase> getJsonTermsTestCases(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();
        SchemaDocument schemaDocument = SchemaDocumentConverter.convert(schemaInfo);

        String subjectTerm = schemaDocument.getSubject();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQuery(FtsConstants.FIELD_SUBJECT, subjectTerm))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        String fullnameTerm = schemaDocument.getRootFullname();
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQuery(FtsConstants.FIELD_ROOT_FULLNAME, fullnameTerm))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        String nameTerm = schemaDocument.getRootName();
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQuery(FtsConstants.FIELD_ROOT_NAME, nameTerm))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        String namespaceTerm = schemaDocument.getRootNamespace();
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQuery(FtsConstants.FIELD_ROOT_NAMESPACE, namespaceTerm))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        String updatedBy = schemaDocument.getMetadata().getUpdatedBy().iterator().next();
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQuery(FtsConstants.FIELD_METADATA_UPDATED_BY, updatedBy))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        return ftsTestCases;
    }

    @SuppressWarnings("rawtypes")
    public static List<FtsTestCase> getJsonFuzzinessTestCases(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();
        SchemaDocument schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        List<Metadata> metadataList = schemaInfo.getMetadataBrowser().getFieldMetadataAsList();

        String subject = schemaDocument.getSubject();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(fuzzyQuery(FtsConstants.FIELD_SUBJECT, subject.replaceFirst("\\w", "%"))
                        .fuzziness(Fuzziness.ONE))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        String name = schemaDocument.getRootName();
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(fuzzyQuery(FtsConstants.FIELD_ROOT_NAME, name.replaceFirst("\\w.", "%&"))
                        .fuzziness(Fuzziness.TWO))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        String updatedBy = schemaDocument.getMetadata().getUpdatedBy().iterator().next();
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(fuzzyQuery(FtsConstants.FIELD_METADATA_UPDATED_BY, updatedBy.replaceFirst("\\w", "&"))
                        .fuzziness(Fuzziness.AUTO))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        List link = (List) metadataList.get(random.nextInt(metadataList.size())).getValue().getAttribute(ATTR_LINK_TITLE);
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(fuzzyQuery(FtsConstants.FIELD_METADATA_ATTRIBUTE_VALUE, ((String) link.get(0)).replaceFirst("\\w", "%"))
                        .fuzziness(Fuzziness.AUTO))
                .build();
//        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        return ftsTestCases;
    }

    public static List<FtsTestCase> getJsonProximityTestCases(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();
        SchemaDocument schemaDocument = SchemaDocumentConverter.convert(schemaInfo);

        String[] doc = schemaDocument.getMetadata().getDoc().iterator().next().split("\\.\\s\\{@.+$")[0].split("\\s");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryStringQuery(String.format(PROXIMITY_QUERY, doc[0], doc[doc.length - 1], doc.length)))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        String[] updatedBy = schemaDocument.getMetadata().getUpdatedBy().iterator().next().split("\\s");
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryStringQuery(String.format(PROXIMITY_QUERY, updatedBy[0], updatedBy[1], 2)))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        return ftsTestCases;
    }

    public static List<FtsTestCase> getJsonBooleanOperatorsTestCases(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();
        SchemaDocument schemaDocument = SchemaDocumentConverter.convert(schemaInfo);

        String[] doc = schemaDocument.getMetadata().getDoc().iterator().next().split("\\.\\s\\{@.+$")[0].split("\\s");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryStringQuery(String.format(BOOLEAN_OPERATORS_QUERY, doc[0])))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        String[] updatedBy = schemaDocument.getMetadata().getUpdatedBy().iterator().next().split("\\s");
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryStringQuery(String.format(BOOLEAN_OPERATORS_QUERY, updatedBy[1])))
                .build();
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchQuery.getQuery().toString()));

        return ftsTestCases;
    }

    public static List<FtsTestCase> getNegativeTestCasesForNotAnalyzedFields(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();

        AnalyzableString analyzable = new AnalyzableString();
        SchemaDocument schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        schemaDocument.addProperty(analyzable.toString(), "nomatter");
        SearchParams searchParams = new SearchParams();
        searchParams.setQuery(FtsConstants.FIELD_PROPERTY_KEY + ":" + analyzable.randomSubToken());
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        analyzable = new AnalyzableString();
        schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        schemaDocument.addProperty("nomatter", analyzable.toString());
        searchParams = new SearchParams();
        searchParams.setQuery(FtsConstants.FIELD_PROPERTY_VALUE + ":" + analyzable.randomSubToken());
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        AnalyzableString analyzableKey = new AnalyzableString();
        AnalyzableString analyzableValue = new AnalyzableString();
        schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        schemaDocument.addProperty(analyzableKey.toString(), analyzableValue.toString());
        searchParams = new SearchParams();
        searchParams.setQuery(
                String.format(
                        "%s:%s AND %s:%s",
                        FtsConstants.FIELD_PROPERTY_KEY,
                        analyzableKey.randomSubToken(),
                        FtsConstants.FIELD_PROPERTY_VALUE,
                        analyzableValue.randomSubToken()));
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        analyzable = new AnalyzableString();
        schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        schemaDocument.getMetadata().addAttribute(analyzable.toString(), "nomatter");
        searchParams = new SearchParams();
        searchParams.setQuery(FtsConstants.FIELD_METADATA_ATTRIBUTE_KEY + ":" + analyzable.randomSubToken());
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        analyzable = new AnalyzableString();
        schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        schemaDocument.getMetadata().addAttribute("nomatter", analyzable.toString());
        searchParams = new SearchParams();
        searchParams.setQuery(FtsConstants.FIELD_METADATA_ATTRIBUTE_VALUE + ":" + analyzable.randomSubToken());
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        analyzableKey = new AnalyzableString();
        analyzableValue = new AnalyzableString();
        schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        schemaDocument.getMetadata().addAttribute(analyzableKey.toString(), analyzableValue.toString());
        searchParams = new SearchParams();
        searchParams.setQuery(
                String.format(
                        "%s:%s AND %s:%s",
                        FtsConstants.FIELD_METADATA_ATTRIBUTE_KEY,
                        analyzableKey.randomSubToken(),
                        FtsConstants.FIELD_METADATA_ATTRIBUTE_VALUE,
                        analyzableValue.randomSubToken()));
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        return ftsTestCases;
    }

    public static List<FtsTestCase> getTestCasesForAnalyzedFields(FullSchemaInfo schemaInfo) {
        List<FtsTestCase> ftsTestCases = new ArrayList<>();

        AnalyzableString analyzable = new AnalyzableString();
        SchemaDocument schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        schemaDocument.addDoc(analyzable.toString());
        SearchParams searchParams = new SearchParams();
        searchParams.setQuery(FtsConstants.FIELD_DOC + ":" + analyzable.randomSubToken());
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        analyzable = new AnalyzableString();
        schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        schemaDocument.getMetadata().addDoc(analyzable.toString());
        searchParams = new SearchParams();
        searchParams.setQuery(FtsConstants.FIELD_METADATA_DOC + ":" + analyzable.randomSubToken());
        ftsTestCases.add(new FtsTestCase(schemaInfo, schemaDocument, searchParams));

        return ftsTestCases;
    }

    private static String escapeSpecialCharacters(String str) {
        return str.
                replace(":", "\\:").
                replace(".", "\\.").
                replace("/", "\\/");
    }

    private static class AnalyzableString {

        private final String[] tokens;
        private final String toString;

        public AnalyzableString() {
            tokens = generateTokens();
            toString = toString(tokens);
        }

        public String randomSubToken() {
            return tokens[random.nextInt(tokens.length)];
        }

        @Override
        public String toString() {
            return toString;
        }

        private static String[] generateTokens() {
            String[] tokens = new String[Math.max(2, random.nextInt(10))];
            for (int i = 0; i < tokens.length; i ++) {
                tokens[i] = RandomStringUtils.randomAlphanumeric(10);
            }
            return tokens;
        }

        private static String toString(String[] tokens) {
            StringBuilder builder = new StringBuilder();
            for (String token : tokens) {
                if (builder.length() > 0) {
                    builder.append(
                            RandomStringUtils.random(3, ' ', '.', ',', ':'));
                }
                builder.append(token);
            }
            return builder.toString();
        }

    }

}
