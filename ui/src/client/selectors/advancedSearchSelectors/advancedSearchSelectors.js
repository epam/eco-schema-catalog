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

import { createSelector } from 'reselect';

export const getRootNamspace = state => state.aggregationsSchemasReducer.rootNamespace;

export const getNamespaceTerm = state => state.aggregationsSchemasReducer.namespaceTerm;
export const getCompatibilityTerm = state => state.aggregationsSchemasReducer.compatibilityTerm;
export const getModeTerm = state => state.aggregationsSchemasReducer.modeTerm;
export const getDeletedTerm = state => state.aggregationsSchemasReducer.deletedTerm;
export const getVersionTerm = state => state.aggregationsSchemasReducer.versionTerm;
export const getVersionLatestTerm = state => state.aggregationsSchemasReducer.versionLatestTerm;

const sortBykey = (a, b) => {
  if (a > b) {
    return 1;
  }
  if (a < b) {
    return -1;
  }
  return 0;
};

export const getSortedRootNamspace = createSelector(
  getRootNamspace,
  (rootNamespace) => {
    const namspaces = Object.entries(rootNamespace);
    const reg = new RegExp('datafactory', 'i');
    const namspacesWithOutDatafactory = namspaces
      .filter(namspace => !reg.test(namspace[0]))
      .sort((a, b) => sortBykey(a[0], b[0]));
    const namspacesWithDatafactory = namspaces
      .filter(namspace => reg.test(namspace[0]))
      .sort((a, b) => sortBykey(a[0], b[0]));

    return [...namspacesWithOutDatafactory, ...namspacesWithDatafactory]
      .reduce((acc, namspace) => Object.assign(acc, { [namspace[0]]: namspace[1] }), {});
  },
);

export const getIsClearAll = createSelector(
  [
    getCompatibilityTerm,
    getNamespaceTerm,
    getModeTerm,
    getDeletedTerm,
    getVersionTerm,
    getVersionLatestTerm,
  ],
  (...args) => args.reduce((acc, item) => acc || !!item.length, false),
);
