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

import { uniq, cloneDeep } from 'lodash-es';
import toogleArray from '../../utils/toggleArrayItem/toggleArrayItem';
import { createMetadata, assingMetadataDocs } from './utils/metadata/metadata';

import {
  TABLE, FIELD, SCHEMA,
  SELECT_SCHEMA,
  CHANGE_SCHEMA_VIEW,
  GOT_SCHEMA,
  GOT_SCHEMA_HISTORY,
  GOT_SCHEMA_DETAILS,
  APPLY_SCHEMA_AGGREGATION,
  FILTER_BY_NAME,
  UPDATE_METADATA_SCHEMA,
  UPDATE_METADATA_FIELD,
  DELETE_METADATA_SCHEMA,
  DELETE_METADATA_FIELD,
  CLEAR_SCHEMA_AGGREGATIONS,
  CLOSE_SCHEMA,
  GOT_COMPATIBILITY_LEVELS,
  SET_COMPATIBILITY_LEVEL,
  UPDATE_METADATA_SCHEMA_SAVED,
  UPDATE_METADATA_FIELD_SAVED,
  UPDATE_METADATA_FIELD_VALUE,
  UPDATE_METADATA_SCHEMA_VALUE,
} from '../../consts/consts';
import getFlattenSchemas from './utils/getFlattenSchemas/getFlattenSchemas';

const initialState = {
  view: TABLE,
  subject: null,
  globalCompatibilityLevel: true,
  version: null,
  versionLatest: false,

  schemas: [],
  deleted: false,

  schemaMetadata: null,
  isSchemaMetadataSaved: true,

  diff: [],
  details: {},

  aggregations: {
    types: [],
    logicalTypes: [],
  },

  checkedAggregations: {
    types: [],
    logicalTypes: [],
  },
  compatibilityLevels: [],
  schemaNameFilter: '',
  schemaRegistryId: null,
  createdTimestamp: null,
  deletedTimestamp: null,
};

