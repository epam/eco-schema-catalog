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
import { getIsClearAll } from '../../../selectors/advancedSearchSelectors/advancedSearchSelectors';
import SchemasContainer from './SchemasContainer';
import { saveSearch, applyAggregationsBatch } from '../../../actions/schemasActions/schemasActions';

const mapStateToProps = state => ({
  query: state.schemasReducer.query,
  isHaveAnyCheckedAggregation: getIsClearAll(state),
});

const mapDispatchToProps = dispatch => ({
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
      deleted = [],
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
      deleted,
    }));
  },
});

export default withRouter(connect(
  mapStateToProps,
  mapDispatchToProps,
)(SchemasContainer));
