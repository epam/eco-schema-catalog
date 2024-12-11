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
import {
  SHOW_CONFIRM_MODAL, SHOW_CREATE_SCHEMA_MODAL, SHOW_TEST_SCHEMA_MODAL, HIDE_MODAL,
} from '../../consts/consts';

const initialState = {
  showConfirmModal: false,
  showCreateSchemaModal: false,
  showTestSchemaModal: false,
  message: '',
  header: '',
  callback: null,
  buttonLabel: 'Delete'
};

const modalReducer = (state = initialState, action) => {
  const {
    isShow, callback, message, header, buttonLabel
  } = action;
  switch (action.type) {
    case SHOW_CONFIRM_MODAL: {
      return Object.assign(
        {},
        initialState,
        {
          showConfirmModal: isShow, callback, message, header, buttonLabel
        },
      );
    }
    case SHOW_CREATE_SCHEMA_MODAL: {
      return Object.assign(
        {},
        initialState,
        { showCreateSchemaModal: true },
      );
    }
    case SHOW_TEST_SCHEMA_MODAL: {
      return Object.assign(
        {},
        initialState,
        { showTestSchemaModal: true },
      );
    }
    case HIDE_MODAL: {
      return initialState;
    }
    default:
      return state;
  }
};

export default modalReducer;
