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
import withOutsideClick from '../../../../hoc/withOutsideClick';
import './MenuDropdown.scss';

class MenuDropdown extends Component {
  static propTypes = {
    children: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.node),
      PropTypes.node,
    ]),
    isMobileMenu: PropTypes.bool,
    menuName: PropTypes.string,
    className: PropTypes.string,
  }

  constructor(props) {
    super(props);
    this.state = { isOpen: false };
  }

  handleToogleMenu = () => {
    const { isOpen } = this.state;
    this.setState({ isOpen: !isOpen });
  }

  render() {
    const {
      children,
      isMobileMenu,
      menuName,
      className,
    } = this.props;
    const { isOpen } = this.state;

    const MenuItems = withOutsideClick(() => (
      <div className="menu-items fadeIn">
        {children}
      </div>
    ));

    return (
      <div
        className={`${className || ''} menu`}
      >
        <button
          onClick={this.handleToogleMenu}
          className={`transparent-button ${isOpen && !isMobileMenu ? 'active' : ''}`}
          type="button"
        >
          <span className="menu-name">
            {menuName}
          </span>
          <div className="arrow">
            <i className={`fa fa-angle-down ${isOpen ? 'open' : ''}`} aria-hidden="true" />
          </div>
        </button>
        {isOpen
          ? <MenuItems outsideClickCallback={this.handleToogleMenu} />
          : null
        }
      </div>
    );
  }
}

export default MenuDropdown;
