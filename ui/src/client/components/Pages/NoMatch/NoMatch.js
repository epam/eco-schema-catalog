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

import './NoMatch.scss';

import gradient from './img/path.png';
import lines from './img/stars.png';

class NoMatch extends Component {
  render() {
    return (
      <div className="no-match">
        <div className="stars">

          <div className="star-lines">
            <img src={lines} alt="lines" />
            <div className="star-gradient">
              <img src={gradient} alt="line" />
            </div>
          </div>
        </div>

        <div className="no-match-content">

          <div className="image-404" />
          <p className="oops">Oops!</p>
          <p className="something-wrong">Looks like the page you are looking for is missing.</p>
          <p className="something-wrong">Sorry for inconvenience.</p>
          <a href="/" className="homepage-btn">home page</a>

        </div>
      </div>
    );
  }
}

export default NoMatch;
