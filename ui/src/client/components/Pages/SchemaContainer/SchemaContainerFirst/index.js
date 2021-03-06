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
import { getSchemaIdentityAsync, selectSchema, closeSchema } from '../../../../actions/schemaActions/schemaActions';
import SchemaContainer from './SchemaContainerFirst';

const mapStateToProps = state => ({
  subject: state.schemaReducer.subject,
});

const mapDispatchToProps = dispatch => ({
  getSchemaIdentity: subject => dispatch(getSchemaIdentityAsync(subject)),
  selectSchema: (subject, version) => {
    dispatch(selectSchema(subject, version));
  },
  closeSchema: () => {
    dispatch(closeSchema());
  },
});

export default withRouter(connect(
  mapStateToProps,
  mapDispatchToProps,
)(SchemaContainer));
