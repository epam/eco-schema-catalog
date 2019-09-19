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
import './VersionSelectorOptions.scss';

class VersionSelectorOptions extends Component {
  static propTypes = {
    options: PropTypes.array,
    selectedOption: PropTypes.string,
    handleSelectOption: PropTypes.func,
    optionHeigth: PropTypes.number,
  }

  render() {
    const {
      options,
      selectedOption,
      handleSelectOption,
      optionHeigth,
    } = this.props;
    return (
      <ul className="version-selector-options">
        {options.map(option => (
          <li
            key={option}
            style={{ height: `${optionHeigth}px` }}
            className="row"
          >
            <a
              href="#"
              onClick={() => handleSelectOption(option)}
              className={selectedOption === option ? 'active' : ''}
            >
              {option}
            </a>
          </li>
        ))}
      </ul>
    );
  }
}

export default VersionSelectorOptions;
