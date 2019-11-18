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
import { getUpdatedAt, getUpdatedBy, getOriginMetadataVersion } from '../../../../selectors/schemaSelectors/schemaSelectors';
import { deleteSchemaMetadataAsync, deleteSchemasActionAsync, deleteSchemaActionAsync } from '../../../../actions/schemaActions/schemaActions';
import { showConfirmWindow, showTestSchemaModalWindow } from '../../../../actions/modalActions/modalActions';
import Schema from './Schema';

const mapStateToProps = state => ({
  view: state.schemaReducer.view,
  subject: state.schemaReducer.subject,
  mode: state.schemaReducer.mode,
  version: state.schemaReducer.version,
  isDeleted: state.schemaReducer.deleted,
  versionLatest: state.schemaReducer.versionLatest,
  updatedBy: getUpdatedBy(state),
  updatedAt: getUpdatedAt(state),
  originMetadataVersion: getOriginMetadataVersion(state),
});

const mapDispatchToProps = dispatch => ({
  deleteMetadata: version => dispatch(showConfirmWindow(
    true,
    () => dispatch(deleteSchemaMetadataAsync(version)),
    'Are you really want to delete description?',
  )),
  deleteSchema: () => dispatch(showConfirmWindow(
    true,
    () => dispatch(deleteSchemaActionAsync()),
    'Are you really want to delete that schema version?',
  )),
  deleteSchemas: () => dispatch(showConfirmWindow(
    true,
    () => dispatch(deleteSchemasActionAsync()),
    'Are you really want to delete all schema versions?',
  )),
  showTestSchemaModal: () => dispatch(showTestSchemaModalWindow()),

});

export default (connect(mapStateToProps, mapDispatchToProps)(Schema));
