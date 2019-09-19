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
import Checkbox from '../../Checkbox/Checkbox';
import './OptionsComponent.scss';

class OptionsComponent extends Component {
  static propTypes = {
    options: PropTypes.array.isRequired,
    handleSelectOption: PropTypes.func.isRequired,
    selectedOption: PropTypes.oneOfType([PropTypes.string, PropTypes.array]),
    valueType: PropTypes.string,
    buttonHeight: PropTypes.number,
    buttonOffsetTop: PropTypes.number,
    optionHeigth: PropTypes.number,
    offsetOptions: PropTypes.number,
    numOfRows: PropTypes.number,
  }

  static defaultNumberOfRows = 8;

  static calcTransformOptionsHeight = (...args) => {
    const [
      clientHeight,
      buttonOffsetTop,
      optionsLength,
      optionHeigth,
      buttonHeight,
    ] = args;
    this.numOfRows = optionsLength > OptionsComponent.defaultNumberOfRows
      ? OptionsComponent.defaultNumberOfRows
      : optionsLength;
    if (buttonOffsetTop) {
      const transformHeight = (optionsLength * optionHeigth) + buttonHeight;
      if (transformHeight > buttonOffsetTop) {
        return 0;
      }
      return (clientHeight < (transformHeight + buttonOffsetTop)) ? transformHeight : 0;
    }
    return 0;
  }

  constructor(props) {
    super(props);
    this.state = {
      transformHeight: OptionsComponent.calcTransformOptionsHeight(
        window.innerHeight,
        props.buttonOffsetTop,
        props.options.length,
        props.optionHeigth,
        props.buttonHeight,
      ),
    };
  }

  getOptionsBody = () => {
    const {
      valueType,
      options,
      optionHeigth,
      selectedOption,
      handleSelectOption,
    } = this.props;
    if (valueType === 'single') {
      return options.map(option => (
        <li key={option} style={{ height: `${optionHeigth}px` }}>
          <a
            href="#"
            onClick={(e) => {
              e.preventDefault();
              handleSelectOption(option);
            }}
            className={selectedOption === option ? 'active' : ''}
          >
            {option}
          </a>
        </li>
      ));
    }
    if (valueType === 'multiple') {
      return options.map(option => (
        <li
          key={option}
          style={{ height: `${optionHeigth}px` }}
        >
          <Checkbox
            id={option}
            isChecked={selectedOption.includes(option)}
            onCheck={(isChecked, value) => handleSelectOption(value)}
          />
          <label htmlFor={option}>
            <span className="label">{option}</span>
          </label>
        </li>
      ));
    }
    return null;
  }

  render() {
    const { transformHeight } = this.state;
    const { offsetOptions, optionHeigth, numOfRows = this.numOfRows } = this.props;
    return (
      <ul
        style={{
          transform: `translateY(${-transformHeight}px`,
          marginTop: transformHeight > 0 ? `${-offsetOptions}px` : `${offsetOptions}px`,
          height: `${optionHeigth * numOfRows}px`,
        }}
        className="dh-selector-options"
      >
        {this.getOptionsBody()}
      </ul>
    );
  }
}

export default OptionsComponent;
