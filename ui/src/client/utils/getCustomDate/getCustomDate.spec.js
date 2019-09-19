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
import getCustomFormatDate from './getCustomDate';

describe('get date in custom format (according to the local time) from UTC date string', () => {
  it('2018-09-03T12:08:56.381Z is equal to \'2018/09/03, 15:08:56\'', () => {
    expect(getCustomFormatDate('2018-09-03T12:08:56.381Z')).toEqual('2018/09/03, 15:08:56');
  });
  it('2018-09-03T12:08:56 is equal to \'2018/09/03, 15:08:56\'', () => {
    expect(getCustomFormatDate('2018-09-03T12:08:56.000Z')).toEqual('2018/09/03, 15:08:56');
  });
  it('2018-12-03T12:08:56 is equal to \'2018/12/03, 15:08:56\'', () => {
    expect(getCustomFormatDate('2018-12-03T12:08:56.000Z')).toEqual('2018/12/03, 15:08:56');
  });
  it('2018-01-01T18:08:56 is equal to \'2018/01/01, 21:08:56\'', () => {
    expect(getCustomFormatDate('2018-01-01T18:08:56.000Z')).toEqual('2018/01/01, 21:08:56');
  });
});
