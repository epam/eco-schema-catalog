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
import MenuDropdown from '../MenuDropdown/MenuDropdown';
import MenuDropdownItem from '../MenuDropdown/MenuDropdownItem/MenuDropdownItem';
import Menulink from '../MenuLink/MenuLink';
import './Header.scss';

export default class Header extends Component {
  static propTypes = {
    children: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.node),
      PropTypes.node,
    ]),
    logo: PropTypes.string,
    brandName: PropTypes.string,
    maxWidth: PropTypes.number,
  }

  static defaultProps = {
    maxWidth: 720,
  }

  static parseMenu = (menuSource, isMobileMenu) => {
    const menuSourceEntries = Object.entries(menuSource);
    return menuSourceEntries.map((entrie) => {
      if (typeof entrie[1] === 'string') {
        return (
          <Menulink
            key={entrie[0]}
            menuName={entrie[0]}
            href={entrie[1]}
            isBlank={entrie[1].slice(0, 4) === 'http'}
          />
        );
      }
      return (
        <MenuDropdown
          key={entrie[0]}
          menuName={entrie[0]}
          isMobileMenu={isMobileMenu}
        >
          {Object.entries(entrie[1]).map(menuItemEntrie => (
            <MenuDropdownItem
              key={menuItemEntrie[0]}
              name={menuItemEntrie[0]}
              href={menuItemEntrie[1]}
              isBlank={menuItemEntrie[1].slice(0, 4) === 'http'}
            />
          ))}
        </MenuDropdown>
      );
    });
  }

  constructor(props) {
    super(props);
    this.mq = window.matchMedia(`(max-width: ${props.maxWidth}px)`);
    this.mq.addListener(this.transformMenu);
    this.state = {
      isShowGlobalMenu: !(window.innerWidth < props.maxWidth),
    };
  }

  transformMenu = (mq) => {
    if (mq.matches) {
      this.setState({ isShowGlobalMenu: false });
    } else {
      this.setState({ isShowGlobalMenu: true });
    }
  }

  handleOpenMenu = () => {
    const { isShowGlobalMenu } = this.state;
    this.setState({ isShowGlobalMenu: !isShowGlobalMenu });
  }

  render() {
    const { children, logo, brandName } = this.props;
    return (
      <header className="header-content">

        <div
          className="burger-menu"
          onClick={this.handleOpenMenu}
          role="presentation"
        >
          <span />
          <span />
          <span />
        </div>

        <div className="brand">
          <a href="/">
            {logo ? <img src={logo} alt="brand logo" /> : null}
            {brandName}
          </a>
        </div>
        {children}
      </header>
    );
  }
}
