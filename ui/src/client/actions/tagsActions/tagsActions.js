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
import { getTags } from '../../services/httpService';
import { gotErrorMessage } from '../alertActions/alertActions';
import { GOT_TAGS } from '../../consts/consts';

export const gotTags = tags => ({
  type: GOT_TAGS,
  tags,
});

export const getTagsAsync = (subject, version) => dispatch => getTags(subject, version)
  .then(tags => dispatch(gotTags(tags)))
  .catch((error) => {
    const { message } = error;
    dispatch(gotErrorMessage({ message }));
    return Promise.resolve();
  });
