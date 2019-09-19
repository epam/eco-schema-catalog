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

import Input from './Input/Input';
import Control from './Control/Control';
import Label from './Label/Label';
import Picker from './Picker/Picker';
import Delimeter from './Delimeter/Delimeter';
import Paginator from './Paginator';

import { PAGINATOR, DEFAULT_AMOUNT_PICKERS_TO_SHOW } from '../../consts/paginator';

const getCustomComponent = () => class CustomComponent extends React.Component {
  render() {
    return (<div />);
  }
};

const checkProps = (props, expected) => {
  Object.keys(expected).forEach((name) => {
    const actual = props[name];
    const real = expected[name];
    expect(actual).toBe(real);
  });
};

describe('Paginator specs', () => {
  it('renders components with default values', () => {
    const amount = 190;
    const wrapper = shallow(<Paginator amount={amount} />);
    const divs = wrapper.find('div');

    expect(wrapper.find(Input)).toHaveLength(1);
    expect(wrapper.find(Delimeter)).toHaveLength(1);
    expect(wrapper.find(Control)).toHaveLength(2);
    expect(wrapper.find(Label)).toHaveLength(2);
    expect(wrapper.find(Picker)).toHaveLength(DEFAULT_AMOUNT_PICKERS_TO_SHOW);
    expect(divs.first().hasClass(PAGINATOR)).toBeTruthy();
  });

  it('disables parts, when disable provided', () => {
    const amount = 120;

    const wrapper = shallow(<Paginator amount={amount} />);

    wrapper.setProps({ enableControls: false });
    expect(wrapper.find(Control)).toHaveLength(0);

    wrapper.setProps({ enableInputControl: false });
    expect(wrapper.find(Input)).toHaveLength(0);

    wrapper.setProps({ enableLabels: false });
    expect(wrapper.find(Label)).toHaveLength(0);

    wrapper.setProps({ enableDelimeter: false });
    expect(wrapper.find(Delimeter)).toHaveLength(0);
  });

  it('renders custom components, if they are provided', () => {
    const amount = 120;
    const wrapper = mount(<Paginator amount={amount} />);

    const customPickerComponent = getCustomComponent();
    wrapper.setProps({ customPickerComponent });
    expect(wrapper.find(customPickerComponent)).toHaveLength(DEFAULT_AMOUNT_PICKERS_TO_SHOW);
    expect(wrapper.find(Picker)).toHaveLength(0);

    const customDelimeterComponent = getCustomComponent();
    wrapper.setProps({ customDelimeterComponent });
    expect(wrapper.find(customDelimeterComponent)).toHaveLength(1);
    expect(wrapper.find(Delimeter)).toHaveLength(0);

    const customLabelComponent = getCustomComponent();
    wrapper.setProps({ customLabelComponent });
    expect(wrapper.find(customLabelComponent)).toHaveLength(2);
    expect(wrapper.find(Label)).toHaveLength(0);

    const customControlComponent = getCustomComponent();
    wrapper.setProps({ customControlComponent });
    expect(wrapper.find(customControlComponent)).toHaveLength(2);
    expect(wrapper.find(Control)).toHaveLength(0);

    const customInputComponent = getCustomComponent();
    wrapper.setProps({ customInputComponent });
    expect(wrapper.find(customInputComponent)).toHaveLength(1);
    expect(wrapper.find(Input)).toHaveLength(0);
  });

  it('should provide props to components', () => {
    const amount = 120;

    const controlsProps = {
      controlUp: '+',
      controlDown: '-',
      controlClassName: 'control',
    };

    const inputProps = {
      inputControlValidator: () => {},
      inputClassName: 'input',
    };

    const labelsProps = {
      firstLabel: 'F',
      lastLabel: 'L',
      labelClassName: 'label',
    };

    const delimeterProps = {
      delimeterValue: '++++',
      delimeterClassName: 'delimeter',
    };

    const pickerProps = {
      pickerClassName: 'picker',
    };

    const props = {
      ...controlsProps,
      ...inputProps,
      ...labelsProps,
      ...delimeterProps,
      ...pickerProps,
    };

    const wrapper = mount(<Paginator amount={amount} {...props} />);

    const controls = wrapper.find(Control);

    const firstControl = controls.at(0);
    checkProps(firstControl.props(), {
      value: controlsProps.controlDown,
      className: controlsProps.controlClassName,
    });

    const secondControl = controls.at(1);
    checkProps(secondControl.props(), {
      value: controlsProps.controlUp,
      className: controlsProps.controlClassName,
    });

    const input = wrapper.find(Input);
    checkProps(input.props(), {
      validator: inputProps.inputControlValidator,
      className: inputProps.inputClassName,
    });

    const labels = wrapper.find(Label);
    const firstLabel = labels.at(0);
    checkProps(firstLabel.props(), {
      value: labelsProps.firstLabel,
      className: labelsProps.labelClassName,
    });

    const secondLabel = labels.at(1);
    checkProps(secondLabel.props(), {
      value: labelsProps.lastLabel,
      className: labelsProps.labelClassName,
    });

    const delimeter = wrapper.find(Delimeter);
    checkProps(delimeter.props(), {
      value: delimeterProps.delimeterValue,
      className: delimeterProps.delimeterClassName,
    });

    const pickers = wrapper.find(Picker);
    pickers.forEach((picker) => {
      checkProps(picker.props(), { className: pickerProps.pickerClassName });
    });
  });

  it('should call provided handler with proper value', () => {
    const amount = 120;
    const handler = jest.fn();

    const wrapper = mount(<Paginator amount={amount} onPickerChange={handler} />);

    // click first disabled
    wrapper.find(Label).at(0).simulate('click');
    expect(handler).not.toHaveBeenCalled();

    // click controlDown disabled
    wrapper.find(Control).at(0).simulate('click');
    expect(handler).not.toHaveBeenCalled();

    // click 3
    wrapper.find(Picker).at(2).simulate('click');
    expect(handler).toHaveBeenLastCalledWith(3);

    // click 3
    wrapper.find(Picker).at(0).simulate('click');
    expect(handler).toHaveBeenCalledTimes(1);

    // click first
    wrapper.find(Label).at(0).simulate('click');
    expect(handler).toHaveBeenLastCalledWith(1);

    // click last
    wrapper.find(Label).at(1).simulate('click');
    expect(handler).toHaveBeenLastCalledWith(12);

    // click last disabled
    wrapper.find(Label).at(1).simulate('click');
    expect(handler).toHaveBeenCalledTimes(3);

    // click controlUp disabled
    wrapper.find(Control).at(1).simulate('click');
    expect(handler).toHaveBeenCalledTimes(3);

    // change input to 5
    wrapper.find(Input).simulate('change', { target: { value: 5 } });
    expect(handler).toHaveBeenLastCalledWith(5);

    // change input to invalid value
    wrapper.find(Input).simulate('change', { target: { value: 'invalid' } });
    expect(handler).toHaveBeenCalledTimes(4);

    // change input to 5 (not invoke handler)
    wrapper.find(Input).simulate('change', { target: { value: 5 } });
    expect(handler).toHaveBeenCalledTimes(4);

    // click controlDown (-1)
    wrapper.find(Control).at(0).simulate('click');
    expect(handler).toHaveBeenLastCalledWith(4);

    // click controlUp (+1)
    wrapper.find(Control).at(1).simulate('click');
    expect(handler).toHaveBeenLastCalledWith(5);
  });

  it('should toggle direction strategy, when same picker clicked', () => {
    const amount = 120;
    const wrapper = mount(<Paginator amount={amount} />);

    // 1,2,3...12 |-> click 3 |-> 3,4,5...12
    wrapper.find(Picker).at(2).simulate('click');
    expect(wrapper.find(Picker).at(0).contains(3)).toBeTruthy();

    // 3,4,5...12 |-> click 3 |-> 1,2,3...12
    wrapper.find(Picker).at(0).simulate('click');
    expect(wrapper.find(Picker).at(2).contains(3)).toBeTruthy();
  });
});
