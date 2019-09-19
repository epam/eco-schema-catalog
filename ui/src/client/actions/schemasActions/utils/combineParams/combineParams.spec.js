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
import combineSearchParams from './combineParams';

describe('combineSearchParams spec', () => {
  it('default params', () => {
    const params = {
      query: '*',
      page: 0,
      pageSize: 20,
    };

    expect(combineSearchParams(params)).toEqual({
      query: '*',
      page: 0,
      pageSize: 20,
    });
  });

  it('default params with default agregations', () => {
    const params = {
      query: '*',
      page: 0,
      pageSize: 20,
      compatibilityTerm: [],
      metadataUpdatedByTerm: [],
      namespaceTerm: [],
    };

    expect(combineSearchParams(params)).toEqual({
      query: '*',
      page: 0,
      pageSize: 20,
    });
  });

  it('default params with default set agregations', () => {
    const params = {
      query: '*',
      page: 0,
      pageSize: 20,
      compatibilityTerm: ['FULL', 'BACKWARD'],
      metadataUpdatedByTerm: ['vasya pupkin', 'pety pupkin'],
      namespaceTerm: [],
    };

    expect(combineSearchParams(params)).toEqual({
      query: '*',
      page: 0,
      pageSize: 20,
      compatibilityTerm: 'FULL,BACKWARD',
      metadataUpdatedByTerm: 'vasya pupkin,pety pupkin',
    });
  });

  it('default params with some agregations', () => {
    const params = {
      query: '*',
      page: 0,
      pageSize: 20,
      compatibilityTerm: [],
      metadataUpdatedByTerm: [],
      namespaceTerm: [],
    };

    const newParams = {
      compatibilityTerm: ['FULL', 'BACKWARD'],
      metadataUpdatedByTerm: ['vasya pupkin', 'pety pupkin'],
      namespaceTerm: [],
    };

    expect(combineSearchParams(params, newParams)).toEqual({
      query: '*',
      page: 0,
      pageSize: 20,
      compatibilityTerm: 'FULL,BACKWARD',
      metadataUpdatedByTerm: 'vasya pupkin,pety pupkin',
    });
  });

  it('default params with some agregations and one query settings', () => {
    const params = {
      query: '*',
      page: 0,
      pageSize: 20,
      compatibilityTerm: [],
      metadataUpdatedByTerm: [],
      namespaceTerm: [],
    };

    const newParams = {
      compatibilityTerm: ['FULL', 'BACKWARD'],
      metadataUpdatedByTerm: ['vasya pupkin', 'pety pupkin'],
      namespaceTerm: [],
    };

    const querySettings = ['AND namespace:com.mynamespace.*'];

    expect(combineSearchParams(params, newParams, querySettings)).toEqual({
      query: '* AND namespace:com.mynamespace.*',
      page: 0,
      pageSize: 20,
      compatibilityTerm: 'FULL,BACKWARD',
      metadataUpdatedByTerm: 'vasya pupkin,pety pupkin',
    });
  });

  it('default params with some agregations and some query settings', () => {
    const params = {
      query: '*',
      page: 0,
      pageSize: 20,
      compatibilityTerm: [],
      metadataUpdatedByTerm: [],
      namespaceTerm: [],
      querySettings: [],
    };

    const newParams = {
      compatibilityTerm: ['FULL', 'BACKWARD'],
      metadataUpdatedByTerm: ['vasya pupkin', 'pety pupkin'],
      namespaceTerm: [],
      querySettings: ['with3rdPartySchemas', 'withPreviousVersions'],
    };

    const querySettings = ['AND namespace:com.mynamespace.*', 'AND versionLatest:true', 'AND deleted:false'];

    expect(combineSearchParams(params, newParams, querySettings)).toEqual({
      query: '* AND namespace:com.mynamespace.* AND versionLatest:true AND deleted:false',
      page: 0,
      pageSize: 20,
      compatibilityTerm: 'FULL,BACKWARD',
      metadataUpdatedByTerm: 'vasya pupkin,pety pupkin',
      querySettings: 'with3rdPartySchemas,withPreviousVersions',
    });
  });

  it('some starne params', () => {
    const params = {
      typeIds: ['BOOL', 'STRING'],
      logical_type: ['date', 'time'],
    };
    expect(combineSearchParams(params)).toEqual({
      typeIds: 'BOOL,STRING',
      logical_type: 'date,time',
    });
  });
});
