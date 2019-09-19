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
import sortSchemasByRoot from './sortSchemasByRoot';

describe('sortSchemasByRoot spec', () => {
  it('should Sort schmas by root field', () => {
    const schemas = [
      {
        name: 'TestHobby',
        root: false,
      },
      {
        name: 'TestSkillLevel',
        root: false,
      },
      {
        name: 'TestJob',
        root: true,
      },
      {
        name: 'TestPerson',
        root: false,
      },
      {
        name: 'TestPosition',
        root: false,
      },
    ];
    const schemasSorted = [
      {
        name: 'TestJob',
        root: true,
      },
      {
        name: 'TestHobby',
        root: false,
      },
      {
        name: 'TestSkillLevel',
        root: false,
      },
      {
        name: 'TestPerson',
        root: false,
      },
      {
        name: 'TestPosition',
        root: false,
      },
    ];

    const result = schemas.sort(sortSchemasByRoot);
    expect(result).toHaveLength(5);
    expect(result).toEqual(schemasSorted);
  });
});
