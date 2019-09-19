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
import axios from 'axios';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import { getSchemasAsync, syncSearchWithHistory } from './schemasActions';
import {
  GOT_SCHEMAS,
  CHANGE_QUERY,
  GOT_SCHEMAS_AGGREGATIONS,
  CALL_HISTORY_METHOD,
} from '../../consts/consts';

jest.mock('axios');
const middlewares = [thunk];

describe('syncSearchWithHistory spec', () => {
  const mockStore = configureMockStore(middlewares);
  const initialState = {
    schemasReducer: {
      query: '*',
      schemas: [],
      queryExamples: {},
      page: 0,
      pageSize: 20,
      totalPages: 0,
      totalElements: 0,
      first: true,
      last: false,
      maxResultWindow: 0,
      isLoading: false,
    },
    aggregationsSchemasReducer: {
      compatibilityTerm: [],
      metadataUpdatedByTerm: [],
      namespaceTerm: [],
      versionTerm: [],
      deletedTerm: [],
      versionLatestTerm: [],
    },
  };
  const store = mockStore(initialState);

  it('sync default params', () => {
    const params = { query: '*12', page: 1, pageSize: 20 };
    const expectedActions = [
      {
        type: CALL_HISTORY_METHOD,
        payload: {
          args: [
            {
              pathname: '/',
              search: 'query=*12&page=2&pageSize=20',
            },
          ],
          method: 'replace',
        },
      },
    ];

    store.dispatch(syncSearchWithHistory(params));
    expect(store.getActions()).toEqual(expectedActions);
    store.clearActions();
  });

  it('sync default params and terms', () => {
    const params = {
      query: '*',
      page: 0,
      pageSize: 20,
      compatibilityTerm: 'NONE',
      namespaceTerm: 'AQA,AQA1,AQA2',
      metadataUpdatedByTerm: 'vasya pupkin,petya jopkin',
    };
    const expectedActions = [
      {
        type: CALL_HISTORY_METHOD,
        payload: {
          args: [
            {
              pathname: '/',
              search: 'query=*&page=1&pageSize=20&compatibilityTerm=NONE&metadataUpdatedByTerm=vasya pupkin,petya jopkin&namespaceTerm=AQA,AQA1,AQA2',
            },
          ],
          method: 'replace',
        },
      },
    ];
    store.dispatch(syncSearchWithHistory(params));
    expect(store.getActions()).toEqual(expectedActions);
    store.clearActions();
  });
});

