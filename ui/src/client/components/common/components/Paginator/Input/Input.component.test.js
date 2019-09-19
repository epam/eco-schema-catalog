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
import { shallow, mount } from 'enzyme';

import Input from './Input';

import { DEFAULT_VALUE } from '../../../consts/core';
import { VALID, INVALID, INPUT } from '../../../consts/input';

describe('Input spec', () => {
  it('renders input with default value', () => {
    const wrapper = shallow(<Input />);

    const inputs = wrapper.find('input');
    const props = inputs.props();

    expect(inputs).toHaveLength(1);
    expect(inputs.hasClass(VALID)).toBeTruthy();
    expect(inputs.hasClass(INPUT)).toBeTruthy();
    expect(props['data-valid']).toBeTruthy();
    expect(props.value).toBe(DEFAULT_VALUE);
  });

  it('should call validator, when trigger input change', () => {
    const validator = jest.fn();
    const simulatedValue = 12;
    const wrapper = mount(<Input validator={validator} />);

    const inputs = wrapper.find('input');

    expect(validator).toHaveBeenNthCalledWith(1, DEFAULT_VALUE);

    inputs.simulate('change', { target: { value: simulatedValue } });

    expect(validator).toHaveBeenCalledTimes(2);
    expect(validator).toHaveBeenNthCalledWith(2, simulatedValue);
  });

  it('should call provided handler, if value is not same', () => {
    const onChange = jest.fn();
    const simulatedValue = 12;
    const wrapper = mount(<Input onChange={onChange} />);

    const inputs = wrapper.find('input');

    inputs.simulate('change', { target: { value: simulatedValue } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith(simulatedValue);
  });

  it('should not call provided handler if values are same', () => {
    const onChange = jest.fn();
    const simulatedValue = DEFAULT_VALUE;
    const wrapper = mount(<Input onChange={onChange} />);

    const inputs = wrapper.find('input');

    inputs.simulate('change', { target: { value: simulatedValue } });

    expect(onChange).not.toHaveBeenCalled();
  });

  it('should update ui after setState', () => {
    const simulatedValue = 12;
    const wrapper = mount(<Input validator={val => val < simulatedValue} />);
    const inputs = wrapper.find('input');

    inputs.simulate('change', { target: { value: simulatedValue } });

    expect(inputs.hasClass(INVALID)).toBeTruthy();
    expect(inputs.hasClass(VALID)).toBeFalsy();
  });

  it('should add className', () => {
    const one = 'one';
    const wrapper = mount(<Input className={one} />);

    expect(wrapper.find('input').hasClass(one)).toBeTruthy();
  });
});
