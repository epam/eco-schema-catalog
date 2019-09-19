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
import applyQuerySettings from './applyQuerySettings';

describe('applyQuerySettings spec', () => {
  const querySettingsMap = {
    with3rdPartySchemas: 'AND namespace:com.mynamespace.*',
    withLatestVersions: 'AND versionLatest:true',
    withDeletedSchemas: 'AND deleted:false',
  };
  it('no settings, only map', () => {
    const querySettings = [];

    expect(applyQuerySettings(querySettings, querySettingsMap)).toEqual([
      'AND namespace:com.mynamespace.*',
      'AND versionLatest:true',
      'AND deleted:false',
    ]);
  });

  it('one settings', () => {
    const querySettings = ['with3rdPartySchemas'];

    expect(applyQuerySettings(querySettings, querySettingsMap)).toEqual([
      'AND versionLatest:true',
      'AND deleted:false',
    ]);
  });

  it('all settings', () => {
    const querySettings = ['with3rdPartySchemas', 'withLatestVersions', 'withDeletedSchemas'];

    expect(applyQuerySettings(querySettings, querySettingsMap)).toEqual([]);
  });

  it('non valid settings', () => {
    const querySettings = ['with3rdPartySchemas', 'opop', 'che'];

    expect(applyQuerySettings(querySettings, querySettingsMap)).toEqual([
      'AND versionLatest:true',
      'AND deleted:false',
    ]);
  });
});
