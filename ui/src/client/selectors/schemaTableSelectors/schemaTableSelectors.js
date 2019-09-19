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
import { cloneDeep } from 'lodash-es';

export const getTableRows = state => state.schemaReducer.schemas;
export const getSchemaNameFilter = state => state.schemaReducer.schemaNameFilter;
export const getСheckedAggregations = state => state.schemaReducer.checkedAggregations;
export const getIsSchemaMetadataSaved = state => state.schemaReducer.isSchemaMetadataSaved;

export const getFilteredTableRowsByName = createSelector(
  getTableRows,
  getSchemaNameFilter,
  (rows, filterValue = '') => {
    const reg = new RegExp(filterValue.replace(/([.?*+^$[\]\\(){}|-])/g, '\\$1'), 'i');
    return rows.filter(row => reg.test(row.name));
  },
);

export const getFilteredTableRows = createSelector(
  getFilteredTableRowsByName,
  getСheckedAggregations,
  (rows, checkedAggregations) => {
    const checkedAggrs = [...checkedAggregations.types, ...checkedAggregations.logicalTypes];
    return rows
      .filter((row) => {
        const availbaleRowAggrs = [...row.type, ...row.logicalType];
        return checkedAggrs.every(checkedAggr => availbaleRowAggrs.includes(checkedAggr));
      });
  },
);

export const getPreparedTableRows = createSelector(
  getFilteredTableRows,
  (rows) => {
    let temp;
    return rows.map((row, i) => {
      if (i === 0) {
        temp = row.schemaName;
        return row;
      }
      if (temp !== row.schemaName) {
        temp = row.schemaName;
        return row;
      }
      if (temp === row.schemaName) {
        return Object.assign({}, cloneDeep(row), { schemaName: '' });
      }
      return row;
    });
  },
);

export const isHaveUnsavedRows = createSelector(
  getTableRows,
  rows => !rows.reduce((acc, row) => row.isSaved && acc, true),
);

export const isHaveUnsaved = createSelector(
  isHaveUnsavedRows,
  getIsSchemaMetadataSaved,
  (isHaveUnsavedRowsvalue, IsSchemaMetadataSaved) => (
    isHaveUnsavedRowsvalue || !IsSchemaMetadataSaved
  ),
);
