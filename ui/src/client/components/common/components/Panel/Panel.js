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
import './Panel.scss';

class Panel extends Component {
  static propTypes = {
    headerName: PropTypes.string,
    className: PropTypes.string,
    children: PropTypes.node,
  }

  render() {
    const { headerName, className, children } = this.props;
    return (
      <div className={`dh-panel ${className}`}>
        <div className="header">
          <h4>{headerName}</h4>
        </div>
        <div className="body">
          {children}
        </div>
      </div>
    );
  }
}

export default Panel;
