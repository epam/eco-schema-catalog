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
import { withRouter } from 'react-router-dom';
import {
  getSchemasAsync,
  expandeSchema,
  syncSearchWithHistory,
  saveSearch,
  applyAggregationsBatch,
} from '../../../../actions/schemasActions/schemasActions';
import { selectSchema, closeSchema } from '../../../../actions/schemaActions/schemaActions';
import {
  getSchemas,
  getIsExeedingMaxResult,
  getTotalElements,
  getLimitedTotalElements,
} from '../../../../selectors/schemasSelectors/schemasSelectors';
import Schemas from './Schemas';

const mapStateToProps = state => ({
  query: state.schemasReducer.query,
  schemas: getSchemas(state),
  totalElements: getTotalElements(state),
  limitedTotalElements: getLimitedTotalElements(state),
  isExeedingMaxResult: getIsExeedingMaxResult(state),
  isLoading: state.schemasReducer.isLoading,
  subject: state.schemaReducer.subject,
  version: state.schemaReducer.version,
});

const mapDispatchToProps = dispatch => ({
  selectSchema: (subject, version) => {
    dispatch(selectSchema(subject, version));
  },
  applySearchParams: (params) => {
    const {
      query,
      page,
      pageSize,
      compatibilityTerm = [],
      metadataUpdatedByTerm = [],
      namespaceTerm = [],
      versionTerm = [],
      versionLatestTerm = [],
      deletedTerm = [],
    } = params;
    dispatch(saveSearch({
      query,
      page,
      pageSize,
    }));
    dispatch(applyAggregationsBatch({
      compatibilityTerm,
      metadataUpdatedByTerm,
      namespaceTerm,
      versionTerm,
      versionLatestTerm,
      deletedTerm,
    }));
  },
  getSchemas: params => dispatch(getSchemasAsync(params)),
  expandeSchema: (isExpanded) => { dispatch(expandeSchema(isExpanded)); },
  closeSchema: () => {
    dispatch(closeSchema());
    dispatch(syncSearchWithHistory());
  },
});

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Schemas));
