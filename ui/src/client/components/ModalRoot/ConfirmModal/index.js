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
import { showConfirmWindow } from '../../../actions/modalActions/modalActions';
import ConfirmModal from './ConfirmModal';

const mapStateToProps = state => ({
  isShowModal: state.modalReducer.showConfirmModal,
  message: state.modalReducer.message,
  callbackAsync: state.modalReducer.callback,
  header: state.modalReducer.header,
  buttonLabel: state.modalReducer.buttonLabel
});

const mapDispatchToProps = dispatch => ({
  closeModal: () => {
    dispatch(showConfirmWindow(false));
  },
});


export default connect(mapStateToProps, mapDispatchToProps)(ConfirmModal);
