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

import Picker from './Picker';

import { DEFAULT_VALUE, PICKED, DISABLED } from '../../../consts/core';
import { PICKER } from '../../../consts/picker';

describe('Picker spec', () => {
  it('renders div with default value', () => {
    const wrapper = shallow(<Picker />);

    const divs = wrapper.find('div');

    expect(divs).toHaveLength(1);
    expect(divs.children).toHaveLength(1);
    expect(divs.contains(DEFAULT_VALUE)).toBeTruthy();
    expect(divs.hasClass(PICKER)).toBeTruthy();
  });

  it('renders div with provided value', () => {
    const value = 'First';
    const wrapper = shallow(<Picker value={value} />);

    expect(wrapper.find('div').contains(value)).toBeTruthy();
  });

  it('sets data-attributes and classNames dependin on provided props', () => {
    const wrapper = shallow(<Picker disabled picked />);

    const divs = wrapper.find('div');
    const props = divs.props();

    expect(divs.hasClass(DISABLED)).toBeTruthy();
    expect(divs.hasClass(PICKED)).toBeTruthy();

    expect(props['data-picked']).toBeTruthy();
    expect(props['data-disabled']).toBeTruthy();
  });

  it('sets classname', () => {
    const myClassName = 'myClassName';
    const myOtherClassName = 'myOtherClassName';
    const className = `${myClassName} ${myOtherClassName}`;

    const wrapper = shallow(<Picker className={className} />);

    const divs = wrapper.find('div');

    expect(divs.hasClass(myClassName)).toBeTruthy();
    expect(divs.hasClass(myOtherClassName)).toBeTruthy();
  });

  it('should invoke provided handler', () => {
    const value = 12;
    const onClick = jest.fn();

    const wrapper = shallow(<Picker value={value} onClick={onClick} />);

    wrapper.find('div').simulate('click');
    expect(onClick).toHaveBeenCalledTimes(1);
    expect(onClick).toHaveBeenCalledWith(value);
  });

  it('should not invoke provided handler if disabled', () => {
    const onClick = jest.fn();

    const wrapper = shallow(<Picker onClick={onClick} disabled />);
    wrapper.find('div').simulate('click');
    expect(onClick).not.toHaveBeenCalled();
  });
});
