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
import autosize from 'autosize';

import './Input.scss';

class Input extends Component {
  static propTypes = {
    id: PropTypes.string,
    onChange: PropTypes.func,
    onFocus: PropTypes.func,
    isError: PropTypes.bool,
    isLongText: PropTypes.bool,
    className: PropTypes.string,
    value: PropTypes.string,
    disable: PropTypes.bool,
    placeholder: PropTypes.string,
  }

  textAreaRef = React.createRef();

  componentDidMount() {
    const { isLongText = false } = this.props;
    if (isLongText) {
      autosize(this.textAreaRef.current);
    }
  }

  render() {
    const {
      id,
      value,
      onChange,
      isError = false,
      isLongText = false,
      onFocus,
      className,
      disable = false,
      placeholder,
    } = this.props;
    return !isLongText
      ? (
        <input
          className={`
            dh-input
            ${isError ? 'error' : ''}
            ${disable ? 'disable' : ''}
            ${className}
          `}
          tabIndex={disable ? -1 : 0}
          type="input"
          id={id}
          onChange={e => onChange(e.target.value)}
          value={value === null ? '' : value}
          onFocus={onFocus}
          placeholder={placeholder || ''}
        />
      )
      : (
        <textarea
          ref={this.textAreaRef}
          className={`
            dh-input dh-textarea
            ${isError ? 'error' : ''}
            ${className}
          `}
          id={id}
          onChange={e => onChange(e.target.value)}
          onFocus={onFocus}
          value={value === null ? '' : value}
        />
      );
  }
}

export default Input;
