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
import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { noop } from '../../../utils/functional';
import { skipEmptyClassNames } from '../../../utils/string';

import {
  DEFAULT_VALUE, PICKED, DISABLED, EMPTY,
} from '../../../consts/core';
import { PICKER } from '../../../consts/picker';

export default class Picker extends Component {
  get value() {
    return this.props.value;
  }

  get disabled() {
    return this.props.disabled;
  }

  get picked() {
    return this.props.picked;
  }

  get className() {
    const { disabled, picked } = this;
    const { className } = this.props;
    const disabledClassName = disabled ? DISABLED : EMPTY;
    const pickedClassName = picked ? PICKED : EMPTY;

    return skipEmptyClassNames([PICKER, disabledClassName, pickedClassName, className]);
  }

  onClick = () => !this.disabled && this.props.onClick(this.value);

  render() {
    return (
      <div
        className={this.className}
        data-disabled={this.disabled}
        data-picked={this.picked}
        onClick={this.onClick}
        role="presentation"
      >
        {this.value}
      </div>
    );
  }
}

Picker.defaultProps = {
  onClick: noop,
  disabled: false,
  picked: false,
  value: DEFAULT_VALUE,
  className: EMPTY,
};

Picker.propTypes = {
  className: PropTypes.string,
  disabled: PropTypes.bool,
  picked: PropTypes.bool,
  value: PropTypes.any,
  onClick: PropTypes.func,
};
