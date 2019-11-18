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

import qs from 'qs';
import { replace } from 'connected-react-router';
import {
  GOT_SCHEMAS,
  CHANGE_QUERY,
  SAVE_SEARCH,
  GOT_QUERY_EXAMPLES,
  GOT_SCHEMAS_AGGREGATIONS,
  SET_IS_LOADING_SCHEMAS,
  APPLY_AGGREGATION,
  APPLY_AGGREGATIONS_BATCH,
  EXPANDE_SCHEMA,
  CLEAR_SCHEMAS_AGGREGATIONS,
} from '../../consts/consts';
import { getSchemas, getQueryExamples } from '../../services/httpService';
import { gotErrorMessage } from '../alertActions/alertActions';
import combineSearchParams from './utils/combineParams/combineParams';

const mergeParams = (state, newParams) => {
  const { query, page, pageSize } = state.schemasReducer;
  const {
    compatibilityTerm,
    metadataUpdatedByTerm,
    namespaceTerm,
    modeTerm,
    deletedTerm,
    versionTerm,
    versionLatestTerm,
  } = state.aggregationsSchemasReducer;

  const params = {
    query,
    page,
    pageSize,
    compatibilityTerm,
    metadataUpdatedByTerm,
    namespaceTerm,
    modeTerm,
    deletedTerm,
    versionTerm,
    versionLatestTerm,
  };
  return combineSearchParams(params, newParams);
};

export const gotSchemas = (response) => {
  const {
    content,
    firstPage,
    lastPage,
    pageNumber,
    pageSize,
    totalElements,
    totalPages,
    maxResultWindow,
  } = response;

  const queryResult = {
    schemas: content,
    page: pageNumber,
    pageSize,
    totalPages,
    totalElements,
    first: firstPage,
    last: lastPage,
    maxResultWindow,
  };
  return {
    type: GOT_SCHEMAS,
    queryResult,
  };
};

export const gotSchemasAggregations = aggregations => ({
  type: GOT_SCHEMAS_AGGREGATIONS,
  aggregations,
});

const gotQueryExamples = queryExamples => ({
  type: GOT_QUERY_EXAMPLES,
  queryExamples,
});

export const changeQuery = value => ({
  type: CHANGE_QUERY,
  query: value,
});

export const saveSearch = search => ({
  type: SAVE_SEARCH,
  search: {
    query: search.query,
    page: search.page,
    pageSize: search.pageSize,
  },
});

export const setLoadingSchemas = isLoading => ({
  type: SET_IS_LOADING_SCHEMAS,
  isLoading,
});

export const applyAggregation = (termName, termValue) => ({
  type: APPLY_AGGREGATION,
  termName,
  termValue,
});

export const applyAggregationsBatch = aggregationTerms => ({
  type: APPLY_AGGREGATIONS_BATCH,
  aggregationTerms,
});

export const clearSchemasAggregations = () => ({
  type: CLEAR_SCHEMAS_AGGREGATIONS,
});

export const syncSearchWithHistory = (newParams = {}) => (dispatch, getState) => {
  const params = mergeParams(getState(), newParams);
  const page = params.page + 1;
  dispatch(replace({
    pathname: '/',
    search: qs.stringify(Object.assign({}, params, { page }), { encode: false }),
  }));
};

export const getSchemasAsync = (newParams = {}) => (dispatch, getState) => {
  dispatch(setLoadingSchemas(true));
  dispatch(syncSearchWithHistory(newParams));
  const params = mergeParams(getState(), newParams);
  return getSchemas(params)
    .then((res) => {
      dispatch(gotSchemas(res));
      dispatch(gotSchemasAggregations(res.aggregations));
      dispatch(changeQuery(params.query));
    })
    .catch((error) => {
      const { message } = error;
      dispatch(gotErrorMessage({ message }));
      return Promise.resolve();
    })
    .then(() => {
      dispatch(setLoadingSchemas(false));
    });
};

export const getQueryExamplesAsync = () => dispatch => getQueryExamples()
  .then((queryExamples) => {
    dispatch(gotQueryExamples(queryExamples));
  })
  .catch((error) => {
    const { message } = error;
    dispatch(gotErrorMessage({ message }));
    return Promise.resolve();
  });

export const expandeSchema = isExpandedSchema => ({
  type: EXPANDE_SCHEMA,
  isExpandedSchema,
});
