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
/* eslint-disable quote-props */
/* eslint-disable quotes */
/* eslint-disable no-undef */
import combineMetadata from './combineMetadata';

describe('combineMetadata spec', () => {
  it('should return combined metadata', () => {
    const reducer = {
      isSchemaMetadataSaved: false,
      schemaMetadata: {
        key: {
          subject: 'rb.test-1',
          type: 'SCHEMA',
          version: 1,
        },
        value: {
          attributes: {
            c: 'c',
          },
          doc: 'text {@link google|https://google.com} text',
          formattedDoc: 'text <a href="https://google.com" target="_blank" rel="noopener noreferrer">google</a> text',
          updatedAt: 1543410665527,
          updatedBy: 'raman babich',
        },
      },
      schemas: [
        {
          schemaName: 'TestPerson',
          namespace: 'com.epam.eco.schemaregistry.client.avro.data',
          defaultValue: null,
          metadata: null,
          name: 'job',
          nativeDoc: null,
          isFirstSubRow: false,
          fullName: 'TestJob',
          type: ['RECORD'],
          logicalType: [],
          isSaved: true,
        },
        {
          schemaName: 'TestPosition',
          namespace: 'com.epam.eco.schemaregistry.client.avro.data',
          defaultValue: null,
          name: 'skill',
          nativeDoc: null,
          isFirstSubRow: true,
          fullName: 'map<string, TestSkillLevel>',
          type: ['MAP'],
          logicalType: [],
          metadata: {
            key: {
              subject: 'rb.test-1',
              version: 1,
              schemaFullName: 'TestPosition',
              field: 'skill',
              type: 'FIELD',
            },
            value: {
              doc: 'sometext 123',
              formattedDoc: 'sometext 123',
              updatedAt: 1543410665529,
              updatedBy: 'raman babich',
            },
          },
          isSaved: true,
        },
        {
          schemaName: 'TestPosition',
          namespace: 'com.epam.eco.schemaregistry.client.avro.data',
          defaultValue: null,
          name: 'start_date',
          nativeDoc: null,
          isFirstSubRow: false,
          fullName: 'union<null, long(timestamp-millis)>',
          type: ['UNION', 'NULL', 'LONG'],
          logicalType: ['timestamp-millis'],
          metadata: {
            key: {
              subject: 'rb.test-1',
              version: 1,
              schemaFullName: 'TestPosition',
              field: 'start_date',
              type: 'FIELD',
            },
            value: {
              doc: 'sometext 321',
              formattedDoc: 'sometext 321',
              updatedAt: 1543410665529,
              updatedBy: 'raman babich',
            },
          },
          isSaved: false,
        },
      ],
    };
    const { schemaMetadata, isSchemaMetadataSaved, schemas } = reducer;

    Object.assign(schemaMetadata, { isSaved: isSchemaMetadataSaved });

    schemas.push({ metadata: schemaMetadata, isSaved: isSchemaMetadataSaved });

    expect(combineMetadata(schemas))
      .toEqual({
        "{\"subject\":\"rb.test-1\",\"type\":\"SCHEMA\",\"version\":1}": { "doc": "text {@link google|https://google.com} text" },
        "{\"subject\":\"rb.test-1\",\"version\":1,\"schemaFullName\":\"TestPosition\",\"field\":\"start_date\",\"type\":\"FIELD\"}": { "doc": "sometext 321" },
      });
  });
});
