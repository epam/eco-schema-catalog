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
import ArrowComponent from '../ArrowComponent/ArrowComponent';
import './DropDownButton.scss';

class DropDownButton extends Component {
  static propTypes = {
    menuName: PropTypes.string.isRequired,
    children: PropTypes.oneOfType([
      PropTypes.array,
      PropTypes.object,
    ]),
    icon: PropTypes.node,
    className: PropTypes.string,
    transparent: PropTypes.bool,
    arrowComponent: PropTypes.any,
  }

  static defaultProps = {
    arrowComponent: ArrowComponent,
  }

  constructor(props) {
    super(props);
    this.state = { isOpen: false };
  }

  componentDidMount() {
    document.addEventListener('mousedown', this.handleOutsideClick);
  }

  componentWillUnmount() {
    document.removeEventListener('mousedown', this.handleOutsideClick);
  }

  handleToogleMenu = (e) => {
    const { isOpen } = this.state;
    if (this.menuRef.contains(e.target)) {
      this.setState({ isOpen: !isOpen });
    }
  }

  handleOutsideClick = (e) => {
    if (!this.menuRef.contains(e.target)) {
      this.setState({ isOpen: false });
    }
  }

  render() {
    const {
      children,
      menuName,
      className,
      transparent = false,
      icon,
      arrowComponent,
    } = this.props;
    const { isOpen } = this.state;
    const CustomArrowComponent = arrowComponent;
    return (
      <div
        className="dh-dropdown-button-container"
        ref={(ref) => { this.menuRef = ref; }}
      >
        <button
          className={`
            ${className}
            ${transparent ? 'transparent' : ''}
            dh-dropdown-button
            `
          }
          onClick={this.handleToogleMenu}
          type="button"
        >
          {icon}
          <span>{menuName}</span>
          <CustomArrowComponent isOpen={isOpen} />
        </button>

        {isOpen
          ? (
            <div className="dropdown-button-items">
              {children}
            </div>
          )
          : null
        }

      </div>
    );
  }
}

export default DropDownButton;
