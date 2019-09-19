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
import sortByNamespace from '../utils/sortByNamespace/sortByNamespace';

export const getSchemas = state => state.schemasReducer.schemas;
export const getTotalPages = state => state.schemasReducer.totalPages;
export const getTotalElements = state => state.schemasReducer.totalElements;
export const getPageSize = state => state.schemasReducer.pageSize;
export const getMaxResultWindow = state => state.schemasReducer.maxResultWindow;

export const getSortedAlphabeticaly = createSelector(
  getSchemas,
  (schemas) => {
    const reg = new RegExp('datafactory', 'i');
    const schemasWithDatafactory = schemas.filter(schema => reg.test(schema.namespace));
    const schemasWithOutDatafactory = schemas.filter(schema => !reg.test(schema.namespace));
    return [
      ...schemasWithOutDatafactory.sort(sortByNamespace),
      ...schemasWithDatafactory.sort(sortByNamespace),
    ];
  },
);

export const getLimitedTotalElements = createSelector(
  getTotalElements,
  getMaxResultWindow,
  (totalElements, maxResultWindow) => (
    totalElements > maxResultWindow ? maxResultWindow : totalElements
  ),
);

export const getLimitedTotalPages = createSelector(
  getTotalElements,
  getPageSize,
  getMaxResultWindow,
  getTotalPages,
  (totalElements, pageSize, maxResultWindow, totalPages) => (totalElements > maxResultWindow
    ? (maxResultWindow / pageSize)
    : totalPages),
);

export const getIsExeedingMaxResult = createSelector(
  getTotalElements,
  getMaxResultWindow,
  (totalElements, maxResultWindow) => (totalElements > maxResultWindow),
);
