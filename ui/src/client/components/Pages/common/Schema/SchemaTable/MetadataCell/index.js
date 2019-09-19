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
import {
  updateFieldMetadataAsync,
  saveFieldMetadataAsync,
  deleteFieldMetadataAsync,
} from '../../../../../../actions/schemaActions/schemaActions';
import { showConfirmWindow } from '../../../../../../actions/modalActions/modalActions';
import MetadataCell from './MetadataCell';

const mapStateToProps = state => ({
  currentVersion: state.schemaReducer.version,
});

const mapDispatchToProps = (dispatch, ownProps) => ({
  updateMetadaAsync: doc => dispatch(updateFieldMetadataAsync(
    ownProps.schemaName,
    ownProps.name,
    doc,
    false,
  )),
  saveMetadaAsync: (doc, version) => dispatch(saveFieldMetadataAsync(
    ownProps.schemaName,
    ownProps.namespace,
    ownProps.name,
    doc,
    version,
  )),

  deleteMetadata: version => dispatch(showConfirmWindow(
    true,
    () => dispatch(deleteFieldMetadataAsync(
      ownProps.schemaName,
      ownProps.namespace,
      ownProps.name,
      version,
    )),
    'Are you really want to delete description?',
  )),
});

export default connect(mapStateToProps, mapDispatchToProps)(MetadataCell);
