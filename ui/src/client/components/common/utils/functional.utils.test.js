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
import {
  noop,
  identity,
  pipe,
  assert,
} from './functional';

describe('functional utils spec', () => {
  describe('noop', () => {
    it('should return undefind', () => {
      expect(noop()).toBeUndefined();
    });
  });

  describe('identity', () => {
    it('should return provided argument', () => {
      expect(identity()).toBeUndefined();
      expect(identity(12)).toBe(12);

      const a = { a: 12 };
      expect(identity(a)).toBe(a);
    });
  });

  describe('pipe', () => {
    it('should return identity if no funcs provided', () => {
      const newidentity = pipe();
      expect(newidentity).toBeInstanceOf(Function);
      expect(newidentity(12)).toBe(12);
    });

    it('should return provided func, if there are only one func provided', () => {
      const func = val => val + 12;
      const f = pipe(func);

      expect(f).toBe(func);
      expect(f(12)).toBe(func(12));
    });

    it('should create flow of function invoke', () => {
      const addA = val => obj => ({ ...obj, a: val });
      const addB = val => obj => ({ ...obj, b: val });
      const addC = val => obj => ({ ...obj, c: val });

      const addMultipleValues = pipe(addA(1), addB(2), addC(3));

      expect(addMultipleValues({})).toEqual({ a: 1, b: 2, c: 3 });
    });
  });

  describe('assert', () => {
    it('should throw error if condition is false', () => {
      const f = () => assert(false, 'NO');

      expect(f).toThrowError('Assertion failed: NO');
    });

    it('should return condition if condition is truthy', () => {
      expect(assert(true, 'NO')).toBe(true);
    });
  });
});
