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

import Label from './Label';

import { DEFAULT_VALUE, DISABLED } from '../../../consts/core';
import { LABEL } from '../../../consts/labels';

describe('Label spec', () => {
  it('renders with default values', () => {
    const wrapper = shallow(<Label />);

    const divs = wrapper.find('div');

    expect(divs).toHaveLength(1);
    expect(divs.children).toHaveLength(1);
    expect(divs.contains(DEFAULT_VALUE)).toBeTruthy();
    expect(divs.hasClass(LABEL)).toBeTruthy();
  });

  it('renders div with provided value', () => {
    const value = 'FIRST';
    const wrapper = shallow(<Label value={value} />);

    expect(wrapper.find('div').contains(value)).toBeTruthy();
  });

  it('sets data-attributes and classNames dependin on provided props', () => {
    const wrapper = shallow(<Label disabled />);

    const divs = wrapper.find('div');
    const props = divs.props();

    expect(divs.hasClass(DISABLED)).toBeTruthy();

    expect(props['data-disabled']).toBeTruthy();
  });

  it('sets classname', () => {
    const myClassName = 'myClassName';
    const myOtherClassName = 'myOtherClassName';
    const className = `${myClassName} ${myOtherClassName}`;

    const wrapper = shallow(<Label className={className} />);

    const divs = wrapper.find('div');

    expect(divs.hasClass(myClassName)).toBeTruthy();
    expect(divs.hasClass(myOtherClassName)).toBeTruthy();
  });

  it('should invoke provided handler', () => {
    const value = 12;
    const handler = jest.fn();

    const wrapper = shallow(<Label value={value} onClick={handler} />);

    wrapper.find('div').simulate('click');
    expect(handler).toHaveBeenCalledTimes(1);
    expect(handler).toHaveBeenCalledWith(value);
  });

  it('should not invoke provided handler if disabled', () => {
    const handler = jest.fn();

    const wrapper = shallow(<Label onClick={handler} disabled />);
    wrapper.find('div').simulate('click');
    expect(handler).not.toHaveBeenCalled();
  });
});
