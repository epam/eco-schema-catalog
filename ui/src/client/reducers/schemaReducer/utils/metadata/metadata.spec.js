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
import { createMetadata, assingMetadataDocs } from './metadata';
import { FIELD, SCHEMA } from '../../../../consts/consts';

describe('createMetadata spec', () => {
  it('createMetadata for the schema', () => {
    const version = 1;
    const schemaName = 'TestPosition';
    const doc = '123';
    const name = 'start_date';
    const subject = 'rb.test-1';
    const formattedDoc = '123';
    expect(createMetadata(
      FIELD,
      subject,
      version,
      doc,
      formattedDoc,
      schemaName,
      name,
    )).toEqual({
      key: {
        subject,
        field: name,
        schemaFullName: schemaName,
        version,
        type: FIELD,
      },
      value: {
        doc,
        formattedDoc,
      },
    });
    expect(createMetadata(
      SCHEMA,
      subject,
      version,
      doc,
      formattedDoc,
    )).toEqual({
      key: {
        subject,
        version,
        type: SCHEMA,
      },
      value: {
        doc,
        formattedDoc,
      },
    });
  });
});

describe('assingMetadataDocs spec', () => {
  it('assing schema metadata object', () => {
    const schemaMetadata = {
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
    };
    const fieldMetadata = {
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
    };
    expect(assingMetadataDocs(schemaMetadata, '123', '123')).toEqual({
      key: {
        subject: 'rb.test-1',
        type: 'SCHEMA',
        version: 1,
      },
      value: {
        attributes: {
          c: 'c',
        },
        doc: '123',
        formattedDoc: '123',
        updatedAt: 1543410665527,
        updatedBy: 'raman babich',
      },
    });
    expect(assingMetadataDocs(fieldMetadata, '321', '321')).toEqual({
      key: {
        subject: 'rb.test-1',
        version: 1,
        schemaFullName: 'TestPosition',
        field: 'skill',
        type: 'FIELD',
      },
      value: {
        doc: '321',
        formattedDoc: '321',
        updatedAt: 1543410665529,
        updatedBy: 'raman babich',
      },
    });
  });

  it('assing schema metadata object should not mutate first argument', () => {
    const schemaMetadata = {
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
    };
    const newMetadata = assingMetadataDocs(schemaMetadata, '123', '123');
    expect(schemaMetadata).toEqual(schemaMetadata);
    expect(newMetadata).not.toEqual(schemaMetadata);
  });
});
