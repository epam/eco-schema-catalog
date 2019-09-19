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

import './TestResult.scss';

export default class TestResult extends Component {
  static propTypes = {
    testErrors: PropTypes.array,
    tested: PropTypes.bool,
  }

  render() {
    const { testErrors, tested } = this.props;
    if (testErrors.length === 0 && !tested) {
      return null;
    }

    if (testErrors.length === 0 && tested) {
      return (<span style={{ padding: '4px 0', color: 'green' }}>ok</span>);
    }

    return (
      <div>
        {
          testErrors.map(error => (
            <div className="test-error">
              {error.path
                ? (
                  <div className="item">
                    <span>path: </span>
                    <span className="text">{error.path}</span>
                  </div>
                ) : null
              }
              <div className="item">
                <span className="text">{error.message}</span>
              </div>
            </div>
          ))
        }
      </div>
    );
  }
}
