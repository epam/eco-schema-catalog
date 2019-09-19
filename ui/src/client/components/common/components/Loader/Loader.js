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
import './Loader.scss';

class Loader extends Component {
  static propTypes = {
    type: PropTypes.string,
    color: PropTypes.string,
    className: PropTypes.string,
    width: PropTypes.number,
    height: PropTypes.number,
  }

  static defaultProps = {
    width: 40,
    height: 40,
  }

  render() {
    const {
      type, color, className = ' ', width, height,
    } = this.props;
    return (
      <div
        style={{ width: `${width}px`, height: `${height}px` }}
        className={`${className} ${type} ${color} uui-loader`}
      >
        <div className="dot dot-1" />
        <div className="dot dot-2" />
        <div className="dot dot-3" />
        <div className="dot dot-4" />
        <div className="dot dot-5" />
        <div className="dot dot-6" />
        <div className="dot dot-7" />
        <div className="dot dot-8" />
        <div className="dot dot-9" />
        <div className="dot dot-10" />
        <div className="dot dot-11" />
        <div className="dot dot-12" />
      </div>
    );
  }
}

export default Loader;
