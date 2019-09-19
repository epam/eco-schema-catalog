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

/* eslint-disable no-undef */
import { cloneDeep } from 'lodash-es';
import {
  getFilteredTableRows,
  getFilteredTableRowsByName,
  getPreparedTableRows,
  isHaveUnsavedRows,
  isHaveUnsaved,
} from './schemaTableSelectors';

describe('getFilteredTableRowsByName spec', () => {
  const initialState = {
    schemaReducer: {
      schemas: [
        {
          schemaName: 'TestHobby',
          name: 'kind',
        },
        {
          schemaName: 'TestHobby',
          name: 'description',
        },
        {
          schemaName: 'TestSkillLevel',
          name: 'level',
        },
        {
          schemaName: 'TestJob',
          name: 'position',
        },
        {
          schemaName: 'TestPerson',
          name: 'age',
        },
        {
          schemaName: 'TestPerson',
          name: 'hobby',
        },
        {
          schemaName: 'TestPerson',
          name: 'job',
        },
        {
          schemaName: 'TestPosition',
          name: 'skill',
        },
        {
          schemaName: 'TestPosition',
          name: 'start_date',
        },
        {
          schemaName: 'TestPosition',
          name: 'skills',
        },
      ],
      schemaNameFilter: '',
    },
  };
  it('default filterValue', () => {
    expect(getFilteredTableRowsByName(initialState)).toHaveLength(10);
  });
  it('filterValue s', () => {
    const state = cloneDeep(initialState);
    state.schemaReducer.schemaNameFilter = 's';
    const resultSchemas = [
      {
        schemaName: 'TestHobby',
        name: 'description',
      },
      {
        schemaName: 'TestJob',
        name: 'position',
      },
      {
        schemaName: 'TestPosition',
        name: 'skill',
      },
      {
        schemaName: 'TestPosition',
        name: 'start_date',
      },
      {
        schemaName: 'TestPosition',
        name: 'skills',
      },
    ];
    expect(getFilteredTableRowsByName(state)).toEqual(resultSchemas);
  });
  it('filterValue 123', () => {
    const state = cloneDeep(initialState);
    state.schemaReducer.schemaNameFilter = '123';
    expect(getFilteredTableRowsByName(state)).toHaveLength(0);
  });
  it('filterValue skill', () => {
    const state = cloneDeep(initialState);
    state.schemaReducer.schemaNameFilter = 'skill';
    expect(getFilteredTableRowsByName(state)).toHaveLength(2);
  });
  it('filterValue is space', () => {
    const state = cloneDeep(initialState);
    state.schemaReducer.schemaNameFilter = ' ';
    expect(getFilteredTableRowsByName(state)).toHaveLength(0);
  });
  it('filterValue is irregular RegEx value - *', () => {
    const state = cloneDeep(initialState);
    state.schemaReducer.schemaNameFilter = '*';
    expect(getFilteredTableRowsByName(state)).toHaveLength(0);
  });
});

describe('getFilteredTableRows spec', () => {
  const schemas = [
    {
      type: ['LONG'],
      logicalType: ['date'],
    },
    {
      type: ['BOOL'],
      logicalType: [],
    },
    {
      type: ['RECORD'],
      logicalType: [],
    },
    {
      type: ['UNION', 'NULL', 'LONG'],
      logicalType: ['timestamp-millis'],
    },
    {
      type: ['UNION', 'NULL', 'ARRAY', 'RECORD'],
      logicalType: [],
    },
  ];
  it('no checked aggrs', () => {
    const state = {
      schemaReducer: {
        schemas,
        checkedAggregations: {
          types: [],
          logicalTypes: [],
        },
      },
    };
    expect(getFilteredTableRows(state)).toHaveLength(5);
  });
  it('one checked type', () => {
    const state = {
      schemaReducer: {
        schemas,
        checkedAggregations: {
          types: ['UNION'],
          logicalTypes: [],
        },
      },
    };
    expect(getFilteredTableRows(state)).toHaveLength(2);
  });
  it('some checked types', () => {
    const state = {
      schemaReducer: {
        schemas,
        checkedAggregations: {
          types: ['UNION', 'LONG'],
          logicalTypes: [],
        },
      },
    };
    expect(getFilteredTableRows(state)).toHaveLength(1);
  });

  it('some checked types and one unexist logicalType', () => {
    const state = {
      schemaReducer: {
        schemas,
        checkedAggregations: {
          types: ['UNION'],
          logicalTypes: ['date'],
        },
      },
    };
    expect(getFilteredTableRows(state)).toHaveLength(0);
  });

  it('some checked types and one logicalType', () => {
    const state = {
      schemaReducer: {
        schemas,
        checkedAggregations: {
          types: ['UNION'],
          logicalTypes: ['timestamp-millis'],
        },
      },
    };
    expect(getFilteredTableRows(state)).toHaveLength(1);
  });

  it('cross types and logicalType', () => {
    const state = {
      schemaReducer: {
        schemas,
        checkedAggregations: {
          types: ['RECORD'],
          logicalTypes: ['timestamp-millis'],
        },
      },
    };
    expect(getFilteredTableRows(state)).toHaveLength(0);
  });
});

