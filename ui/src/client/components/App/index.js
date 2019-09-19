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
import { getQueryExamplesAsync } from '../../actions/schemasActions/schemasActions';
import App from './App';

const mapStateToProps = state => ({
  page: state.schemasReducer.page,
  schemas: state.schemasReducer.schemas,
  isExpandedSchema: state.schemasReducer.isExpandedSchema,
});

const mapDispatchToProps = dispatch => ({
  getQueryExamples: () => {
    dispatch(getQueryExamplesAsync());
  },
});

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(App));
