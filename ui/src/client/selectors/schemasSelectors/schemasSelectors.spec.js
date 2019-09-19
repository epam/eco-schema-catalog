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
  getLimitedTotalElements,
  getLimitedTotalPages,
  getIsExeedingMaxResult,
} from './schemasSelectors';

describe('selectors spec', () => {
  it('default state', () => {
    const someState = {
      schemasReducer: {
        pageSize: 20,
        totalPages: 0,
        totalElements: 0,
        maxResultWindow: 0,
      },
    };
    expect(getLimitedTotalElements(someState)).toBe(0);
    expect(getLimitedTotalPages(someState)).toBe(0);
    expect(getIsExeedingMaxResult(someState)).toBeFalsy();
  });

  it('total elements does not exeed maxResultWindow', () => {
    const someState = {
      schemasReducer: {
        pageSize: 20,
        totalPages: 11,
        totalElements: 212,
        maxResultWindow: 15000,
      },
    };
    expect(getLimitedTotalElements(someState)).toBe(212);
    expect(getLimitedTotalPages(someState)).toBe(11);
    expect(getIsExeedingMaxResult(someState)).toBeFalsy();
  });

  it('total elements exeeds maxResultWindow', () => {
    const someState = {
      schemasReducer: {
        pageSize: 20,
        totalPages: 830,
        totalElements: 16596,
        maxResultWindow: 15000,
      },
    };
    expect(getLimitedTotalElements(someState)).toBe(15000);
    expect(getLimitedTotalPages(someState)).toBe(750);
    expect(getIsExeedingMaxResult(someState)).toBeTruthy();
  });

  it('total elements exeeds maxResultWindow and different maxResultWindow value', () => {
    const someState = {
      schemasReducer: {
        pageSize: 20,
        totalPages: 830,
        totalElements: 16596,
        maxResultWindow: 1000,
      },
    };
    expect(getLimitedTotalElements(someState)).toBe(1000);
    expect(getLimitedTotalPages(someState)).toBe(50);
    expect(getIsExeedingMaxResult(someState)).toBeTruthy();
  });

  it('total elements exeeds maxResultWindow pageSize=10', () => {
    const someState = {
      schemasReducer: {
        pageSize: 10,
        totalPages: 1660,
        totalElements: 16596,
        maxResultWindow: 1000,
      },
    };
    expect(getLimitedTotalElements(someState)).toBe(1000);
    expect(getLimitedTotalPages(someState)).toBe(100);
    expect(getIsExeedingMaxResult(someState)).toBeTruthy();
  });
});
