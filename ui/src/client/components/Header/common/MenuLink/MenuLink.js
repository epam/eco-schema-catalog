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
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';
import './MenuLink.scss';

class MenuLink extends Component {
  static propTypes = {
    children: PropTypes.node,
    menuName: PropTypes.string,
    href: PropTypes.string,
    isBlank: PropTypes.bool,
    rel: PropTypes.bool,
  }

  render() {
    const {
      rel = false, isBlank = false, href, menuName, children,
    } = this.props;
    let link = (
      <a className="menu-link" href={href}>
        {children || menuName}
      </a>
    );
    if (isBlank) {
      link = (
        <a className="menu-link" href={href} target="_blank" rel="noopener noreferrer">
          {children || menuName}
        </a>
      );
    } else if (rel) {
      link = (
        <Link className="menu-link" to={href}>
          {children || menuName}
        </Link>
      );
    }
    return (
      <div className="menu-link-wrapper">
        {link}
      </div>
    );
  }
}

export default MenuLink;
