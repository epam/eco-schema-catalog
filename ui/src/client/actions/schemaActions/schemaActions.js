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

import { replace } from 'connected-react-router';
import { isFetching } from '../fetchingActions/fetchingActions';
import {
  getSchema,
  getFullDiff,
  getDetails,
  renderHTMLDoc,
  postFieldMetadata,
  postSchemaMetadata,
  deleteSchemaMetadata,
  deleteFieldMetadata,
  getSchemaIdentity,
  deleteSchema,
  deleteSchemas,
  getCompatibilityLevels,
  setCompatibilityLevel,
} from '../../services/httpService';
import { gotErrorMessage, gotSuccessMessage } from '../alertActions/alertActions';
import getSchemaFullName from './utils/getSchemaFullName/getSchemaFullName';
import {
  GOT_SCHEMA_AGGREGATIONS,
  GOT_SCHEMA_LATEST_VERSION,
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
  SET_SCHEMA_TABLE_LOADING,
  SET_SCHEMA_HISTORY_LOADING,
  SET_SCHEMA_DETAILS_LOADING,
  GOT_COMPATIBILITY_LEVELS,
  SET_COMPATIBILITY_LEVEL,
  UPDATE_METADATA_SCHEMA_SAVED,
  UPDATE_METADATA_FIELD_SAVED,
  UPDATE_METADATA_FIELD_VALUE,
  UPDATE_METADATA_SCHEMA_VALUE,
} from '../../consts/consts';

export const gotSchema = (response) => {
  const {
    schemas, schemaMetadata, deleted, versionLatest, compatibilityLevel, mode,
  } = response;
  return {
    type: GOT_SCHEMA,
    schemas,
    deleted,
    mode,
    schemaMetadata,
    versionLatest,
    compatibilityLevel,
  };
};

export const closeSchema = () => ({
  type: CLOSE_SCHEMA,
});

export const gotSchemaAggregations = aggregations => ({
  type: GOT_SCHEMA_AGGREGATIONS,
  aggregations,
});

export const gotLatestVersion = latestVesion => ({
  type: GOT_SCHEMA_LATEST_VERSION,
  latestVesion,
});

export const selectSchema = (subject, version) => (dispatch) => {
  dispatch(replace(`/schema/${subject}/${version}`));
  dispatch({
    type: SELECT_SCHEMA,
    subject,
    version,
  });
};

export const changeSchemaView = view => ({
  type: CHANGE_SCHEMA_VIEW,
  view,
});

export const applyAggregation = (aggrKey, aggrValue) => ({
  type: APPLY_SCHEMA_AGGREGATION,
  aggrKey,
  aggrValue,
});

export const clearAggregations = () => ({
  type: CLEAR_SCHEMA_AGGREGATIONS,
});

export const getSchemaAsync = (subject, version, params) => (dispatch) => {
  dispatch(isFetching(SET_SCHEMA_TABLE_LOADING, true));
  return getSchema(subject, version, params)
    .then((res) => {
      dispatch(gotSchema(res));
    })
    .catch((error) => {
      dispatch(gotErrorMessage({ message: error }));
    })
    .finally(() => {
      dispatch(isFetching(SET_SCHEMA_TABLE_LOADING, false));
    });
};

export const getSchemaIdentityAsync = subject => async (dispatch) => {
  try {
    const res = await getSchemaIdentity(subject);
    return res;
  } catch (error) {
    dispatch(gotErrorMessage({ message: error }));
    return null;
  }
};

export const gotSchemaHistory = diff => ({
  type: GOT_SCHEMA_HISTORY,
  diff,
});

export const getSchemaHistoryAsync = subject => (dispatch) => {
  dispatch(isFetching(SET_SCHEMA_HISTORY_LOADING, true));
  return getFullDiff(subject)
    .then((res) => {
      dispatch(gotSchemaHistory(res));
    })
    .catch((error) => {
      const { statusText } = error.response;
      dispatch(gotErrorMessage({ message: statusText }));
    })
    .finally(() => {
      dispatch(isFetching(SET_SCHEMA_HISTORY_LOADING, false));
    });
};

export const gotDetails = details => ({
  type: GOT_SCHEMA_DETAILS,
  details,
});

export const getDetailsAsync = (subject, version) => (dispatch) => {
  dispatch(isFetching(SET_SCHEMA_DETAILS_LOADING, true));
  return getDetails(subject, version)
    .then((res) => {
      dispatch(gotDetails(res));
      return res;
    })
    .catch((error) => {
      dispatch(gotErrorMessage({ message: error.toString() }));
      return error;
    })
    .finally(() => {
      dispatch(isFetching(SET_SCHEMA_DETAILS_LOADING, false));
    });
};


export const filterByName = value => ({
  type: FILTER_BY_NAME,
  schemaNameFilterValue: value,
});


export const updateSchemaMetadata = (metadataDoc, metadataFormattedDoc) => ({
  type: UPDATE_METADATA_SCHEMA,
  metadataDoc,
  metadataFormattedDoc,
});

export const updateSchemaMetadataSaved = isSaved => ({
  type: UPDATE_METADATA_SCHEMA_SAVED,
  isSaved,
});

export const updateSchemaMetadataValue = (schemaMetadata = null) => ({
  type: UPDATE_METADATA_SCHEMA_VALUE,
  schemaMetadata,
});

export const updateSchemaMetadataAsync = metadataDoc => dispatch => (
  renderHTMLDoc(metadataDoc)
    .then((metadataFormattedDoc) => {
      dispatch(updateSchemaMetadata(metadataDoc, metadataFormattedDoc));
      dispatch(updateSchemaMetadataSaved(false));
    })
    .catch((error) => {
      const { statusText } = error.response;
      dispatch(gotErrorMessage({ message: statusText }));
    }));

