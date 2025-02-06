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

import { connect } from 'react-redux';
import toggleArrayItem from '../../../../utils/toggleArrayItem/toggleArrayItem';
import {
  NAMESPACE_TERM,
  COMPATIBILITY_TERM,
  METADATA_UPDATED_BY_TERM,
  DELETED_TERM,
  VERSION_TERM,
  VERSION_LATEST_TERM,
  MODE_TERM,
} from '../../../../consts/terms';
import { getSchemasAsync, applyAggregation, applyAggregationsBatch } from '../../../../actions/schemasActions/schemasActions';
import { closeSchema } from '../../../../actions/schemaActions/schemaActions';
import {
  getCompatibilityTerm,
  getNamespaceTerm,
  getMetadataUpdatedByTerm,
  getSortedRootNamspace,
  getIsClearAll,
  getDeletedTerm,
  getVersionLatestTerm,
  getVersionTerm,
  getModeTerm,
} from '../../../../selectors/advancedSearchSelectors/advancedSearchSelectors';
import AdvancedSearch from './AdvancedSearch';

const mapStateToProps = state => ({
  rootNamespace: getSortedRootNamspace(state),
  compatibility: state.aggregationsSchemasReducer.compatibility,
  metadataUpdatedBy: state.aggregationsSchemasReducer.metadataUpdatedBy,
  mode: state.aggregationsSchemasReducer.mode,
  deleted: state.aggregationsSchemasReducer.deleted,
  version: state.aggregationsSchemasReducer.version,
  versionLatest: state.aggregationsSchemasReducer.versionLatest,

  metadataUpdatedByTerm: getMetadataUpdatedByTerm(state),
  namespaceTerm: getNamespaceTerm(state),
  compatibilityTerm: getCompatibilityTerm(state),
  modeTerm: getModeTerm(state),
  deletedTerm: getDeletedTerm(state),
  versionTerm: getVersionTerm(state),
  versionLatestTerm: getVersionLatestTerm(state),

  isClearAll: getIsClearAll(state),
});


const mapDispatchToProps = dispatch => ({
  applyMultipleAggregation: (termName, termValue, appliedTerms) => {
    const toggledArray = toggleArrayItem(appliedTerms, termValue);
    dispatch(closeSchema());
    dispatch(applyAggregation(termName, toggledArray));
    dispatch(getSchemasAsync({ page: 0 }));
  },
  clearAllTerms: () => {
    dispatch(closeSchema());
    dispatch(applyAggregationsBatch({
      [COMPATIBILITY_TERM]: [],
      [DELETED_TERM]: [],
      [METADATA_UPDATED_BY_TERM]: [],
      [NAMESPACE_TERM]: [],
      [MODE_TERM]: [],
      [VERSION_TERM]: [],
      [VERSION_LATEST_TERM]: [],
    }));
    dispatch(getSchemasAsync());
  },
});

export default connect(mapStateToProps, mapDispatchToProps)(AdvancedSearch);
