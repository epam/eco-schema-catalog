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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import com.epam.eco.schemacatalog.fts.FtsConstants;
import com.epam.eco.schemacatalog.fts.JsonSearchQuery;
import com.epam.eco.schemacatalog.fts.QueryStringQuery;
import com.epam.eco.schemacatalog.fts.SchemaDocument;
import com.epam.eco.schemacatalog.fts.SearchParams;
import com.epam.eco.schemacatalog.fts.SearchResult;

/**
 * @author Andrei_Tytsik
 */
public class SchemaDocumentRepositoryImpl implements SchemaDocumentRepositoryCustom {

    @Autowired
    private ElasticsearchTemplate template;

    private int maxResultWindow;

    @PostConstruct
    private void init() {
        createIndexIfNotCreated();
        readMaxResultWindowSetting();
    }

    private void createIndexIfNotCreated() {
        template.createIndex(SchemaDocument.class);
    }

    @SuppressWarnings("rawtypes")
    private void readMaxResultWindowSetting() {
        Map settings = template.getSetting(SchemaDocument.class);
        String maxResultWindowStr =
                (String)settings.get(IndexSettings.MAX_RESULT_WINDOW_SETTING.getKey());
        maxResultWindow =
                maxResultWindowStr != null ?
                maxResultWindow = Integer.parseInt(maxResultWindowStr) :
                IndexSettings.MAX_RESULT_WINDOW_SETTING.getDefault(null);
    }

    @Override
    public int getMaxResultWindow() {
        return maxResultWindow;
    }

    @Override
    public SearchResult<SchemaDocument> searchByQuery(SearchQuery query) {
        Validate.notNull(query, "Query is null");

        return toSearchResult(template.queryForPage(query, SchemaDocument.class));
    }

    @Override
    public SearchResult<SchemaDocument> searchByQuery(JsonSearchQuery query) {
        Validate.notNull(query, "Query is null");

        WrapperQueryBuilder wrapperQueryBuilder = QueryBuilders.wrapperQuery(query.getJson());
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(wrapperQueryBuilder);
        nativeSearchQueryBuilder.withPageable(query.getPageable());

        return toSearchResult(template.queryForPage(nativeSearchQueryBuilder.build(), SchemaDocument.class));
    }

    @Override
    public SearchResult<SchemaDocument> searchByQuery(QueryStringQuery query) {
        Validate.notNull(query, "Query is null");

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.
                withQuery(QueryBuilders.queryStringQuery(query.getQueryString())).
                withPageable(query.getPageable());

        return toSearchResult(template.queryForPage(queryBuilder.build(), SchemaDocument.class));
    }

    @Override
    public SearchResult<SchemaDocument> searchByParams(SearchParams params) {
        Validate.notNull(params, "Search params object is null");

        NativeSearchQueryBuilder queryBuilder =
                initQueryBuilder(
                        params,
                        createBoostedQuery(params),
                        params.getPageable());
        queryBuilder.withFilter(createFilter(params));

        return toSearchResult(template.queryForPage(queryBuilder.build(), SchemaDocument.class));
    }

    private SearchResult<SchemaDocument> toSearchResult(AggregatedPage<SchemaDocument> page) {
        return new SearchResult<>(
                page,
                maxResultWindow,
                toGenericMap(page.getAggregations()));
    }

    private static NativeSearchQueryBuilder initQueryBuilder(
            SearchParams params,
            QueryBuilder mainQuery,
            Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.
                withQuery(mainQuery).
                withPageable(pageable).
                withSort(SortBuilders.scoreSort().order(SortOrder.DESC));

        params.getAggregations().forEach(aggregationParams -> queryBuilder.addAggregation(
                AggregationBuilders.
                    terms(aggregationParams.getTerm()).
                    field(aggregationParams.getField()).
                    order(Terms.Order.count(false)).
                    size(aggregationParams.getSize())));

        return queryBuilder;
    }

