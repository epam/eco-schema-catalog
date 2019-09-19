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
import toggleArrayItem from './toggleArrayItem';

describe('toggleArrayItem spec', () => {
  it('should add item to array if it does not exist within it', () => {
    expect(toggleArrayItem([], 'flf')).toEqual(['flf']);
    expect(toggleArrayItem(['adsf', 'fff'], 'flf')).toEqual(['adsf', 'fff', 'flf']);
    expect(toggleArrayItem(['adsf', 'fff'], 'fff')).toEqual(['adsf']);
    expect(toggleArrayItem(['adsf', 'fff'], 'adsf')).toEqual(['fff']);
  });
  it('should remove item from array if it exists within it', () => {
    expect(toggleArrayItem(['adsf', 'fff'], 'fff')).toEqual(['adsf']);
  });
  it('no second argument', () => {
    expect(toggleArrayItem(['adsf', 'fff'])).toEqual(['adsf', 'fff']);
  });
});
