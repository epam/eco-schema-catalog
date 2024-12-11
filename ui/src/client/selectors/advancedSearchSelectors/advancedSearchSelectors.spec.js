
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
import { getSortedRootNamspace, getIsClearAll } from './advancedSearchSelectors';

describe('getSortedRootNamspace spec', () => {
  it('no namespaces', () => {
    const state = {
      aggregationsSchemasReducer: {
        rootNamespace: {},
      },
    };
    expect(getSortedRootNamspace(state)).toEqual({});
  });
  it('some namespaces', () => {
    const state = {
      aggregationsSchemasReducer: {
        rootNamespace: {
          'ydf.32': 2,
          'datafactory.test': 4,
          'comp.32': 2,
          'datafactory.data': 4,
          'accounting.32': 1,
          'adventure.32': 39,
          'datafactory.asdf': 2,
          'crm.112': 219,
          'datafactory.hty': 8,
        },
      },
    };
    expect(getSortedRootNamspace(state)).toEqual({
      'accounting.32': 1,
      'adventure.32': 39,
      'comp.32': 2,
      'crm.112': 219,
      'ydf.32': 2,
      'datafactory.asdf': 2,
      'datafactory.data': 4,
      'datafactory.hty': 8,
      'datafactory.test': 4,
    });
  });
});


describe('getIsClearAll spec', () => {
  it('have no applied terms', () => {
    const state = {
      aggregationsSchemasReducer: {
        compatibilityTerm: [],
        namespaceTerm: [],
        metadataUpdatedByTerm: [],
        modeTerm: [],
        deletedTerm: [],
        versionTerm: [],
        versionLatestTerm: [],
      },
    };
    expect(getIsClearAll(state)).toBeFalsy();
  });

  it('have some applied terms', () => {
    const state = {
      aggregationsSchemasReducer: {
        compatibilityTerm: ['NONE'],
        namespaceTerm: [],
        metadataUpdatedByTerm: [],
        deletedTerm: [],
        versionTerm: [],
        versionLatestTerm: [],
      },
    };
    expect(getIsClearAll(state)).toBeTruthy();
  });
});
