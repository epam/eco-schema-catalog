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
export const noop = () => {};

export const identity = arg => arg;

export const pipe = (...funcs) => {
  if (!funcs.length) {
    return identity;
  }

  if (funcs.length === 1) {
    return funcs[0];
  }

  return funcs.reduceRight((a, b) => (...args) => a(b(...args)));
};

export const assert = (condition, message) => {
  if (!condition) {
    throw new Error(`Assertion failed: ${message}`);
  }

  return condition;
};
