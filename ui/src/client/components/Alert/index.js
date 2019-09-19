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
/* eslint-disable import/extensions */
/* eslint-disable import/no-unresolved */
import { connect } from 'react-redux';
import { AlertPopUp } from 'react-eco-ui';
import { resetMessages } from '../../actions/alertActions/alertActions';

export const getErrorMessage = state => state.alertReducer.error;
export const getSuccessMessage = state => state.alertReducer.success;

const mapStateToProps = state => ({
  error: getErrorMessage(state),
  success: getSuccessMessage(state),
});

const mapDispatchToProps = dispatch => ({
  resetMessages: () => {
    dispatch(resetMessages());
  },
});

export default connect(mapStateToProps, mapDispatchToProps)(AlertPopUp);
