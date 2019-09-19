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
  SET_SCHEMA_TABLE_LOADING,
  SET_SCHEMA_HISTORY_LOADING,
  SET_SCHEMA_DETAILS_LOADING,
  SET_METADATA_LOADING,
} from '../../consts/consts';

const initialState = {
  isSchemasLoading: false,
  isSchemaTableLoading: false,
  isSchemaDetailsLoading: false,
  isSchemaHistoryLoading: false,
  isVersionLatestLoading: false,
  isMetadataRendering: false,
};

const fetchingReducer = (state = initialState, action) => {
  const { type, isFetching } = action;
  switch (type) {
    case SET_SCHEMA_TABLE_LOADING: {
      return Object.assign({}, state, {
        isSchemaTableLoading: isFetching,
      });
    }
    case SET_SCHEMA_HISTORY_LOADING: {
      return Object.assign({}, state, {
        isSchemaHistoryLoading: isFetching,
      });
    }
    case SET_SCHEMA_DETAILS_LOADING: {
      return Object.assign({}, state, {
        isSchemaDetailsLoading: isFetching,
      });
    }
    case SET_METADATA_LOADING: {
      return Object.assign({}, state, {
        isMetadataRendering: true,
      });
    }
    default:
      return initialState;
  }
};

export default fetchingReducer;