const schemaReducer = (state = initialState, action) => {
  const {
    type,
    subject,
    version,
    schemas,
    deleted,
    mode,
    schemaMetadata,
    versionLatest,
    compatibilityLevel,
    globalCompatibilityLevel,
    compatibilityLevels,
    view,
    diff,
    details,
    aggrKey,
    aggrValue,
    schemaNameFilterValue,
    schemaName,
    name,
    metadataDoc,
    metadataFormattedDoc,
    isSaved,
    schemaRegistryId,
    createdTimestamp,
    deletedTimestamp,
  } = action;
  switch (type) {
    case SELECT_SCHEMA: {
      return Object.assign(
        {},
        state,
        { subject, version },
      );
    }

    case CHANGE_SCHEMA_VIEW: {
      return Object.assign({}, state, { view });
    }

    case GOT_SCHEMA: {
      const tableRows = getFlattenSchemas(schemas);
      const availableTypes = uniq(tableRows
        .reduce((acc, row) => acc.concat(row.type), []));
      const availableLogicalTypes = uniq(tableRows
        .reduce((acc, row) => acc.concat(row.logicalType), []));
      const aggr = {
        ...state.aggregations,
        types: availableTypes,
        logicalTypes: availableLogicalTypes,
      };
      return {
        ...state,
        schemas: tableRows,
        compatibilityLevel,
        deleted,
        schemaMetadata,
        versionLatest,
        mode,
        isSchemaMetadataSaved: true,
        aggregations: aggr,
        globalCompatibilityLevel,
        schemaRegistryId,
        createdTimestamp,
        deletedTimestamp,
      };
    }

    case GOT_SCHEMA_HISTORY: {
      return Object.assign({}, state, { diff: diff.reverse() });
    }

    case GOT_SCHEMA_DETAILS: {
      return Object.assign({}, state, { details });
    }

    case GOT_COMPATIBILITY_LEVELS: {
      return { ...state, compatibilityLevels };
    }

    case SET_COMPATIBILITY_LEVEL: {
      return { ...state, compatibilityLevel, globalCompatibilityLevel: false };
    }

    case APPLY_SCHEMA_AGGREGATION: {
      const clone = state.checkedAggregations[aggrKey].slice(0);
      const newCheckedAggregations = Object.assign(
        {},
        state.checkedAggregations,
        { [aggrKey]: toogleArray(clone, aggrValue) },
      );
      return Object.assign({}, state, { checkedAggregations: newCheckedAggregations });
    }

    case FILTER_BY_NAME: {
      return Object.assign({}, state, { schemaNameFilter: schemaNameFilterValue });
    }

    case UPDATE_METADATA_SCHEMA: {
      if (state.schemaMetadata === null) {
        return Object.assign({}, state, {
          schemaMetadata: createMetadata(
            SCHEMA,
            state.subject,
            state.version,
            metadataDoc,
            metadataFormattedDoc,
          ),
        });
      }
      return Object.assign({}, state, {
        schemaMetadata: assingMetadataDocs(
          state.schemaMetadata,
          metadataDoc,
          metadataFormattedDoc,
        ),
      });
    }

    case UPDATE_METADATA_SCHEMA_SAVED: {
      return {
        ...state,
        isSchemaMetadataSaved: isSaved,
      };
    }

    case UPDATE_METADATA_SCHEMA_VALUE: {
      return {
        ...state,
        schemaMetadata,
      };
    }

    case UPDATE_METADATA_FIELD: {
      const index = state.schemas.findIndex(item => (
        schemaName === item.schemaName && name === item.name
      ));
      const schemasCopy = cloneDeep(state.schemas);
      let metadata = schemasCopy[index].metadata;
      if (metadata === null) {
        metadata = createMetadata(
          FIELD,
          state.subject,
          state.version,
          metadataDoc,
          metadataFormattedDoc,
          schemaName,
          name,
        );
      } else {
        metadata = assingMetadataDocs(metadata, metadataDoc, metadataFormattedDoc);
      }
      schemasCopy[index].metadata = metadata;
      return Object.assign({}, state, { schemas: schemasCopy });
    }

    case UPDATE_METADATA_FIELD_SAVED: {
      const fieldIndex = state.schemas.findIndex(
        item => schemaName === item.schemaName && name === item.name,
      );
      if (fieldIndex > -1) {
        const schemaItem = cloneDeep(state.schemas[fieldIndex]);
        schemaItem.isSaved = isSaved;
        return {
          ...state,
          schemas: [
            ...state.schemas.slice(0, fieldIndex),
            schemaItem,
            ...state.schemas.slice(fieldIndex + 1),
          ],
        };
      }
      return state;
    }

    case UPDATE_METADATA_FIELD_VALUE: {
      const updatedSchema = getFlattenSchemas(schemas);
      const updatedField = updatedSchema.find(
        item => schemaName === item.schemaName && name === item.name,
      );
      const originFieldIndex = state.schemas.findIndex(
        item => schemaName === item.schemaName && name === item.name,
      );
      if (updatedField && originFieldIndex > -1) {
        return {
          ...state,
          schemas: [
            ...state.schemas.slice(0, originFieldIndex),
            updatedField,
            ...state.schemas.slice(originFieldIndex + 1),
          ],
        };
      }
      return state;
    }

    case DELETE_METADATA_FIELD: {
      const index = state.schemas.findIndex(item => (
        schemaName === item.schemaName && name === item.name
      ));
      const schemasCopy = cloneDeep(state.schemas);
      schemasCopy[index].metadata = null;
      return Object.assign({}, state, { schemas: schemasCopy });
    }

    case DELETE_METADATA_SCHEMA: {
      return Object.assign({}, state, { schemaMetadata: null });
    }

    case CLEAR_SCHEMA_AGGREGATIONS: {
      return Object.assign({}, state, { checkedAggregations: initialState.checkedAggregations });
    }

    case CLOSE_SCHEMA: {
      return initialState;
    }

    default:
      return state;
  }
};

export default schemaReducer;