describe('getPreparedTableRows spec', () => {
  const initialState = {
    schemaReducer: {
      checkedAggregations: {
        types: [],
        logicalTypes: [],
      },
      schemas: [
        {
          schemaName: 'TestHobby',
          name: 'kind',
          type: ['STRING'],
          logicalType: [],
        },
        {
          schemaName: 'TestHobby',
          name: 'description',
          type: ['STRING'],
          logicalType: [],
        },
        {
          schemaName: 'TestSkillLevel',
          name: 'level',
          type: ['STRING'],
          logicalType: [],
        },
        {
          schemaName: 'TestJob',
          name: 'position',
          type: ['STRING'],
          logicalType: [],
        },
        {
          schemaName: 'TestPerson',
          name: 'age',
          type: ['INT'],
          logicalType: [],
        },
        {
          schemaName: 'TestPerson',
          name: 'hobby',
          type: ['STRING'],
          logicalType: [],
        },
        {
          schemaName: 'TestPerson',
          name: 'job',
          type: ['STRING'],
          logicalType: [],
        },
        {
          schemaName: 'TestPosition',
          name: 'skill',
          type: ['STRING'],
          logicalType: [],
        },
        {
          schemaName: 'TestPosition',
          name: 'start_date',
          type: ['STRING'],
          logicalType: [],
        },
        {
          schemaName: 'TestPosition',
          name: 'skills',
          type: ['STRING'],
          logicalType: [],
        },
      ],
      schemaNameFilter: '',
    },
  };
  it('no schemas', () => {
    const state = cloneDeep(initialState);
    state.schemaReducer.schemas = [];
    expect(getPreparedTableRows(state)).toEqual([]);
  });
  it('no filterValue', () => {
    const filteredsSchemas = [
      {
        schemaName: 'TestHobby',
        name: 'kind',
        type: ['STRING'],
        logicalType: [],
      },
      {
        schemaName: '',
        name: 'description',
        type: ['STRING'],
        logicalType: [],
      },
      {
        schemaName: 'TestSkillLevel',
        name: 'level',
        type: ['STRING'],
        logicalType: [],
      },
      {
        schemaName: 'TestJob',
        name: 'position',
        type: ['STRING'],
        logicalType: [],
      },
      {
        schemaName: 'TestPerson',
        name: 'age',
        type: ['INT'],
        logicalType: [],
      },
      {
        schemaName: '',
        name: 'hobby',
        type: ['STRING'],
        logicalType: [],
      },
      {
        schemaName: '',
        name: 'job',
        type: ['STRING'],
        logicalType: [],
      },
      {
        schemaName: 'TestPosition',
        name: 'skill',
        type: ['STRING'],
        logicalType: [],
      },
      {
        schemaName: '',
        name: 'start_date',
        type: ['STRING'],
        logicalType: [],
      },
      {
        schemaName: '',
        name: 'skills',
        type: ['STRING'],
        logicalType: [],
      },
    ];
    expect(getPreparedTableRows(initialState)).toEqual(filteredsSchemas);
  });

  it('filterValue \'skill\'', () => {
    const state = cloneDeep(initialState);
    state.schemaReducer.schemaNameFilter = 'skill';
    const filteredsSchemas = [
      {
        schemaName: 'TestPosition',
        name: 'skill',
        type: ['STRING'],
        logicalType: [],
      },
      {
        schemaName: '',
        name: 'skills',
        type: ['STRING'],
        logicalType: [],
      },
    ];
    expect(getPreparedTableRows(state)).toEqual(filteredsSchemas);
  });
});

describe('isHaveUnsavedRows spec', () => {
  it('all saved', () => {
    const state = {
      schemaReducer: {
        schemas: [
          { isSaved: true },
          { isSaved: true },
          { isSaved: true },
        ],
      },
    };
    expect(isHaveUnsavedRows(state)).toEqual(false);
  });
  it('one unsaved filed', () => {
    const state = {
      schemaReducer: {
        schemas: [
          { isSaved: true },
          { isSaved: false },
          { isSaved: true },
        ],
      },
    };
    expect(isHaveUnsavedRows(state)).toEqual(true);
  });
  it('all unsaved fileds', () => {
    const state = {
      schemaReducer: {
        schemas: [
          { isSaved: false },
          { isSaved: false },
          { isSaved: false },
        ],
      },
    };
    expect(isHaveUnsavedRows(state)).toEqual(true);
  });
  it('all schema and rows saved', () => {
    const state = {
      schemaReducer: {
        isSchemaMetadataSaved: true,
        schemas: [
          { isSaved: true },
          { isSaved: true },
          { isSaved: true },
        ],
      },
    };
    expect(isHaveUnsaved(state)).toEqual(false);
  });
  it('have unsaved schema', () => {
    const state = {
      schemaReducer: {
        isSchemaMetadataSaved: false,
        schemas: [
          { isSaved: true },
          { isSaved: true },
          { isSaved: true },
        ],
      },
    };
    expect(isHaveUnsaved(state)).toEqual(true);
  });
  it('have unsaved schema and some of rows', () => {
    const state = {
      schemaReducer: {
        isSchemaMetadataSaved: false,
        schemas: [
          { isSaved: true },
          { isSaved: false },
          { isSaved: true },
        ],
      },
    };
    expect(isHaveUnsaved(state)).toEqual(true);
  });
  it('have saved schema and some of rows unsaved', () => {
    const state = {
      schemaReducer: {
        isSchemaMetadataSaved: true,
        schemas: [
          { isSaved: true },
          { isSaved: false },
          { isSaved: true },
        ],
      },
    };
    expect(isHaveUnsaved(state)).toEqual(true);
  });
});
