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
  GOT_SCHEMAS_AGGREGATIONS,
  APPLY_AGGREGATION,
  APPLY_AGGREGATIONS_BATCH,
  CLEAR_SCHEMAS_AGGREGATIONS,
} from '../../consts/consts';

import {
  NAMESPACE_TERM,
  COMPATIBILITY_TERM,
  DELETED_TERM,
  VERSION_LATEST_TERM,
  VERSION_TERM,
  MODE_TERM,
} from '../../consts/terms';

const initialState = {
  /* available Terms */
  compatibility: {},
  deleted: {},
  rootNamespace: {},
  version: {},
  versionLatest: {},
  mode: {},

  /* applied Terms */
  [COMPATIBILITY_TERM]: [],
  [DELETED_TERM]: [],
  [NAMESPACE_TERM]: [],
  [VERSION_TERM]: [],
  [VERSION_LATEST_TERM]: [],
  [MODE_TERM]: [],
};

const aggregationsSchemasReducer = (state = initialState, action) => {
  const {
    type,
    aggregations,
    aggregationTerms,
    termName,
    termValue,
  } = action;
  switch (type) {
    case GOT_SCHEMAS_AGGREGATIONS: {
      const {
        compatibility,
        rootNamespace,
        version,
        deleted,
        versionLatest,
        mode,
      } = aggregations;
      return Object.assign(
        {},
        state,
        {
          compatibility,
          rootNamespace,
          version,
          mode,
          deleted: { true: deleted.true, false: deleted.false },
          versionLatest: { true: versionLatest.true, false: versionLatest.false },
        },
      );
    }
    case APPLY_AGGREGATION: {
      if (!Array.isArray(termValue)) {
        throw new Error('term items shouild be provided in array');
      }
      return Object.assign(
        {},
        state,
        { [termName]: termValue },
      );
    }
    case APPLY_AGGREGATIONS_BATCH: {
      return Object.assign(
        {},
        state,
        { ...aggregationTerms },
      );
    }
    case CLEAR_SCHEMAS_AGGREGATIONS: {
      return initialState;
    }

    default:
      return state;
  }
};

export default aggregationsSchemasReducer;
