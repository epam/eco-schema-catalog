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
/* eslint-disable no-restricted-globals */
import React, { Component } from 'react';
import PropTypes from 'prop-types';

import Picker from './Picker/Picker';
import Input from './Input/Input';
import Delimeter from './Delimeter/Delimeter';
import Control from './Control/Control';
import Label from './Label/Label';

import {
  calculateAllAvailablePickers,
  calculateVisiblePickers,
  producePickerMap,
  calculateIndexes,
} from '../../utils/paginator';

import { noop } from '../../utils/functional';
import { skipEmptyClassNames } from '../../utils/string';

import { EMPTY } from '../../consts/core';
import { FIRST, LAST } from '../../consts/labels';
import { CONTROL_UP, CONTROL_DOWN } from '../../consts/controls';
import {
  PAGINATOR,
  DEFAULT_INIT_INDEX,
  DEFAULT_DELIMETER,
  DEFAULT_VALUE_PER_PAGE,
  DEFAULT_AMOUNT_PICKERS_TO_SHOW,
  DEFAULT_ENABLE,
} from '../../consts/paginator';

export default class Paginator extends Component {
  _allPickersCache = {};

  _visiblePickersCache = {};

  state = {
    currentIndex: this.initIndex,
    controlInputValue: this.initIndex,
    direction: { up: true, down: false },
  };

  componentDidUpdate(prevProps) {
    if (prevProps.amount !== this.props.amount) {
      // eslint-disable-next-line react/no-did-update-set-state
      this.setState({ currentIndex: 1 });
    }
  }

  get initIndex() {
    return this.props.initIndex;
  }

  get className() {
    return skipEmptyClassNames([PAGINATOR, this.props.className]);
  }

  get amount() {
    return this.props.amount;
  }

  get amountPickersToShow() {
    return this.props.amountPickersToShow;
  }

  get valuePerPage() {
    return this.props.valuePerPage;
  }

  get allPikersCacheKey() {
    return `amount:${this.amount}_perPage:${this.valuePerPage}`;
  }

  get visiblePickersChacheKey() {
    return `all:${this.allPickers}_toShow:${this.amountPickersToShow}`;
  }

  get allPickers() {
    const cachedValue = this._allPickersCache[this.allPikersCacheKey];

    if (cachedValue) {
      return cachedValue;
    }

    const allPickers = calculateAllAvailablePickers(this.amount, this.valuePerPage);
    this._allPickersCache[this.allPikersCacheKey] = allPickers;

    return allPickers;
  }

  get visiblePickers() {
    const cachedValue = this._visiblePickersCache[this.visiblePickersChacheKey];

    if (cachedValue) {
      return cachedValue;
    }

    const visiblePickers = calculateVisiblePickers(this.allPickers, this.amountPickersToShow);
    this._visiblePickersCache[this.visiblePickersChacheKey] = visiblePickers;

    return visiblePickers;
  }

  get indexes() {
    const {
      currentIndex, allPickers, visiblePickers, direction,
    } = this;
    const { up, down } = direction;

    return calculateIndexes({
      currentIndex, up, down, all: allPickers, visibleAmount: visiblePickers,
    });
  }

  get sequence() {
    const {
      currentIndex,
      allPickers,
      visiblePickers,
      indexes,
      labelsConfig,
      controlsConfig,
      delimeter,
    } = this;

    return producePickerMap({
      withLast: indexes.length !== visiblePickers,
      labels: labelsConfig,
      delimeter,
      controls: controlsConfig,
      currentIndex,
      indexes,
      lastIndex: allPickers,
    });
  }

  get labelsConfig() {
    const { enableLabels, firstLabel, lastLabel } = this;

    return enableLabels ? { firstLabel, lastLabel } : {};
  }

  get controlsConfig() {
    const { enableControls, controlUp, controlDown } = this;

    return enableControls ? { controlUp, controlDown } : {};
  }

  get delimeter() {
    const { enableDelimeter, delimeterValue } = this;

    return enableDelimeter && delimeterValue;
  }

  get enableDelimeter() {
    return this.props.enableDelimeter;
  }

  get delimeterValue() {
    return this.props.delimeterValue;
  }

  get firstLabel() {
    return this.props.firstLabel;
  }

  get lastLabel() {
    return this.props.lastLabel;
  }

  get enableLabels() {
    return this.props.enableLabels;
  }

  get controlUp() {
    return this.props.controlUp;
  }

  get controlDown() {
    return this.props.controlDown;
  }

  get enableControls() {
    return this.props.enableControls;
  }

  get direction() {
    return this.state.direction;
  }

  get currentIndex() {
    return this.state.currentIndex;
  }

  get controlInputValue() {
    return this.state.controlInputValue;
  }

  onPickerChange = (value) => {
    const index = this.convertValueToIndex(value);

    if (index === this.currentIndex) {
      this.toggleDirection();

      return;
    }

    this.setState({ currentIndex: index, controlInputValue: index });

    this.props.onPickerChange(index);
  };

  validateInput = (value) => {
    const castToNumber = Number(value);

    return !isNaN(castToNumber)
      && isFinite(castToNumber)
      && castToNumber > 0
      && castToNumber <= this.allPickers;
  }

  onInputChange = (value) => {
    this.setState({ controlInputValue: value });

    if (!this.validateInput(value)) {
      return;
    }

    this.onPickerChange(Number(value));
  }

  isDelimeter(value) {
    return value === this.delimeterValue;
  }

  isControl(value) {
    return value === this.controlUp || value === this.controlDown;
  }

