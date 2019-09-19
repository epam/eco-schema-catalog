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

import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import ArrowComponent from '../ArrowComponent/ArrowComponent';
import ClearComponent from './ClearComponent/ClearComponent';
import OptionsComponent from './OptionsComponent/OptionsComponent';
import './Selector.scss';

class Selector extends PureComponent {
  static propTypes = {
    options: PropTypes.array.isRequired,
    onOptionChange: PropTypes.func.isRequired,
    onClear: PropTypes.func,
    valueType: PropTypes.string,
    selectedOption: PropTypes.oneOfType([PropTypes.string, PropTypes.array]),
    className: PropTypes.string,
    buttonHeight: PropTypes.number,
    optionHeigth: PropTypes.number,
    offsetOptions: PropTypes.number,
    buttonClassName: PropTypes.string,
    placeholder: PropTypes.string,
    arrowComponent: PropTypes.any,
    clearComponent: PropTypes.any,
    optionsComponent: PropTypes.any,
    isClearIcon: PropTypes.bool,
    handleIsOpen: PropTypes.func,
  }

  static defaultProps = {
    valueType: 'single',
    buttonClassName: 'default-button',
    buttonHeight: 36,
    optionHeigth: 30,
    offsetOptions: 6,
    placeholder: 'choose option',
    arrowComponent: ArrowComponent,
    clearComponent: ClearComponent,
    optionsComponent: OptionsComponent,
    onClear: () => {},
    isClearIcon: false,
    handleIsOpen: () => {},
  }

  state = {
    isOpen: false,
  };

  componentDidMount() {
    document.addEventListener('mousedown', this.handleOutsideClick);
  }

  componentWillUnmount() {
    document.removeEventListener('mousedown', this.handleOutsideClick);
  }

  getSelectedValue = () => {
    const { valueType, selectedOption } = this.props;
    if (valueType === 'single') {
      return selectedOption;
    }
    if (valueType === 'multiple' && selectedOption.length) {
      return `${selectedOption.length} selected`;
    }
    return null;
  }

  handleOutsideClick = (e) => {
    const { handleIsOpen } = this.props;
    if (!this.selector.contains(e.target)) {
      this.setState({ isOpen: false }, () => handleIsOpen(this.state.isOpen));
    }
  }

  handleToogleSelector = (e) => {
    const { isOpen } = this.state;
    const { handleIsOpen } = this.props;
    if (this.selector.contains(e.target)) {
      this.setState({ isOpen: !isOpen }, () => handleIsOpen(this.state.isOpen));
    }
  }

  handleSelectOption = (option) => {
    const { isOpen } = this.state;
    const { onOptionChange, valueType } = this.props;
    if (onOptionChange) {
      onOptionChange(option);
    }
    if (valueType === 'single') {
      this.setState({ isOpen: !isOpen });
    }
  }

  isHaveSelected = () => {
    const { selectedOption, valueType } = this.props;
    if (valueType === 'single') {
      return !!selectedOption;
    }
    if (valueType === 'multiple') {
      return !!selectedOption.length;
    }
    return false;
  }

  render() {
    const {
      options = [],
      className,
      placeholder,
      selectedOption,
      onClear,
      buttonHeight,
      buttonClassName,
      optionHeigth,
      offsetOptions,
      arrowComponent,
      clearComponent,
      optionsComponent,
      isClearIcon,
    } = this.props;
    if (!options) {
      return null;
    }
    const { isOpen } = this.state;
    const CustomArrowComponent = arrowComponent;
    const CustomClearComponent = clearComponent;
    const CustomOptionsComponent = optionsComponent;
    return (
      <div
        className={`dh-selector ${className}`}
        ref={(ref) => { this.selector = ref; }}
      >

        <button
          style={{ height: `${buttonHeight}px` }}
          className={buttonClassName || ''}
          type="button"
          onClick={this.handleToogleSelector}
        >
          <span>
            {this.getSelectedValue() || placeholder}
          </span>
          <CustomArrowComponent isOpen={isOpen} />
          {
            this.isHaveSelected() && isClearIcon && (
            <CustomClearComponent handleClear={(e) => {
              e.stopPropagation();
              onClear();
            }}
            />
            )
          }
        </button>

        {isOpen && (
          <CustomOptionsComponent
            options={options}
            selectedOption={selectedOption}
            handleSelectOption={this.handleSelectOption}
            placeholder={placeholder}
            buttonHeight={buttonHeight}
            buttonOffsetTop={this.selector.getBoundingClientRect().top}
            optionHeigth={optionHeigth}
            offsetOptions={offsetOptions}
            {...this.props}
          />
        )
        }
      </div>
    );
  }
}

export default Selector;
