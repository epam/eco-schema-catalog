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
import FilterInput from '../../../common/FilterInput/FilterInput';
import './NamespaceOptions.scss';

export default class NamespaceOptions extends Component {
  static propTypes = {
    options: PropTypes.array,
    selectedOption: PropTypes.string,
    handleSelectOption: PropTypes.func,
    optionHeigth: PropTypes.number,
  }

  state = {
    filterInputValue: '',
    filteredOptions: this.props.options,
  }

  handleChangeFilterValue = (newValue) => {
    const reg = new RegExp(newValue, 'i');
    this.setState({
      filterInputValue: newValue,
      filteredOptions: this.props.options.filter(option => reg.test(option)),
    });
  }

  render() {
    const {
      selectedOption,
      handleSelectOption,
      optionHeigth,
    } = this.props;
    const { filterInputValue, filteredOptions } = this.state;
    return (
      <ul className="advanced-search-selector-options">
        <li className="namespace-filter-input-wrapper">
          <FilterInput
            value={filterInputValue}
            onChangeValue={this.handleChangeFilterValue}
          />
        </li>
        {filteredOptions
          .map(option => (
            <li
              key={option[0]}
              style={{ height: `${optionHeigth}px` }}
              className="row"
            >
              <Checkbox
                id={option[0]}
                isChecked={selectedOption.includes(option[0])}
                onCheck={(_isChecked, value) => handleSelectOption(value)}
              />
              <label className="label" htmlFor={option[0]}>
                <span>{option[0]}</span>
              </label>
              <div className="amount">{option[1]}</div>
            </li>
          ))
          }
      </ul>
    );
  }
}
