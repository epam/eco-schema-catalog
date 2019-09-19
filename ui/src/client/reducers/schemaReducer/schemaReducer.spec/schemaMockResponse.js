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
export const response = {
  deleted: false,
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
      name: 'TestHobby',
      namespace: 'com.epam.eco.schemaregistry.client.avro.data',
      root: false,
      fields: [
        {
          name: 'kind',
          defaultValue: null,
          metadata: null,
          nativeDoc: 'for kind',
          type: {
            fullName: 'string',
            logicalType: null,
            type: 'STRING',
          },
        },
        {
          name: 'description',
          defaultValue: null,
          metadata: null,
          nativeDoc: 'for description',
          type: {
            fullName: 'string',
            logicalType: null,
            type: 'STRING',
          },
        },
      ],
    },
    {
      name: 'TestSkillLevel',
      namespace: 'com.epam.eco.schemaregistry.client.avro.data',
      root: false,
      fields: [
        {
          defaultValue: null,
          metadata: null,
          name: 'level',
          nativeDoc: null,
          type: {
            fullName: 'string',
            logicalType: null,
            type: 'STRING',
          },
        },
      ],
    },
    {
      name: 'TestJob',
      namespace: 'com.epam.eco.schemaregistry.client.avro.data',
      root: true,
      fields: [
        {
          defaultValue: null,
          metadata: null,
          name: 'position',
          nativeDoc: null,
          type: {
            fullName: 'TestPosition',
            logicalType: null,
            name: 'TestPosition',
            namespace: null,
            type: 'RECORD',
          },
        },
        {
          defaultValue: null,
          metadata: null,
          name: 'previousJob',
          nativeDoc: null,
          type: {
            fullName: 'union<null, TestJob>',
            logicalType: null,
            parameters: [],
            type: 'UNION',
          },
        },
      ],
    },
    {
      name: 'TestPerson',
      namespace: 'com.epam.eco.schemaregistry.client.avro.data',
      root: false,
      fields: [
        {
          defaultValue: null,
          metadata: null,
          name: 'age',
          nativeDoc: null,
          type: {
            fullName: 'union<null, int>',
            logicalType: null,
            type: 'UNION',
          },
        },
        {
          defaultValue: null,
          metadata: null,
          name: 'hobby',
          nativeDoc: null,
          type: {
            fullName: 'union<null, array<TestHobby>>',
            logicalType: null,
            type: 'UNION',
          },
        },
        {
          defaultValue: null,
          metadata: null,
          name: 'job',
          nativeDoc: null,
          type: {
            fullName: 'TestJob',
            logicalType: null,
            name: 'TestJob',
            namespace: null,
            type: 'RECORD',
          },
        },
      ],
    },
    {
      name: 'TestPosition',
      namespace: 'com.epam.eco.schemaregistry.client.avro.data',
      root: false,
      fields: [
        {
          defaultValue: null,
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
          name: 'skill',
          nativeDoc: null,
          type: {
            fullName: 'map<string, TestSkillLevel>',
            logicalType: null,
            type: 'MAP',
          },
        },

        {
          defaultValue: null,
          metadata: {
            key: {
              subject: 'rb.test-1',
              version: 1,
              schemaFullName: 'TestPosition',
              field: 'start_date',
              type: 'FIELD',
            },
            value: {
              doc: 'sometext {@link google|https://google.com} sometext 321',
              formattedDoc: 'text <a href="https://google.com" target="_blank" rel="noopener noreferrer">google</a> sometext 321',
              updatedAt: 1543410665529,
              updatedBy: 'raman babich',
            },
          },
          name: 'start_date',
          nativeDoc: null,
          type: {
            fullName: 'union<null, long(timestamp-millis)>',
            logicalType: null,
            parameters: [
              {
                fullName: 'null',
                logicalType: null,
                type: 'NULL',
              },
              {
                fullName: 'long(timestamp-millis)',
                logicalType: 'timestamp-millis',
                type: 'LONG',
              },
            ],
            type: 'UNION',
          },
        },

        {
          defaultValue: null,
          metadata: null,
          name: 'skills',
          nativeDoc: null,
          type: {
            fullName: 'union<null, array<SkillRef>>',
            logicalType: null,
            parameters: [
              {
                fullName: 'null',
                logicalType: null,
                type: 'NULL',
              },
              {
                fullName: 'array<SkillRef>',
                logicalType: null,
                parameters: [
                  {
                    fullName: 'SkillRef',
                    logicalType: null,
                    name: 'SkillRef',
                    namespace: null,
                    type: 'RECORD',
                  },
                ],
                type: 'ARRAY',
              },
            ],
            type: 'UNION',
          },
        },
      ],
    },
  ],
};


