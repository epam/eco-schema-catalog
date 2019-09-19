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
import {
  GOT_SCHEMAS,
  CHANGE_QUERY,
  SAVE_SEARCH,
  GOT_QUERY_EXAMPLES,
  SET_IS_LOADING_SCHEMAS,
  EXPANDE_SCHEMA,
} from '../../consts/consts';

const initialState = {
  query: '*',
  schemas: [],
  queryExamples: {},
  page: 0,
  pageSize: 20,
  totalPages: 0,
  totalElements: 0,
  first: true,
  last: false,
  isLoading: false,
  maxResultWindow: 0,
  isExpandedSchema: false,
};

const schemasReducer = (state = initialState, action) => {
  const {
    type,
    query,
    search,
    queryExamples,
    queryResult,
    isLoading,
    isExpandedSchema,
  } = action;

  switch (type) {
    case GOT_SCHEMAS: {
      return Object.assign({}, state, { ...queryResult });
    }
    case CHANGE_QUERY: {
      return Object.assign({}, state, { query });
    }
    case SAVE_SEARCH: {
      return Object.assign({}, state, { ...search });
    }
    case GOT_QUERY_EXAMPLES: {
      return Object.assign({}, state, { queryExamples });
    }
    case SET_IS_LOADING_SCHEMAS: {
      return Object.assign({}, state, { isLoading });
    }
    case EXPANDE_SCHEMA: {
      return Object.assign({}, state, { isExpandedSchema });
    }
    default:
      return state;
  }
};

export default schemasReducer;
