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
import { Link } from 'react-router-dom';
import './MenuDropdownItem.scss';

class MenuDropdownItem extends Component {
  static propTypes = {
    name: PropTypes.string,
    href: PropTypes.string,
    isBlank: PropTypes.bool,
    rel: PropTypes.bool,
    customClass: PropTypes.string,
  }

  render() {
    const {
      name, href, isBlank = false, customClass, rel = false,
    } = this.props;
    let menuItemLink = (
      <a href={href}>
        {name}
      </a>
    );
    if (isBlank) {
      menuItemLink = (
        <a href={href} target="_blank" rel="noopener noreferrer">
          {name}
        </a>
      );
    } else if (rel) {
      menuItemLink = (
        <Link to={href}>
          {name}
        </Link>
      );
    }
    return (
      <div className={`menu-item ${customClass || ''}`}>
        {menuItemLink}
      </div>
    );
  }
}

export default MenuDropdownItem;
