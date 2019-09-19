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
import React from 'react';
import { shallow } from 'enzyme';

import Delimeter from './Delimeter';

import { DELIMETER, DEFAULT_DELIMETER } from '../../../consts/delimeter';

describe('Delimeter spec', () => {
  it('renders with default values', () => {
    const wrapper = shallow(<Delimeter />);

    const span = wrapper.find('span');

    expect(span).toHaveLength(1);
    expect(span.hasClass(DELIMETER)).toBeTruthy();
    expect(span.contains(DEFAULT_DELIMETER)).toBeTruthy();
  });

  it('should set className', () => {
    const two = 'two';
    const wrapper = shallow(<Delimeter className={two} />);

    expect(wrapper.find('span').hasClass(two)).toBeTruthy();
  });

  it('should set value', () => {
    const value = '______';
    const wrapper = shallow(<Delimeter value={value} />);

    expect(wrapper.find('span').contains(value)).toBeTruthy();
  });
});
