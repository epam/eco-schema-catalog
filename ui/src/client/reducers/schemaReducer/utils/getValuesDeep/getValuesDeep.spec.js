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
import getValuesDeep from './getValuesDeep';

describe('getValuesDeep spec', () => {
  it('get by type without nested object', () => {
    const object = {
      fullName: 'union<null, array<SkillRef>>',
      logicalType: null,
      type: 'UNION',
    };
    expect(getValuesDeep(object, 'type')).toEqual(['UNION']);
  });

  it('get by type with one nested object in deep', () => {
    const object = {
      fullName: 'union<null, long(timestamp-millis)>',
      logicalType: null,
      parameters: [
        {
          type: 'NULL',
        },
        {
          type: 'LONG',
          parameters: [
            {
              type: 'MAP',
              parameters: [
                {
                  type: 'STRING',
                },
              ],
            },
            {
              type: 'BOOL',
            },
          ],
        },
      ],
      type: 'UNION',
    };
    expect(getValuesDeep(object, 'type')).toEqual(['UNION', 'NULL', 'LONG', 'MAP', 'STRING', 'BOOL']);
  });

  it('get by logicalType without nested object', () => {
    const object = {
      fullName: 'union<null, array<SkillRef>>',
      logicalType: null,
      type: 'UNION',
    };
    expect(getValuesDeep(object, 'logicalType')).toEqual([]);
  });

  it('get by logicalType with nested object', () => {
    const object = {
      fullName: 'union<null, array<SkillRef>>',
      logicalType: null,
      type: 'UNION',
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
    };
    expect(getValuesDeep(object, 'logicalType')).toEqual(['timestamp-millis']);
  });
});
