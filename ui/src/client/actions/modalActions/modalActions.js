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

export const showConfirmWindow = (isShow, cb = null, message = '', header) => ({
  type: SHOW_CONFIRM_MODAL,
  isShow,
  callback: cb,
  message,
  header,
});

export const showCreateModalWindow = () => ({
  type: SHOW_CREATE_SCHEMA_MODAL,
});

export const showTestSchemaModalWindow = () => ({
  type: SHOW_TEST_SCHEMA_MODAL,
});

export const hideModalWindow = () => ({
  type: HIDE_MODAL,
});
