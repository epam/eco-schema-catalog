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

const toggleArrayItem = (arr = [], value) => {
  if (value === undefined) {
    return arr;
  }
  const index = arr.findIndex(item => item === value);
  if (index !== -1) {
    return [...arr.slice(0, index), ...arr.slice(index + 1, arr.length)];
  }
  return [...arr, value];
};

export default toggleArrayItem;
