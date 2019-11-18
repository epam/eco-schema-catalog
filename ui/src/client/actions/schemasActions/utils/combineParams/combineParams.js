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

const combineParams = (params, newParams = {}, querySettings = []) => {
  const mergedParams = Object.assign({}, params, newParams);
  const { query } = mergedParams;
  Object.keys(mergedParams).forEach((key) => {
    if (Array.isArray(mergedParams[key])) {
      if (mergedParams[key].length > 0) {
        mergedParams[key] = mergedParams[key].join(',');
      } else {
        delete mergedParams[key];
      }
    }
  });
  if (querySettings.length > 0) {
    mergedParams.query = `${query} ${querySettings.join(' ')}`.trim();
  }
  return mergedParams;
};

export default combineParams;
