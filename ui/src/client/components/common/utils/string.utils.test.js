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
import { constructClassName, skipEmptyClassNames } from './string';

import { EMPTY } from '../consts/core';

describe('string utils spec', () => {
  describe('constructClassName', () => {
    it('should return empty string if no values provided', () => {
      expect(constructClassName()).toBe('');
      expect(constructClassName([])).toBe('');
    });

    it('should return className string, whne classes are provided', () => {
      const expected = 'one two three';
      expect(constructClassName(['one', 'two', 'three'])).toBe(expected);
    });

    it('should create className string, depending on provided config', () => {
      const expected = 'one four';
      expect(constructClassName(['two', 'one', 'three', 'four', 'two'], { break: ['two', 'three'] })).toBe(expected);
    });
  });

  describe('skipEmptyClassNames', () => {
    it('should skip EMPTY classNames', () => {
      const expected = 'one two';

      expect(skipEmptyClassNames([EMPTY, EMPTY, 'one', EMPTY, 'two'])).toBe(expected);
    });
  });
});
