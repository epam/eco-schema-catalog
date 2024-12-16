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

/* eslint-disable import/no-unresolved */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Checkbox } from 'react-eco-ui';

class YesNoOptions extends Component {
  static propTypes = {
    options: PropTypes.array,
    selectedOption: PropTypes.array,
    handleSelectOption: PropTypes.func,
    optionHeigth: PropTypes.number,
  }

  items = {
    true: 'yes',
    false: 'no',
  }

  onCheck = (_isChecked, value) => {
    const { handleSelectOption } = this.props;
    handleSelectOption(value);
  }

  render() {
    const {
      options,
      selectedOption,
      optionHeigth,
    } = this.props;
    return (
      <ul className="advanced-search-selector-options">
        {options.map(option => (
          <li
            key={option[0]}
            style={{ height: `${optionHeigth}px` }}
            className="row"
          >
            <Checkbox
              id={option[0]}
              isChecked={selectedOption.includes(option[0])}
              onCheck={this.onCheck}
            />
            <label className="label" htmlFor={option[0]}>
              <span>{this.items[option[0]]}</span>
            </label>
            <div className="amount">{option[1]}</div>
          </li>
        ))}
      </ul>
    );
  }
}

export default YesNoOptions;
