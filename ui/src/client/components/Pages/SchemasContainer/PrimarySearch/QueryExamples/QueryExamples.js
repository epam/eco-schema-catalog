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
import '../../../../../config/queryExamples.json';
import './QueryExamples.scss';


class QueryExamples extends Component {
  static propTypes = {
    queryExamples: PropTypes.object,
    onPick: PropTypes.func,
  }

  render() {
    const { queryExamples, onPick } = this.props;
    return (
      <div className="query-examples">
        <div className="query-examples-header">
          <h4>query examples:</h4>
        </div>
        <div className="query-examples-panel">
          {
            Object.entries(queryExamples).map((keyValue, rowIndex) => (
              <div
                key={keyValue[0]}
                className={`query-examples-row ${rowIndex % 2 !== 0 ? 'odd' : ' '}`}
                onClick={() => onPick(keyValue[1])}
                role="presentation"
              >
                <div className="query-examples-row-key">{keyValue[0]}</div>
                <div className="query-examples-row-value">{keyValue[1]}</div>
              </div>
            ))
          }
        </div>
      </div>
    );
  }
}

export default QueryExamples;
