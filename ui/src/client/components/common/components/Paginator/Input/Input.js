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

import { noop, identity } from '../../../utils/functional';
import { skipEmptyClassNames } from '../../../utils/string';

import { DEFAULT_VALUE, EMPTY } from '../../../consts/core';
import { VALID, INVALID, INPUT } from '../../../consts/input';

export default class Input extends Component {
  state = { isValid: this.validate(this.value) };

  get value() {
    return this.props.value;
  }

  get isValid() {
    return this.state.isValid;
  }

  get className() {
    const { className } = this.props;
    const validClassName = this.isValid ? VALID : INVALID;

    return skipEmptyClassNames([INPUT, validClassName, className]);
  }

  onChange = ({ target }) => {
    const { onChange } = this.props;

    let isValid = false;

    const receivedValue = target.value;

    if (this.validate(receivedValue)) {
      isValid = true;

      if (this.value === receivedValue) {
        return;
      }
    }

    this.setState({ isValid });
    onChange(receivedValue);
  };

  validate(value) {
    return Boolean(this.props.validator(value));
  }

  render() {
    return (
      <input
        className={this.className}
        data-valid={this.isValid}
        onChange={this.onChange}
        value={this.value}
      />
    );
  }
}

Input.defaultProps = {
  onChange: noop,
  validator: identity,
  className: EMPTY,
  value: DEFAULT_VALUE,
};

Input.propTypes = {
  className: PropTypes.string,
  validator: PropTypes.func,
  onChange: PropTypes.func,
  value: PropTypes.any,
};
