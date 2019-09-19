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

import './Button.scss';

class Button extends Component {
  static propTypes = {
    name: PropTypes.string,
    className: PropTypes.string,
    transparent: PropTypes.bool,
    disable: PropTypes.bool,
    onClick: PropTypes.func,
  }

  handleClick = () => {
    const { onClick, disable } = this.props;
    if (!disable) {
      onClick();
    }
  }

  render() {
    const {
      name, className, transparent = false, disable,
    } = this.props;
    return (
      <button
        className={
          `dh-button
          ${transparent ? 'transparent' : ''}
          ${disable ? 'disable' : ''}
          ${className || 'blue'}`
        }
        type="button"
        onClick={this.handleClick}
      >
        {name}
      </button>
    );
  }
}

export default Button;