  isLabel(value) {
    return value === this.firstLabel || value === this.lastLabel;
  }

  toggleDirection() {
    const { up, down } = this.direction;
    const newDirection = { up: !up, down: !down };

    this.setState({ direction: newDirection });
  }

  convertValueToIndex(value) {
    switch (value) {
      case this.firstLabel:
        return 1;
      case this.lastLabel:
        return this.allPickers;
      case this.controlUp:
        return this.currentIndex + 1;
      case this.controlDown:
        return this.currentIndex - 1;
      default:
        return value;
    }
  }

  generateDelimeter(value) {
    const { customDelimeterComponent, delimeterClassName } = this.props;
    const DelimeterComponent = customDelimeterComponent;

    return (<DelimeterComponent key={value} className={delimeterClassName} value={value} />);
  }

  generatePicker(value, disabled, picked) {
    const { customPickerComponent, pickerClassName } = this.props;
    const PickerComponent = customPickerComponent;

    return (
      <PickerComponent
        key={`${value}${disabled}${picked}`}
        className={pickerClassName}
        value={value}
        disabled={disabled}
        picked={picked}
        onClick={this.onPickerChange}
      />
    );
  }

  generateControl(value, disabled) {
    const { customControlComponent, controlClassName } = this.props;
    const ControlComponent = customControlComponent;

    return (
      <ControlComponent
        key={`${value}${disabled}`}
        className={controlClassName}
        value={value}
        disabled={disabled}
        onClick={this.onPickerChange}
      />
    );
  }

  generateLabel(value, disabled) {
    const { customLabelComponent, labelClassName } = this.props;
    const LabelComponent = customLabelComponent;

    return (
      <LabelComponent
        key={`${value}${disabled}`}
        className={labelClassName}
        value={value}
        disabled={disabled}
        onClick={this.onPickerChange}
      />
    );
  }

  generateSequence() {
    return this.sequence
      .map(({ value, disabled, picked }) => {
        if (this.isDelimeter(value)) {
          return this.generateDelimeter(value);
        }

        if (this.isLabel(value)) {
          return this.generateLabel(value, disabled);
        }

        if (this.isControl(value)) {
          return this.generateControl(value, disabled);
        }

        return this.generatePicker(value, disabled, picked);
      });
  }

  generateInputControl() {
    const {
      enableInputControl, customInputComponent, inputControlValidator, inputClassName,
    } = this.props;
    const { controlInputValue, amount } = this;

    if (!enableInputControl || !amount) {
      return null;
    }

    const InputComponent = customInputComponent;
    const validator = inputControlValidator || this.validateInput;

    return (
      <InputComponent
        className={inputClassName}
        validator={validator}
        onChange={this.onInputChange}
        value={controlInputValue}
      />
    );
  }

  render() {
    return (
      <div className={this.className}>
        { this.generateInputControl() }
        { this.generateSequence() }
      </div>
    );
  }
}

Paginator.defaultProps = {
  /** assets configuration * */
  valuePerPage: DEFAULT_VALUE_PER_PAGE,
  amountPickersToShow: DEFAULT_AMOUNT_PICKERS_TO_SHOW,
  className: EMPTY,

  /** customization * */
  customPickerComponent: Picker,
  pickerClassName: EMPTY,

  /** delimeter configuration * */
  enableDelimeter: DEFAULT_ENABLE.DELIMETER,
  customDelimeterComponent: Delimeter,
  delimeterValue: DEFAULT_DELIMETER,
  delimeterClassName: EMPTY,

  /** labels configuration * */
  enableLabels: DEFAULT_ENABLE.LABELS,
  customLabelComponent: Label,
  firstLabel: FIRST,
  lastLabel: LAST,
  labelClassName: EMPTY,

  /** controls configuration * */
  enableControls: DEFAULT_ENABLE.CONTROLS,
  customControlComponent: Control,
  controlUp: CONTROL_UP,
  controlDown: CONTROL_DOWN,
  controlClassName: EMPTY,

  /** input configuration * */
  enableInputControl: DEFAULT_ENABLE.INPUT_CONTROL,
  customInputComponent: Input,
  inputClassName: EMPTY,

  /** base configuration * */
  onPickerChange: noop,
  initIndex: DEFAULT_INIT_INDEX,
};

Paginator.propTypes = {
  /** mandatory prop * */
  amount: PropTypes.number.isRequired,

  /** assets configuration * */
  valuePerPage: PropTypes.number,
  amountPickersToShow: PropTypes.number,
  className: PropTypes.string,

  /** customization * */
  customPickerComponent: PropTypes.any,
  pickerClassName: PropTypes.string,

  /** delimeter configuration * */
  enableDelimeter: PropTypes.bool,
  customDelimeterComponent: PropTypes.any,
  delimeterValue: PropTypes.string,
  delimeterClassName: PropTypes.string,

  /** labels configuration * */
  enableLabels: PropTypes.bool,
  customLabelComponent: PropTypes.any,
  firstLabel: PropTypes.string,
  lastLabel: PropTypes.string,
  labelClassName: PropTypes.string,

  /** controls configuration * */
  enableControls: PropTypes.bool,
  customControlComponent: PropTypes.any,
  controlUp: PropTypes.any,
  controlDown: PropTypes.any,
  controlClassName: PropTypes.string,

  /** input configuration * */
  enableInputControl: PropTypes.bool,
  customInputComponent: PropTypes.any,
  inputControlValidator: PropTypes.func,
  inputClassName: PropTypes.string,

  /** base configuration * */
  onPickerChange: PropTypes.func,
  initIndex: PropTypes.number,
};