    private static QueryBuilder createBoostedQuery(SearchParams params) {
        String queryString = params.getQuery() != null ? params.getQuery() : "";

        QueryStringQueryBuilder boostedQuery = new QueryStringQueryBuilder(queryString);

        Map<String, Float> boostingMap = new HashMap<>();

        boostingMap.put(FtsConstants.FIELD_SCHEMA_REGISTRY_ID, params.getSchemaRegistryIdBoost());
        boostingMap.put(FtsConstants.FIELD_SUBJECT, params.getSubjectBoost());
        boostingMap.put(FtsConstants.FIELD_VERSION, params.getVersionBoost());
        boostingMap.put(FtsConstants.FIELD_VERSION_LATEST, params.getVersionLatestBoost());
        boostingMap.put(FtsConstants.FIELD_COMPATIBILITY, params.getCompatibilityBoost());
        boostingMap.put(FtsConstants.FIELD_MODE, params.getModeBoost());
        boostingMap.put(FtsConstants.FIELD_ROOT_NAME, params.getNameBoost());
        boostingMap.put(FtsConstants.FIELD_ROOT_NAMESPACE, params.getNamespaceBoost());
        boostingMap.put(FtsConstants.FIELD_ROOT_FULLNAME, params.getFullnameBoost());
        boostingMap.put(FtsConstants.FIELD_DELETED, params.getDeletedBoost());
        boostingMap.put(FtsConstants.FIELD_METADATA_DOC, params.getMetadataDocBoost());
        boostingMap.put(FtsConstants.FIELD_METADATA_ATTRIBUTE_KEY, params.getMetadataAttributeKeyBoost());
        boostingMap.put(FtsConstants.FIELD_METADATA_ATTRIBUTE_VALUE, params.getMetadataAttributeValueBoost());
        boostingMap.put(FtsConstants.FIELD_METADATA_UPDATED_BY, params.getMetadataUpdatedByBoost());
        boostingMap.put(FtsConstants.FIELD_PROPERTY_KEY, params.getPropertyKeysBoost());
        boostingMap.put(FtsConstants.FIELD_PROPERTY_VALUE, params.getPropertyValuesBoost());

        boostingMap.forEach((field, boost) -> {
            if (boost != null) {
                boostedQuery.field(field, boost);
            }
        });

        return boostedQuery;
    }

    private static QueryBuilder createFilter(SearchParams params) {
        return createTermFilter(params).must(createRegExpFilter(params));
    }

    private static BoolQueryBuilder createTermFilter(SearchParams params) {
        BoolQueryBuilder filter = QueryBuilders.boolQuery();

        Map<String, List<?>> fieldMap = new HashMap<>();

        fieldMap.put(FtsConstants.FIELD_SUBJECT, params.getSubjectTerm());
        fieldMap.put(FtsConstants.FIELD_ROOT_NAME, params.getNameTerm());
        fieldMap.put(FtsConstants.FIELD_ROOT_FULLNAME, params.getFullnameTerm());
        fieldMap.put(FtsConstants.FIELD_DELETED, params.getDeletedTerm());
        fieldMap.put(FtsConstants.FIELD_VERSION, params.getVersionTerm());
        fieldMap.put(FtsConstants.FIELD_VERSION_LATEST, params.getVersionLatestTerm());
        fieldMap.put(FtsConstants.FIELD_COMPATIBILITY, params.getCompatibilityTerm());
        fieldMap.put(FtsConstants.FIELD_MODE, params.getModeTerm());
        fieldMap.put(FtsConstants.FIELD_ROOT_NAMESPACE, params.getNamespaceTerm());
        fieldMap.put(FtsConstants.FIELD_METADATA_UPDATED_BY, params.getMetadataUpdatedByTerm());

        fieldMap.forEach((field, terms) -> {
            if (terms != null && !terms.isEmpty()) {
                filter.must(QueryBuilders.termsQuery(field, terms));
            }
        });
        return filter;
    }

    private static BoolQueryBuilder createRegExpFilter(SearchParams params) {
        BoolQueryBuilder filter = QueryBuilders.boolQuery();

        Map<String, String> fieldMap = new HashMap<>();

        fieldMap.put(FtsConstants.FIELD_SUBJECT, params.getSubjectRegExp());
        fieldMap.put(FtsConstants.FIELD_ROOT_NAME, params.getNameRegExp());
        fieldMap.put(FtsConstants.FIELD_ROOT_FULLNAME, params.getFullnameRegExp());
        fieldMap.put(FtsConstants.FIELD_ROOT_NAMESPACE, params.getNamespaceRegExp());
        fieldMap.put(FtsConstants.FIELD_METADATA_UPDATED_BY, params.getMetadataUpdatedByRegExp());

        fieldMap.forEach((field, regExp) -> {
            if (regExp != null && !regExp.isEmpty()) {
                filter.must(QueryBuilders.regexpQuery(field, regExp));
            }
        });
        return filter;
    }

    private static Map<String, Map<String, Long>> toGenericMap(Aggregations aggregations) {
        Map<String, Map<String, Long>> aggregationMap = null;
        if (aggregations != null) {
            Map<String, Aggregation> aggregationNameMap = aggregations.asMap();
            aggregationMap = new HashMap<>((int) (aggregationNameMap.size() / 0.75));
            for (Map.Entry<String, Aggregation> agg : aggregationNameMap.entrySet()) {
                InternalTerms<?, ?> internalTerms = (InternalTerms<?, ?>) agg.getValue();
                Map<String, Long> buckets = new TreeMap<>();
                for (Terms.Bucket bucket : internalTerms.getBuckets()) {
                    buckets.put(bucket.getKeyAsString(), bucket.getDocCount());
                }
                aggregationMap.put(agg.getKey(), buckets);
            }
        }
        return aggregationMap;
    }

}
