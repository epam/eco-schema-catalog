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
import schemaReducer from '../schemaReducer';
import {
  gotSchema,
  applyAggregation,
  clearAggregations,
  filterByName,
  updateFieldMetadata,
  updateSchemaMetadata,
} from '../../../actions/schemaActions/schemaActions';
import { TABLE } from '../../../consts/consts';
import { response, flattenSchemas } from './schemaMockResponse';

describe('schema reducer spec', () => {
  it('should return the initial state', () => {
    const state = {
      view: TABLE,
      subject: null,
      version: null,
      versionLatest: false,
      globalCompatibilityLevel: true,
      schemas: [],
      deleted: false,
      schemaMetadata: null,
      isSchemaMetadataSaved: true,
      diff: [],
      details: {},
      aggregations: {
        types: [],
        logicalTypes: [],
      },
      checkedAggregations: {
        types: [],
        logicalTypes: [],
      },
      compatibilityLevels: [],
      schemaNameFilter: '',
      schemaRegistryId: null,
      createdTimestamp: null,
      deletedTimestamp: null
    };
    expect(schemaReducer(undefined, {})).toEqual(state);
  });

  it('should get schema rows', () => {
    const someInitialState = {
      schemaMetadata: null,
      isSchemaMetadataSaved: true,
      schemas: [],
      deleted: false,
      aggregations: {
        types: [],
        logicalTypes: [],
      },
      checkedAggregations: {
        types: [],
        logicalTypes: [],
      },
    };
    const state = {
      schemaMetadata: response.schemaMetadata,
      isSchemaMetadataSaved: true,
      schemas: flattenSchemas,
      deleted: false,
      aggregations: {
        types: ['RECORD', 'UNION', 'STRING', 'MAP', 'NULL', 'LONG', 'ARRAY'],
        logicalTypes: ['timestamp-millis'],
      },
      checkedAggregations: {
        types: [],
        logicalTypes: [],
      },
    };
    expect(schemaReducer(someInitialState, gotSchema(response))).toEqual(state);
  });

  it('should apply \'type\' aggregation', () => {
    const someInitialState = {
      aggregations: {
        types: ['STRING', 'RECORD', 'UNION', 'MAP'],
        logicalTypes: ['date', 'millis', 'seconds'],
      },
      checkedAggregations: {
        types: [],
        logicalTypes: [],
      },
    };
    const state = {
      aggregations: {
        types: ['STRING', 'RECORD', 'UNION', 'MAP'],
        logicalTypes: ['date', 'millis', 'seconds'],
      },
      checkedAggregations: {
        types: ['STRING'],
        logicalTypes: [],
      },
    };
    expect(schemaReducer(someInitialState, applyAggregation('types', 'STRING'))).toEqual(state);
  });

  it('should apply \'logicalType\'aggregation with already checked type', () => {
    const someInitialState = {
      aggregations: {
        types: ['STRING', 'RECORD', 'UNION', 'MAP'],
        logicalTypes: ['date', 'millis', 'seconds'],
      },
      checkedAggregations: {
        types: ['STRING'],
        logicalTypes: [],
      },
    };
    const state = {
      aggregations: {
        types: ['STRING', 'RECORD', 'UNION', 'MAP'],
        logicalTypes: ['date', 'millis', 'seconds'],
      },
      checkedAggregations: {
        types: ['STRING'],
        logicalTypes: ['date'],
      },
    };
    expect(schemaReducer(someInitialState, applyAggregation('logicalTypes', 'date'))).toEqual(state);
  });

  it('should add \'type\'aggregation to already checked types and logicaTypes', () => {
    const someInitialState = {
      aggregations: {
        types: ['STRING', 'RECORD', 'UNION', 'MAP'],
        logicalTypes: ['date', 'millis', 'seconds'],
      },
      checkedAggregations: {
        types: ['STRING'],
        logicalTypes: ['date'],
      },
    };
    const state = {
      aggregations: {
        types: ['STRING', 'RECORD', 'UNION', 'MAP'],
        logicalTypes: ['date', 'millis', 'seconds'],
      },
      checkedAggregations: {
        types: ['STRING', 'UNION'],
        logicalTypes: ['date'],
      },
    };
    expect(schemaReducer(someInitialState, applyAggregation('types', 'UNION'))).toEqual(state);
  });


  it('should clear all applied aggregation', () => {
    const someInitialState = {
      aggregations: {
        types: ['STRING', 'RECORD', 'UNION', 'MAP'],
        logicalTypes: ['date', 'millis', 'seconds'],
      },
      checkedAggregations: {
        types: ['STRING', 'RECORD'],
        logicalTypes: ['date', 'millis'],
      },
    };

    const state = {
      aggregations: {
        types: ['STRING', 'RECORD', 'UNION', 'MAP'],
        logicalTypes: ['date', 'millis', 'seconds'],
      },
      checkedAggregations: {
        types: [],
        logicalTypes: [],
      },
    };
    expect(schemaReducer(someInitialState, clearAggregations())).toEqual(state);
  });

  it('should apply filterValue', () => {
    const someInitialState = {
      schemaNameFilter: '',
    };
    const state = {
      schemaNameFilter: '123',
    };
    expect(schemaReducer(someInitialState, filterByName('123'))).toEqual(state);
  });

  it('should update metadata of field if it is null', () => {
    const someInitialState = {
      subject: 'rb.test-1',
      version: 1,
      schemas: [
        {
          schemaName: 'TestPosition',
          name: 'skills',
          metadata: null,
        },
      ],
    };

    const state = {
      subject: 'rb.test-1',
      version: 1,
      schemas: [
        {
          schemaName: 'TestPosition',
          name: 'skills',
          metadata: {
            key: {
              subject: 'rb.test-1',
              version: 1,
              schemaFullName: 'TestPosition',
              field: 'skills',
              type: 'FIELD',
            },
            value: {
              doc: '{@link google|https://google.com} sometext 123',
              formattedDoc: '<a href="https://google.com" target="_blank" rel="noopener noreferrer">google</a> sometext 123',
            },
          },
        },
      ],
    };

    expect(schemaReducer(someInitialState, updateFieldMetadata(
      'TestPosition',
      'skills',
      '{@link google|https://google.com} sometext 123',
      '<a href="https://google.com" target="_blank" rel="noopener noreferrer">google</a> sometext 123',
    )))
      .toEqual(state);
  });

  it('should update metadata of FIELD if it is already have metadata value', () => {
    const someInitialState = {
      subject: 'rb.test-1',
      version: 1,
      schemas: [
        {
          schemaName: 'TestPosition',
          name: 'skill',
          metadata: {
            key: {
              subject: 'rb.test-1',
              version: 1,
              schemaFullName: 'TestPosition',
              field: 'skill',
              type: 'FIELD',
            },
            value: {
              doc: 'sometext {@link google|https://google.com} sometext 123',
              formattedDoc: 'text <a href="https://google.com" target="_blank" rel="noopener noreferrer">google</a> sometext 123',
              updatedAt: 1543410665529,
              updatedBy: 'raman babich',
            },
          },
        },
      ],
    };

    const state = {
      subject: 'rb.test-1',
      version: 1,
      schemas: [
        {
          schemaName: 'TestPosition',
          name: 'skill',
          metadata: {
            key: {
              subject: 'rb.test-1',
              version: 1,
              schemaFullName: 'TestPosition',
              field: 'skill',
              type: 'FIELD',
            },
            value: {
              doc: 'sometext',
              formattedDoc: 'sometext',
              updatedAt: 1543410665529,
              updatedBy: 'raman babich',
            },
          },
        },
      ],
    };

    expect(schemaReducer(someInitialState, updateFieldMetadata(
      'TestPosition',
      'skill',
      'sometext',
      'sometext',
      false,
    )))
      .toEqual(state);
  });

  it('should update metadata of SCHEMA if it is null', () => {
    const someInitialState = {
      subject: 'rb.test-1',
      version: 1,
      schemaMetadata: null,
    };
    const state = {
      subject: 'rb.test-1',
      version: 1,
      schemaMetadata: {
        key: {
          subject: 'rb.test-1',
          type: 'SCHEMA',
          version: 1,
        },
        value: {
          doc: 'sometext',
          formattedDoc: 'sometext',
        },
      },
    };
    expect(schemaReducer(someInitialState, updateSchemaMetadata(
      'sometext',
      'sometext',
      false,
    )))
      .toEqual(state);
  });
  it('should update metadata of SCHEMA if it is not null', () => {
    const someInitialState = {
      subject: 'rb.test-1',
      version: 1,
      schemaMetadata: {
        key: {
          subject: 'rb.test-1',
          type: 'SCHEMA',
          version: 1,
        },
        value: {
          doc: 'sometext',
          formattedDoc: 'sometext',
          someProp: '123',
        },
      },
    };
    const state = {
      subject: 'rb.test-1',
      version: 1,
      schemaMetadata: {
        key: {
          subject: 'rb.test-1',
          type: 'SCHEMA',
          version: 1,
        },
        value: {
          doc: 'sometext123',
          formattedDoc: 'sometext123',
          someProp: '123',
        },
      },
    };
    expect(schemaReducer(someInitialState, updateSchemaMetadata(
      'sometext123',
      'sometext123',
      false,
    )))
      .toEqual(state);
  });
});