export const flattenSchemas = [
  {
    schemaName: 'TestJob',
    namespace: 'com.epam.eco.schemaregistry.client.avro.data',
    defaultValue: null,
    metadata: null,
    name: 'position',
    nativeDoc: null,
    fullName: 'TestPosition',
    type: ['RECORD'],
    logicalType: [],
    isSaved: true,
  },
  {
    schemaName: 'TestJob',
    namespace: 'com.epam.eco.schemaregistry.client.avro.data',
    defaultValue: null,
    metadata: null,
    name: 'previousJob',
    nativeDoc: null,
    fullName: 'union<null, TestJob>',
    type: ['UNION'],
    logicalType: [],
    isSaved: true,
  },
  {
    schemaName: 'TestHobby',
    namespace: 'com.epam.eco.schemaregistry.client.avro.data',
    name: 'kind',
    defaultValue: null,
    metadata: null,
    nativeDoc: 'for kind',
    fullName: 'string',
    type: ['STRING'],
    logicalType: [],
    isSaved: true,
  },
  {
    schemaName: 'TestHobby',
    namespace: 'com.epam.eco.schemaregistry.client.avro.data',
    name: 'description',
    defaultValue: null,
    metadata: null,
    nativeDoc: 'for description',
    fullName: 'string',
    type: ['STRING'],
    logicalType: [],
    isSaved: true,
  },
  {
    schemaName: 'TestSkillLevel',
    namespace: 'com.epam.eco.schemaregistry.client.avro.data',
    defaultValue: null,
    metadata: null,
    name: 'level',
    nativeDoc: null,
    fullName: 'string',
    type: ['STRING'],
    logicalType: [],
    isSaved: true,
  },
  {
    schemaName: 'TestPerson',
    namespace: 'com.epam.eco.schemaregistry.client.avro.data',
    defaultValue: null,
    metadata: null,
    name: 'age',
    nativeDoc: null,
    fullName: 'union<null, int>',
    type: ['UNION'],
    logicalType: [],
    isSaved: true,
  },
  {
    schemaName: 'TestPerson',
    namespace: 'com.epam.eco.schemaregistry.client.avro.data',
    defaultValue: null,
    metadata: null,
    name: 'hobby',
    nativeDoc: null,
    fullName: 'union<null, array<TestHobby>>',
    type: ['UNION'],
    logicalType: [],
    isSaved: true,
  },
  {
    schemaName: 'TestPerson',
    namespace: 'com.epam.eco.schemaregistry.client.avro.data',
    defaultValue: null,
    metadata: null,
    name: 'job',
    nativeDoc: null,
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
        doc: 'sometext {@link google|https://google.com} sometext 123',
        formattedDoc: 'text <a href="https://google.com" target="_blank" rel="noopener noreferrer">google</a> sometext 123',
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
        doc: 'sometext {@link google|https://google.com} sometext 321',
        formattedDoc: 'text <a href="https://google.com" target="_blank" rel="noopener noreferrer">google</a> sometext 321',
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
    metadata: null,
    name: 'skills',
    nativeDoc: null,
    fullName: 'union<null, array<SkillRef>>',
    type: ['UNION', 'NULL', 'ARRAY', 'RECORD'],
    logicalType: [],
    isSaved: true,
  },
];