describe('get schemas async', () => {
  const mockStore = configureMockStore(middlewares);
  const initialState = {
    schemasReducer: {
      query: '*',
      search: '',
      schemas: [],
      queryExamples: {},
      page: 0,
      pageSize: 20,
      totalPages: 0,
      totalElements: 0,
      maxResultWindow: 0,
      first: true,
      last: false,
      isLoading: false,
    },
    aggregationsSchemasReducer: {
      compatibilityTerm: [],
      metadataUpdatedByTerm: [],
      namespaceTerm: [],
      versionTerm: [],
      deletedTerm: [],
      versionLatestTerm: [],
    },
  };
  const store = mockStore(initialState);

  it('default params', () => {
    const schemasResponse = {
      aggregations: {},
      content: [],
      firstPage: true,
      lastPage: false,
      hasNextPage: true,
      hasPreviousPage: false,
      maxResultWindow: 15000,
      pageNumber: 0,
      pageSize: 20,
      totalElements: 16902,
      totalPages: 846,
    };
    axios.get.mockImplementation(() => Promise.resolve({ data: schemasResponse }));
    const expectedActions = [
      { type: 'SET_IS_LOADING_SCHEMAS', isLoading: true },
      {
        type: CALL_HISTORY_METHOD,
        payload: {
          args: [
            {
              pathname: '/',
              search: 'query=*&page=1&pageSize=20',
            },
          ],
          method: 'replace',
        },
      },
      {
        type: GOT_SCHEMAS,
        queryResult: {
          schemas: [],
          first: true,
          last: false,
          page: 0,
          pageSize: 20,
          totalElements: 16902,
          totalPages: 846,
          maxResultWindow: 15000,
        },
      },
      { type: GOT_SCHEMAS_AGGREGATIONS, aggregations: {} },
      { type: CHANGE_QUERY, query: '*' },
      { type: 'SET_IS_LOADING_SCHEMAS', isLoading: false },
    ];
    store.dispatch(getSchemasAsync())
      .then(() => {
        expect(store.getActions()).toEqual(expectedActions);
        store.clearActions();
      });
  });


  it('custom params on mount', () => {
    const schemasResponse = {
      aggregations: {},
      content: [],
      firstPage: true,
      lastPage: false,
      hasNextPage: true,
      hasPreviousPage: false,
      pageNumber: 3,
      pageSize: 10,
      totalElements: 16902,
      totalPages: 846,
      maxResultWindow: 15000,
    };
    axios.get.mockImplementation(() => Promise.resolve({ data: schemasResponse }));
    const expectedActions = [
      { type: 'SET_IS_LOADING_SCHEMAS', isLoading: true },
      {
        type: CALL_HISTORY_METHOD,
        payload: {
          args: [
            {
              pathname: '/',
              search: 'query=*ysd&page=4&pageSize=10',
            },
          ],
          method: 'replace',
        },
      },
      {
        type: GOT_SCHEMAS,
        queryResult: {
          schemas: [],
          first: true,
          last: false,
          page: 3,
          pageSize: 10,
          totalElements: 16902,
          totalPages: 846,
          maxResultWindow: 15000,
        },
      },
      { type: GOT_SCHEMAS_AGGREGATIONS, aggregations: {} },
      { type: CHANGE_QUERY, query: '*ysd' },
      { type: 'SET_IS_LOADING_SCHEMAS', isLoading: false },
    ];

    store.dispatch(getSchemasAsync({ query: '*ysd', page: 3, pageSize: 10 }))
      .then(() => {
        expect(store.getActions()).toEqual(expectedActions);
        store.clearActions();
      });
  });
});

describe('get schemas async / with agregations', () => {
  const mockStore = configureMockStore(middlewares);
  const initialState = {
    schemasReducer: {
      query: '*',
      schemas: [],
      queryExamples: {},
      page: 0,
      pageSize: 20,
      totalPages: 0,
      totalElements: 0,
      maxResultWindow: 0,
      first: true,
      last: false,
      isLoading: false,
    },
    aggregationsSchemasReducer: {
      compatibilityTerm: ['FULL', 'BACKWARD'],
      metadataUpdatedByTerm: ['john doe'],
      namespaceTerm: [],
      versionTerm: [],
      deletedTerm: [],
      versionLatestTerm: [],
    },
  };
  const store = mockStore(initialState);

  it('default params with some agregations', () => {
    const schemasResponse = {
      aggregations: {},
      content: [],
      firstPage: true,
      lastPage: false,
      hasNextPage: true,
      hasPreviousPage: false,
      maxResultWindow: 15000,
      pageNumber: 0,
      pageSize: 20,
      totalElements: 16902,
      totalPages: 846,
    };
    axios.get.mockImplementation(() => Promise.resolve({ data: schemasResponse }));
    const expectedActions = [
      { type: 'SET_IS_LOADING_SCHEMAS', isLoading: true },
      {
        type: CALL_HISTORY_METHOD,
        payload: {
          args: [
            {
              pathname: '/',
              search: 'query=123&page=1&pageSize=20&compatibilityTerm=FULL,BACKWARD&metadataUpdatedByTerm=john doe',
            },
          ],
          method: 'replace',
        },
      },
      {
        type: GOT_SCHEMAS,
        queryResult: {
          schemas: [],
          first: true,
          last: false,
          page: 0,
          pageSize: 20,
          totalElements: 16902,
          maxResultWindow: 15000,
          totalPages: 846,
        },
      },
      { type: GOT_SCHEMAS_AGGREGATIONS, aggregations: {} },
      { type: CHANGE_QUERY, query: '123' },
      { type: 'SET_IS_LOADING_SCHEMAS', isLoading: false },
    ];
    store.dispatch(getSchemasAsync({ query: '123' }))
      .then(() => {
        expect(store.getActions()).toEqual(expectedActions);
        store.clearActions();
      });
  });
});