export const saveSchemaMetadataAsync = (doc, postingVersion) => async (dispatch, getState) => {
  const { subject, version } = getState().schemaReducer;
  try {
    const message = await postSchemaMetadata(subject, postingVersion, doc);
    dispatch(gotSuccessMessage(message));
    dispatch(updateSchemaMetadataSaved(true));
    // to get updatedAt and updatedBy metadata values
    const { schemaMetadata } = await getSchema(subject, version);
    dispatch(updateSchemaMetadataValue(schemaMetadata));
  } catch (error) {
    const { statusText } = error.response;
    dispatch(gotErrorMessage({ message: statusText }));
  }
};

export const updateFieldMetadata = (...args) => {
  const [schemaName, name, metadataDoc, metadataFormattedDoc] = args;
  return {
    type: UPDATE_METADATA_FIELD,
    schemaName,
    name,
    metadataDoc,
    metadataFormattedDoc,
  };
};

export const updateFieldMetadataSaved = (schemaName, name, isSaved) => ({
  type: UPDATE_METADATA_FIELD_SAVED,
  isSaved,
  schemaName,
  name,
});

export const updateFieldMetadataValue = (schemas, schemaName, name) => ({
  type: UPDATE_METADATA_FIELD_VALUE,
  schemas,
  schemaName,
  name,
});

export const updateFieldMetadataAsync = (schemaName, name, metadataDoc) => dispatch => (
  renderHTMLDoc(metadataDoc)
    .then((metadataFormattedDoc) => {
      dispatch(updateFieldMetadata(
        schemaName,
        name,
        metadataDoc,
        metadataFormattedDoc,
      ));
      dispatch(updateFieldMetadataSaved(schemaName, name, false));
    })
    .catch((error) => {
      const { statusText } = error.response;
      dispatch(gotErrorMessage({ message: statusText }));
    }));

export const saveFieldMetadataAsync = (...args) => async (dispatch, getState) => {
  const [schemaName, namespace, name, doc, version] = args;
  const state = getState().schemaReducer;
  try {
    const message = await postFieldMetadata(
      state.subject,
      version,
      getSchemaFullName(namespace, schemaName),
      name,
      doc,
    );
    dispatch(gotSuccessMessage(message));
    dispatch(updateFieldMetadataSaved(schemaName, name, true));
    // to get updatedAt and updatedBy metadata values
    const res = await getSchema(state.subject, state.version);
    dispatch(updateFieldMetadataValue(res.schemas, schemaName, name));
    return message;
  } catch (error) {
    const { message } = error.response;
    dispatch(gotErrorMessage({ message }));
    return message;
  }
};

export const deleteSchemaMetadataAsync = version => (dispatch, getState) => {
  const { subject } = getState().schemaReducer;
  return deleteSchemaMetadata(subject, version)
    .then((message) => {
      dispatch({ type: DELETE_METADATA_SCHEMA });
      dispatch(gotSuccessMessage(message));
    })
    .catch((error) => {
      const { statusText } = error.response;
      dispatch(gotErrorMessage({ message: statusText }));
    });
};

const deleteFiledMetadata = (schemaName, name) => ({
  type: DELETE_METADATA_FIELD,
  schemaName,
  name,
});

export const deleteFieldMetadataAsync = (...args) => (dispatch, getState) => {
  const { subject } = getState().schemaReducer;
  const [schemaName, namespace, name, version] = args;
  return deleteFieldMetadata(
    subject,
    version,
    getSchemaFullName(namespace, schemaName),
    name,
  )
    .then((message) => {
      dispatch(deleteFiledMetadata(schemaName, name));
      dispatch(gotSuccessMessage(message));
    })
    .catch((error) => {
      const { statusText } = error.response;
      dispatch(gotErrorMessage({ message: statusText }));
    });
};

export const deleteSchemaActionAsync = () => async (dispatch, getState) => {
  const { subject, version } = getState().schemaReducer;
  try {
    await deleteSchema(subject, version);
    dispatch(getSchemaAsync(subject, version));
    dispatch(gotSuccessMessage({ message: 'deleted' }));
    return 'deleted';
  } catch (error) {
    dispatch(gotErrorMessage({ message: error }));
    return null;
  }
};

export const deleteSchemasActionAsync = () => async (dispatch, getState) => {
  const { subject, version } = getState().schemaReducer;
  try {
    await deleteSchemas(subject);
    dispatch(getSchemaAsync(subject, version));
    dispatch(gotSuccessMessage({ message: 'deleted' }));
    return 'deleted';
  } catch (error) {
    dispatch(gotErrorMessage({ message: error }));
    return null;
  }
};

export const getCompatibilityLevelsAsync = () => async (dispatch) => {
  try {
    const compatibilityLevels = await getCompatibilityLevels();
    dispatch({
      compatibilityLevels,
      type: GOT_COMPATIBILITY_LEVELS,
    });
    return compatibilityLevels;
  } catch (error) {
    dispatch(gotErrorMessage({ message: error }));
    return null;
  }
};

export const setCompatibilityLevelAsync = newLevel => async (dispatch, getState) => {
  const { subject, compatibilityLevel } = getState().schemaReducer;
  try {
    dispatch({
      compatibilityLevel: newLevel,
      type: SET_COMPATIBILITY_LEVEL,
    });
    const res = await setCompatibilityLevel(subject, newLevel);
    dispatch(gotSuccessMessage({ message: 'compatibility was set'}));
    return res.message;
  } catch (error) {
    dispatch({
      compatibilityLevel,
      type: SET_COMPATIBILITY_LEVEL,
    });
    dispatch(gotErrorMessage({ message: error }));
    return null;
  }
};
