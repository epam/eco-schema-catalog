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
import { debounce } from 'lodash-es';

import searchIcon from './assets/search_grey.svg';
import closeIcon from '../../../../assets/close.svg';

import './FilterInput.scss';

class FilterInput extends Component {
  static propTypes = {
    onChangeValue: PropTypes.func,
    value: PropTypes.string,
  }

  constructor(props) {
    super(props);
    this.handleChangheValue = debounce(props.onChangeValue, 300);
    this.inputKey = Math.random() * 1000;
  }

  handleClearInput = () => {
    const { onChangeValue } = this.props;
    this.inputKey = Math.random() * 1000;
    onChangeValue('');
  }

  render() {
    const { value } = this.props;
    return (
      <div className="filter-input-wrapper">
        <div className="search-input">
          <img src={searchIcon} alt="" />
          <input
            className="input"
            key={this.inputKey}
            onChange={e => this.handleChangheValue(e.target.value)}
            placeholder="Filter by field name.."
          />
        </div>

        {
          value
          && (
          <div
            className="clear-button"
            onClick={() => this.handleClearInput()}
            role="presentation"
          >
            <img src={closeIcon} alt="" />
          </div>
          )
        }

      </div>
    );
  }
}

export default FilterInput;
