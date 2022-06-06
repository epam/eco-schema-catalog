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
package com.epam.eco.schemacatalog.fts;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestPropertySource;

import com.epam.eco.commons.avro.modification.RenameSchema;
import com.epam.eco.commons.avro.modification.SchemaModifications;
import com.epam.eco.commons.avro.modification.SetSchemaProperties;
import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.fts.datagen.FtsTestCaseGenerator;
import com.epam.eco.schemacatalog.fts.datagen.FtsTestFactory;
import com.epam.eco.schemacatalog.fts.entity.FtsTestCase;
import com.epam.eco.schemacatalog.fts.repo.SchemaDocumentRepository;
import com.epam.eco.schemacatalog.fts.utils.FtsTestUtils;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static com.epam.eco.schemacatalog.fts.asserts.FtsAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Andrei_Tytsik
 */
@RunWith(JUnitParamsRunner.class)
@SpringBootTest(classes = {Config.class})
@TestPropertySource(value = "classpath:application.properties")
public class SchemaDocumentRepositoryIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaDocumentRepositoryIT.class);

    @Autowired
    private SchemaDocumentRepository repository;

    @Autowired
    private ExtendedSchemaRegistryClient client;

    public SchemaDocumentRepositoryIT() throws Exception {
        new TestContextManager(getClass()).prepareTestInstance(this);
    }

    @Test
    public void searchTest() throws Exception {
        SearchResult<SchemaDocument> ftsResult = repository.searchByQuery(
                new QueryStringQuery("*", PageRequest.of(0, 1000)));

        List<Integer> list = new ArrayList<>();
        ftsResult.getContent().forEach((doc)-> list.add(doc.getSchemaRegistryId()));
        Collections.sort(list);
        list.forEach(System.out::println);

        ftsResult = repository.searchByQuery(
                new QueryStringQuery("Ljava.lang.String;", PageRequest.of(0, 1000)));
        ftsResult = repository.searchByQuery(
                new QueryStringQuery("pmc", PageRequest.of(0, 1000)));

        System.out.println("Count = " + repository.count());

        System.out.println("findByRootNamespace");
        Page<SchemaDocument> page = repository.findByRootNamespace("PMC_SYSTEM", PageRequest.of(0, 300));
        page.forEach(d -> System.out.println(d.getRootFullname() + "-" + d.getVersion()));

        System.out.println("findByRootNamespaceAndVersionLatest(1)");
        page = repository.findByRootNamespaceAndVersionLatest("PMC_SYSTEM", true, PageRequest.of(0, 300));
        page.forEach(d -> System.out.println(d.getRootFullname() + "-" + d.getVersion()));

        System.out.println("findByRootNamespaceAndVersionLatest(2)");
        page = repository.findByRootNamespaceAndVersionLatest("PMC_SYSTEM", false, PageRequest.of(0, 300));
        page.forEach(d -> System.out.println(d.getRootFullname() + "-" + d.getVersion()));

        System.out.println("findByRootNamespaceAndVersionLatest(3)");
        page = repository.findByRootNamespaceAndVersionLatest("PMC_SYSTEM", null, PageRequest.of(0, 300));
        page.forEach(d -> System.out.println(d.getRootFullname() + "-" + d.getVersion()));

        System.out.println("findByRootNamespaceInAndVersionLatest");
        page = repository.findByRootNamespaceInAndVersionLatest(Collections.singletonList("PMC_SYSTEM"), true, PageRequest.of(0, 300));
        page.forEach(d -> System.out.println(d.getRootFullname() + "-" + d.getVersion()));

        System.out.println("findByRootNamespaceInAndVersionLatestAndSubjectNotIn");
        page = repository.findByRootNamespaceInAndVersionLatestAndSubjectNotIn(
                Arrays.asList("PMC_SYSTEM", "OK1", "OK2", "OK3"),
                true,
                Arrays.asList("PMC_SYSTEM", "NOK1", "NOK2", "NOK3"),
                PageRequest.of(0, 300));
        page.forEach(d -> System.out.println(d.getRootFullname() + "-" + d.getVersion()));
    }

    @Test
    public void findOneBySchemaSubjectAndVersion() throws Exception {
        SimpleEntry<String, String> testSchema = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchema = registerSchema(testSchema.getKey(), parseSchema(testSchema.getValue()));

        FtsTestUtils.checkSchemasExistence(repository, 1L, registeredSchema.values().iterator().next().getNamespace());

        SchemaDocument actualSchemaDoc = repository.findOneBySubjectAndVersion(testSchema.getKey(), 1);

        assertThat(actualSchemaDoc).isNotNull();
        assertThat(actualSchemaDoc)
                .describedAs("Actual schema document has same fields values as registered schema")
                .isFromSchema(registeredSchema.get(actualSchemaDoc.getSchemaRegistryId()));
    }

    @Test
    public void findByRootNamespace() throws Exception {
        SimpleEntry<String, String> testSchema = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchema = registerSchema(testSchema.getKey(), parseSchema(testSchema.getValue()));

        FtsTestUtils.checkSchemasExistence(repository, 1L, registeredSchema.values().iterator().next().getNamespace());

        Page<SchemaDocument> page = repository.findByRootNamespace(registeredSchema.values().iterator().next().getNamespace(), PageRequest.of(0, 100));
        SchemaDocument actualSchemaDoc = page.iterator().next();

        assertThat(actualSchemaDoc).isNotNull();
        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(1);
        assertThat(actualSchemaDoc)
                .describedAs("Actual schema document has same fields values as registered schema")
                .isFromSchema(registeredSchema.get(actualSchemaDoc.getSchemaRegistryId()));
    }

    @Test
    public void findByRootNamespaceAndVersionLatest() throws Exception {
        SimpleEntry<String, String> testSchema = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchemas = registerSchemaWithRandomNameAndProperty(testSchema.getKey(), parseSchema(testSchema.getValue()), 3);

        FtsTestUtils.checkSchemasExistence(repository, 3L, registeredSchemas.values().iterator().next().getNamespace());

        Page<SchemaDocument> page = repository.findByRootNamespaceAndVersionLatest(registeredSchemas.values().iterator().next().getNamespace(), true, PageRequest.of(0, 100));

        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(1);
        page.forEach(actualSchemaDoc -> {
            assertThat(actualSchemaDoc.getVersionLatest())
                    .describedAs("Actual schema has latest version")
                    .isTrue();
            assertThat(actualSchemaDoc)
                    .describedAs("Actual schema document has same fields values as registered schema")
                    .isFromSchema(registeredSchemas.get(actualSchemaDoc.getSchemaRegistryId()));
        });
    }

    @Test
    public void findByRootNamespaceAndVersionNotLatest() throws Exception {
        SimpleEntry<String, String> testSchema = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchemas = registerSchemaWithRandomNameAndProperty(testSchema.getKey(), parseSchema(testSchema.getValue()), 3);
        assertThat(registeredSchemas).hasSize(3);

        FtsTestUtils.checkSchemasExistence(repository, 3L, registeredSchemas.values().iterator().next().getNamespace());

        Page<SchemaDocument> page = repository.findByRootNamespaceAndVersionLatest(registeredSchemas.values().iterator().next().getNamespace(), false, PageRequest.of(0, 100));

        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(2);
        page.forEach(actualSchemaDoc -> {
            assertThat(actualSchemaDoc.getVersionLatest())
                    .describedAs("Actual schema has not latest version")
                    .isFalse();
            assertThat(actualSchemaDoc)
                    .describedAs("Actual schema document has same fields values as registered schema")
                    .isFromSchema(registeredSchemas.get(actualSchemaDoc.getSchemaRegistryId()));
        });
    }

    @Test
    public void findByRootNamespaceAndAnyVersion() throws Exception {
        SimpleEntry<String, String> testSchema = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchemas = registerSchemaWithRandomNameAndProperty(testSchema.getKey(), parseSchema(testSchema.getValue()), 3);

        FtsTestUtils.checkSchemasExistence(repository, 3L, registeredSchemas.values().iterator().next().getNamespace());

        Page<SchemaDocument> page = repository.findByRootNamespaceAndVersionLatest(registeredSchemas.values().iterator().next().getNamespace(), null, PageRequest.of(0, 100));

        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(3);
        page.forEach(actualSchemaDoc ->
                assertThat(actualSchemaDoc)
                        .describedAs("Actual schema document has same fields values as registered schema")
                        .isFromSchema(registeredSchemas.get(actualSchemaDoc.getSchemaRegistryId()))
        );
    }

    @Test
    public void findByRootNamespaceInAndVersionLatest() throws Exception {
        SimpleEntry<String, String> testSchemas1 = FtsTestFactory.getTestSchema();
        SimpleEntry<String, String> testSchemas2 = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchemas1 = registerSchemaWithRandomNameAndProperty(testSchemas1.getKey(), parseSchema(testSchemas1.getValue()), 3);
        Map<Integer, Schema> registeredSchemas2 = registerSchemaWithRandomNameAndProperty(testSchemas2.getKey(), parseSchema(testSchemas2.getValue()), 3);

        String namespace1 = registeredSchemas1.values().iterator().next().getNamespace();
        String namespace2 = registeredSchemas2.values().iterator().next().getNamespace();

        FtsTestUtils.checkSchemasExistence(repository, 6L, namespace1, namespace2);

        Page<SchemaDocument> page = repository.findByRootNamespaceInAndVersionLatest(Collections.singletonList(namespace1), true, PageRequest.of(0, 100));

        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(1);
        SchemaDocument actualSchemaDocument = page.iterator().next();
        assertThat(actualSchemaDocument.getVersionLatest())
                .describedAs("Actual schema has latest version")
                .isTrue();
        assertThat(actualSchemaDocument)
                .describedAs("Actual schema document has same fields values as registered schema")
                .isFromSchema(registeredSchemas1.get(actualSchemaDocument.getSchemaRegistryId()));
    }

    @Test
    public void findByRootNamespaceInAndVersionNotLatest() throws Exception {
        SimpleEntry<String, String> testSchemas1 = FtsTestFactory.getTestSchema();
        SimpleEntry<String, String> testSchemas2 = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchemas1 = registerSchemaWithRandomNameAndProperty(testSchemas1.getKey(), parseSchema(testSchemas1.getValue()), 3);
        Map<Integer, Schema> registeredSchemas2 = registerSchemaWithRandomNameAndProperty(testSchemas2.getKey(), parseSchema(testSchemas2.getValue()), 3);

        String namespace1 = registeredSchemas1.values().iterator().next().getNamespace();
        String namespace2 = registeredSchemas2.values().iterator().next().getNamespace();

        FtsTestUtils.checkSchemasExistence(repository, 6L, namespace1, namespace2);

        Page<SchemaDocument> page = repository.findByRootNamespaceInAndVersionLatest(Collections.singletonList(namespace1), false, PageRequest.of(0, 100));

        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(2);
        page.forEach(actualSchemaDocument -> {
            assertThat(actualSchemaDocument.getVersionLatest())
                    .describedAs("Actual schema has not latest version")
                    .isFalse();
            assertThat(actualSchemaDocument)
                    .describedAs("Actual schema document has same fields values as registered schema")
                    .isFromSchema(registeredSchemas1.get(actualSchemaDocument.getSchemaRegistryId()));
        });
    }

    @Test
    public void findByRootNamespaceInAndAnyVersion() throws Exception {
        SimpleEntry<String, String> testSchemas1 = FtsTestFactory.getTestSchema();
        SimpleEntry<String, String> testSchemas2 = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchemas1 = registerSchemaWithRandomNameAndProperty(testSchemas1.getKey(), parseSchema(testSchemas1.getValue()), 3);
        Map<Integer, Schema> registeredSchemas2 = registerSchemaWithRandomNameAndProperty(testSchemas2.getKey(), parseSchema(testSchemas2.getValue()), 3);

        String namespace1 = registeredSchemas1.values().iterator().next().getNamespace();
        String namespace2 = registeredSchemas2.values().iterator().next().getNamespace();

        FtsTestUtils.checkSchemasExistence(repository, 6L, namespace1, namespace2);

        Page<SchemaDocument> page = repository.findByRootNamespaceInAndVersionLatest(Collections.singletonList(namespace1), null, PageRequest.of(0, 100));

        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(3);
        page.forEach(actualSchemaDocument ->
                assertThat(actualSchemaDocument)
                        .describedAs("Actual schema document has same fields values as registered schema")
                        .isFromSchema(registeredSchemas1.get(actualSchemaDocument.getSchemaRegistryId()))
        );
    }

    @Test
    public void findByRootNamespaceInAndVersionLatestAndSubjectNotIn() throws Exception {
        SimpleEntry<String, String> testSchemas1 = FtsTestFactory.getTestSchema();
        SimpleEntry<String, String> testSchemas2 = FtsTestFactory.getTestSchema();
        SimpleEntry<String, String> testSchemas3 = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchemas1 = registerSchemaWithRandomNameAndProperty(testSchemas1.getKey(), parseSchema(testSchemas1.getValue()), 2);
        Map<Integer, Schema> registeredSchemas2 = registerSchemaWithRandomNameAndProperty(testSchemas2.getKey(), parseSchema(testSchemas2.getValue()), 2);
        Map<Integer, Schema> registeredSchemas3 = registerSchemaWithRandomNameAndProperty(testSchemas3.getKey(), parseSchema(testSchemas3.getValue()), 2);

        String namespace1 = registeredSchemas1.values().iterator().next().getNamespace();
        String namespace2 = registeredSchemas2.values().iterator().next().getNamespace();
        String namespace3 = registeredSchemas3.values().iterator().next().getNamespace();

        FtsTestUtils.checkSchemasExistence(repository, 6L, namespace1, namespace2, namespace3);

        Page<SchemaDocument> page = repository.findByRootNamespaceInAndVersionLatestAndSubjectNotIn(
                Arrays.asList(namespace1, namespace2),
                true,
                Collections.singletonList(testSchemas3.getKey()),
                PageRequest.of(0, 100));

        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(2);
        registeredSchemas1.putAll(registeredSchemas2);
        page.forEach(actualSchemaDocument -> {
            assertThat(actualSchemaDocument.getVersionLatest())
                    .describedAs("Actual schema has latest version")
                    .isTrue();
            assertThat(actualSchemaDocument)
                    .describedAs("Actual schema document has same fields values as registered schema")
                    .isFromSchema(registeredSchemas1.get(actualSchemaDocument.getSchemaRegistryId()));
        });
    }

    @Test
    public void findByRootNamespaceInAndVersionNotLatestAndSubjectNotIn() throws Exception {
        SimpleEntry<String, String> testSchemas1 = FtsTestFactory.getTestSchema();
        SimpleEntry<String, String> testSchemas2 = FtsTestFactory.getTestSchema();
        SimpleEntry<String, String> testSchemas3 = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchemas1 = registerSchemaWithRandomNameAndProperty(testSchemas1.getKey(), parseSchema(testSchemas1.getValue()), 2);
        Map<Integer, Schema> registeredSchemas2 = registerSchemaWithRandomNameAndProperty(testSchemas2.getKey(), parseSchema(testSchemas2.getValue()), 2);
        Map<Integer, Schema> registeredSchemas3 = registerSchemaWithRandomNameAndProperty(testSchemas3.getKey(), parseSchema(testSchemas3.getValue()), 2);

        String namespace1 = registeredSchemas1.values().iterator().next().getNamespace();
        String namespace2 = registeredSchemas2.values().iterator().next().getNamespace();
        String namespace3 = registeredSchemas3.values().iterator().next().getNamespace();

        FtsTestUtils.checkSchemasExistence(repository, 6L, namespace1, namespace2, namespace3);

        Page<SchemaDocument> page = repository.findByRootNamespaceInAndVersionLatestAndSubjectNotIn(
                Arrays.asList(namespace1, namespace2),
                false,
                Collections.singletonList(testSchemas3.getKey()),
                PageRequest.of(0, 100));

        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(2);
        registeredSchemas1.putAll(registeredSchemas2);
        page.forEach(actualSchemaDocument -> {
            assertThat(actualSchemaDocument.getVersionLatest())
                    .describedAs("Actual schema has not latest version")
                    .isFalse();
            assertThat(actualSchemaDocument)
                    .describedAs("Actual schema document has same fields values as registered schema")
                    .isFromSchema(registeredSchemas1.get(actualSchemaDocument.getSchemaRegistryId()));
        });
    }

    @Test
    public void findByRootNamespaceInAndAnyVersionAndSubjectNotIn() throws Exception {
        SimpleEntry<String, String> testSchemas1 = FtsTestFactory.getTestSchema();
        SimpleEntry<String, String> testSchemas2 = FtsTestFactory.getTestSchema();
        SimpleEntry<String, String> testSchemas3 = FtsTestFactory.getTestSchema();

        Map<Integer, Schema> registeredSchemas1 = registerSchemaWithRandomNameAndProperty(testSchemas1.getKey(), parseSchema(testSchemas1.getValue()), 2);
        Map<Integer, Schema> registeredSchemas2 = registerSchemaWithRandomNameAndProperty(testSchemas2.getKey(), parseSchema(testSchemas2.getValue()), 2);
        Map<Integer, Schema> registeredSchemas3 = registerSchemaWithRandomNameAndProperty(testSchemas3.getKey(), parseSchema(testSchemas3.getValue()), 2);

        String namespace1 = registeredSchemas1.values().iterator().next().getNamespace();
        String namespace2 = registeredSchemas2.values().iterator().next().getNamespace();
        String namespace3 = registeredSchemas3.values().iterator().next().getNamespace();

        FtsTestUtils.checkSchemasExistence(repository, 6L, namespace1, namespace2, namespace3);

        Page<SchemaDocument> page = repository.findByRootNamespaceInAndVersionLatestAndSubjectNotIn(
                Arrays.asList(namespace1, namespace2),
                null,
                Collections.singletonList(testSchemas3.getKey()),
                PageRequest.of(0, 100));

        assertThat(page.getNumberOfElements())
                .describedAs("Number of schemas was found")
                .isEqualTo(4);
        registeredSchemas1.putAll(registeredSchemas2);
        page.getContent().forEach(actualSchemaDocument ->
                assertThat(actualSchemaDocument)
                        .describedAs("Actual schema document has same fields values as registered schema")
                        .isFromSchema(registeredSchemas1.get(actualSchemaDocument.getSchemaRegistryId()))
        );
    }

    @Test
    public void testCompatibilityTermFilter() throws Exception {
        CompatibilityLevel compatibility = client.getGlobalLevelOfCompatibility();

        SearchParams searchParams = new SearchParams();
        searchParams.setQuery("*");
        SearchResult<SchemaDocument> documents = repository.searchByParams(searchParams);

        long amount = documents.getAggregation(FtsConstants.FIELD_COMPATIBILITY)
                .get(compatibility.toString());
        searchParams.setCompatibilityTerm(Collections.singletonList(compatibility.toString()));
        searchParams.setPageSize(10000);
        documents = repository.searchByParams(searchParams);

        assertThat(documents.getTotalElements())
                .isEqualTo(amount);
        assertThat(documents.getContent())
                .extracting(SchemaDocument::getCompatibility)
                .containsOnly(compatibility.toString());
    }

    @Test
    @Parameters(method = "searchByParamsTestDp")
    public void searchByParamsTest(FtsTestCase ftsTestCase) {
        repository.save(ftsTestCase.getSchemaDocument());

        LOGGER.info("Test search params: {}", ftsTestCase.getSearchParams().toString());

        FtsTestUtils.checkSchemasExistence(repository, 1L, ftsTestCase.getSchemaDocument().getRootNamespace());

        SearchParams sp = ftsTestCase.getSearchParams();
        sp.setNamespaceAggregation(new AggregationParams(
                        FtsConstants.FIELD_ROOT_NAMESPACE,
                        FtsConstants.FIELD_ROOT_NAMESPACE,
                        10000));
        SearchResult<SchemaDocument> documents = repository.searchByParams(sp);
        assertThat(documents.getContent())
                .describedAs("Number of schemas was found")
                .hasSize(1);
        assertThat(documents.getContent().get(0))
                .describedAs("Actual schema document has same fields values as registered schema")
                .isEqualTo(ftsTestCase.getSchemaDocument());
        assertThat(documents.getContent().get(0))
                .describedAs("Actual schema document has correct aggregations")
                .hasCorrectAggregations(documents);
    }

    @SuppressWarnings("unused")
    private Iterator<FtsTestCase> searchByParamsTestDp() {
        FullSchemaInfo schemaInfo = FtsTestFactory.getTestSchemaInfo();
        List<FtsTestCase> ftsTestCases = new ArrayList<>();

        ftsTestCases.addAll(FtsTestCaseGenerator.getRegExpTestCases(schemaInfo));
        ftsTestCases.addAll(FtsTestCaseGenerator.getQueryStringTestCases(schemaInfo));
        ftsTestCases.addAll(FtsTestCaseGenerator.getTermsTestCases(schemaInfo));

        return ftsTestCases.iterator();
    }

    @Test
    @Parameters(method = "searchByJsonQueryTestDp")
    public void searchByJsonQueryTest(FtsTestCase ftsTestCase) {
        repository.save(ftsTestCase.getSchemaDocument());

        LOGGER.info("Test search json query: {}", ftsTestCase.getSearchJsonQuery());

        FtsTestUtils.checkSchemasExistence(repository, 1L, ftsTestCase.getSchemaDocument().getRootNamespace());

        SearchResult<SchemaDocument> documents = repository.searchByQuery(
                new JsonSearchQuery(ftsTestCase.getSearchJsonQuery(), 0, 0));
        assertThat(documents.getContent())
                .describedAs("Number of schemas was found")
                .hasSize(1);
        assertThat(documents.getContent().get(0))
                .describedAs("Actual schema document has same fields values as registered schema")
                .isEqualTo(ftsTestCase.getSchemaDocument());
    }

    @Test
    @Parameters(method = "searchByNotAnalyzedFieldsNegativeDp")
    public void searchByNotAnalyzedFieldsNegative(FtsTestCase ftsTestCase) {
        repository.save(ftsTestCase.getSchemaDocument());

        LOGGER.info("searchByNotAnalyzedFieldsNegative() params: {}", ftsTestCase.getSearchParams().toString());

        FtsTestUtils.checkSchemasExistence(repository, 1L, ftsTestCase.getSchemaDocument().getRootNamespace());

        SearchResult<SchemaDocument> documents = repository.searchByParams(ftsTestCase.getSearchParams());
        assertThat(documents.getContent())
                .describedAs("Number of schemas was found")
                .hasSize(0);
    }

    @Test
    @Parameters(method = "searchByAnalyzedFieldsDp")
    public void searchByAnalyzedFields(FtsTestCase ftsTestCase) {
        repository.save(ftsTestCase.getSchemaDocument());

        LOGGER.info("searchByAnalyzedFields() params: {}", ftsTestCase.getSearchParams().toString());

        FtsTestUtils.checkSchemasExistence(repository, 1L, ftsTestCase.getSchemaDocument().getRootNamespace());

        SearchResult<SchemaDocument> documents = repository.searchByParams(ftsTestCase.getSearchParams());
        assertThat(documents.getContent())
                .describedAs("Number of schemas was found")
                .hasSize(1);
        assertThat(documents.getContent().get(0))
                .describedAs("Actual schema document has same fields values as registered schema")
                .isEqualTo(ftsTestCase.getSchemaDocument());
    }

    @SuppressWarnings("unused")
    private Iterator<FtsTestCase> searchByNotAnalyzedFieldsNegativeDp() {
        FullSchemaInfo schemaInfo = FtsTestFactory.getTestSchemaInfo();

        List<FtsTestCase> ftsTestCases = new ArrayList<>(
                FtsTestCaseGenerator.getNegativeTestCasesForNotAnalyzedFields(schemaInfo));

        return ftsTestCases.iterator();
    }

    @SuppressWarnings("unused")
    private Iterator<FtsTestCase> searchByAnalyzedFieldsDp() {
        FullSchemaInfo schemaInfo = FtsTestFactory.getTestSchemaInfo();

        List<FtsTestCase> ftsTestCases = new ArrayList<>(
                FtsTestCaseGenerator.getTestCasesForAnalyzedFields(schemaInfo));

        return ftsTestCases.iterator();
    }

    @SuppressWarnings("unused")
    private Iterator<FtsTestCase> searchByJsonQueryTestDp() {
        FullSchemaInfo schemaInfo = FtsTestFactory.getTestSchemaInfo();
        List<FtsTestCase> ftsTestCases = new ArrayList<>();

        ftsTestCases.addAll(FtsTestCaseGenerator.getJsonRegExpTestCases(schemaInfo));
        ftsTestCases.addAll(FtsTestCaseGenerator.getJsonTermsTestCases(schemaInfo));
        ftsTestCases.addAll(FtsTestCaseGenerator.getJsonFuzzinessTestCases(schemaInfo));
        ftsTestCases.addAll(FtsTestCaseGenerator.getJsonProximityTestCases(schemaInfo));
        ftsTestCases.addAll(FtsTestCaseGenerator.getJsonBooleanOperatorsTestCases(schemaInfo));

        return ftsTestCases.iterator();
    }

    @SuppressWarnings("serial")
    private Map<Integer, Schema> registerSchema(String subject, Schema schema) throws Exception {
        Integer schemaId = client.register(subject, new AvroSchema(schema));
        return new HashMap<Integer, Schema>() {{
            put(schemaId, schema);
        }};
    }

    private Map<Integer, Schema> registerSchemaWithRandomNameAndProperty(String subject, Schema schema, int times) throws Exception {
        Map<Integer, Schema> result = new HashMap<>();
        for (int i = 0; i < times; i++) {
            SimpleEntry<String, String> property = FtsTestFactory.getRandomProperty();
            Schema schemaModified = SchemaModifications.of(
                    new RenameSchema(FtsTestFactory.getRandomName(), null),
                    new SetSchemaProperties(property.getKey(), property.getValue())
            ).applyTo(schema);
            Integer schemaId = client.register(subject, new AvroSchema(schemaModified));
            result.put(schemaId, schemaModified);
        }
        return result;
    }

    @SuppressWarnings("unused")
    private Map<Integer, Schema> registerSchemas(Map<String, String> schemaMap) throws Exception {
        Map<Integer, Schema> result = new HashMap<>(schemaMap.size());
        for (Map.Entry<String, String> entry : schemaMap.entrySet()) {
            result.putAll(registerSchema(entry.getKey(), parseSchema(entry.getValue())));
        }
        return result;
    }

    private Schema parseSchema(String schemaJson) {
        return new Schema.Parser().parse(schemaJson);
    }

}
