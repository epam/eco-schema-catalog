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
import parseSearchString from './parseSearchString';

describe('parseSearchString spec', () => {
  it('should return proper 3 params from search', () => {
    expect(parseSearchString('?query=*&page=1&pageSize=20')).toEqual({ query: '*', page: 0, pageSize: 20 });
    expect(parseSearchString('?query=*ysd&page=1&pageSize=20')).toEqual({ query: '*ysd', page: 0, pageSize: 20 });
    expect(parseSearchString('?query=*ysd&page=0&pageSize=20')).toEqual({ query: '*ysd', page: 0, pageSize: 20 });
    expect(parseSearchString('?query=&page=1&pageSize=20')).toEqual({ query: '*', page: 0, pageSize: 20 });
    expect(parseSearchString('?query= &page=1&pageSize=20')).toEqual({ query: ' ', page: 0, pageSize: 20 });
    expect(parseSearchString('?query=-1&page=1&pageSize=20')).toEqual({ query: '-1', page: 0, pageSize: 20 });
    expect(parseSearchString('?query=*&page=-12&pageSize=20')).toEqual({ query: '*', page: 0, pageSize: 20 });
    expect(parseSearchString('?query=*&page=12&pageSize=20')).toEqual({ query: '*', page: 11, pageSize: 20 });
    expect(parseSearchString('?query=*&page=2d&pageSize=20')).toEqual({ query: '*', page: 1, pageSize: 20 });
    expect(parseSearchString('?query=*&page= &pageSize=20')).toEqual({ query: '*', page: 0, pageSize: 20 });
    expect(parseSearchString('?query=*&page=   &pageSize=20')).toEqual({ query: '*', page: 0, pageSize: 20 });
    expect(parseSearchString('?query=*&page=2&pageSize=0')).toEqual({ query: '*', page: 1, pageSize: 20 });
    expect(parseSearchString('?query=*&page=2&pageSize=-23')).toEqual({ query: '*', page: 1, pageSize: 20 });
    expect(parseSearchString('?query=*&page=2&pageSize=-23sdf')).toEqual({ query: '*', page: 1, pageSize: 20 });
    expect(parseSearchString('?query=*&page=2&pageSize=sdf')).toEqual({ query: '*', page: 1, pageSize: 20 });
    expect(parseSearchString('?query=*&page=2&pageSize=')).toEqual({ query: '*', page: 1, pageSize: 20 });

    expect(parseSearchString('')).toEqual({ query: '*', page: 0, pageSize: 20 });
    expect(parseSearchString('?query=*')).toEqual({ query: '*', page: 0, pageSize: 20 });
    expect(parseSearchString('?page=2')).toEqual({ query: '*', page: 1, pageSize: 20 });

    expect(parseSearchString('?pageSize=11')).toEqual({ query: '*', page: 0, pageSize: 11 });

    expect(parseSearchString(123)).toEqual({ query: '*', page: 0, pageSize: 20 });

    expect(parseSearchString('?query=*&page=1&pageSize=20&fake=123')).toEqual({ query: '*', page: 0, pageSize: 20 });
    expect(parseSearchString('?fake=123')).toEqual({ query: '*', page: 0, pageSize: 20 });
  });

  it('should return proper 3 params and with aggregations from search', () => {
    expect(parseSearchString('?query=*&page=1&pageSize=20&compatibilityTerm=FULL,NONE'))
      .toEqual({
        query: '*', page: 0, pageSize: 20, compatibilityTerm: ['FULL', 'NONE'],
      });
    expect(parseSearchString('?query=*&page=1&pageSize=20&compatibilityTerm=FULL'))
      .toEqual({
        query: '*', page: 0, pageSize: 20, compatibilityTerm: ['FULL'],
      });

    expect(parseSearchString('?query=*&page=1&pageSize=20&compatibilityTerm=FULL,NONE&metadataUpdatedByTerm=valiantsin%20dzemiashkevich'))
      .toEqual({
        query: '*', page: 0, pageSize: 20, compatibilityTerm: ['FULL', 'NONE'], metadataUpdatedByTerm: ['valiantsin dzemiashkevich'],
      });
    expect(parseSearchString('?query=*&page=1&pageSize=20&compatibilityTerm=NONE&namespaceTerm=AQA,AUD_PMC_SYSTEM,AUD_PMC_UPSA,MSSQLAQA'))
      .toEqual({
        query: '*', page: 0, pageSize: 20, compatibilityTerm: ['NONE'], namespaceTerm: ['AQA', 'AUD_PMC_SYSTEM', 'AUD_PMC_UPSA', 'MSSQLAQA'],
      });

    expect(parseSearchString('?query=*&page=1&pageSize=20&comerm=NONE&nameTerm=AQA,AUD_PMC_SYSTEM,AUD_PMC_UPSA,MSSQLAQA'))
      .toEqual({
        query: '*', page: 0, pageSize: 20,
      });
  });
});
