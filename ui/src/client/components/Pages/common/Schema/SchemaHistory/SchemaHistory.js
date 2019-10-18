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

/* eslint-disable import/no-unresolved */
/* eslint-disable import/extensions */
import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { TabsContainer, Loader } from 'react-eco-ui';

import './SchemaHistory.scss';

class SchemaHistory extends PureComponent {
  static propTypes = {
    diff: PropTypes.array,
    subject: PropTypes.string,
    getHistory: PropTypes.func,
    isLoading: PropTypes.bool,
  }

  constructor(props) {
    super(props);
    this.state = { currentDiffIndex: 0 };
  }

  componentDidMount() {
    const { subject, getHistory } = this.props;
    getHistory(subject);
  }

  handleTabClick = (version) => {
    const { diff } = this.props;
    const i = version.slice(SchemaHistory.VERSION.length + 1);
    this.setState({ currentDiffIndex: diff.findIndex(item => item.parsedVersion === i) });
  }

  selectRowColor = (char) => {
    if (char === '-') {
      return 'raspberry-color';
    } if (char === '+') {
      return 'green-color';
    }
    return null;
  }

  static VERSION = 'version'

  render() {
    const { diff, isLoading } = this.props;
    const { currentDiffIndex } = this.state;

    if (diff.length === 0 || currentDiffIndex < 0) {
      return null;
    }

    if (isLoading) {
      return (<Loader type="spinner" color="lime-green" />);
    }

    const tabs = diff.map(item => `${SchemaHistory.VERSION} ${item.parsedVersion}`);

    return (
      <TabsContainer
        tabs={tabs}
        selectedTab={`${SchemaHistory.VERSION} ${diff[currentDiffIndex].parsedVersion}`}
        onTabClick={this.handleTabClick}
      >
        <div className="schema-history">
          {!diff[currentDiffIndex].diff.length && <span>no changes</span>}
          {
            diff[currentDiffIndex].diff.map((row, index) => (
              <span
                key={`${row + index}`}
                className={this.selectRowColor(row[0])}
              >
                {row}
              </span>
            ))
          }
        </div>
      </TabsContainer>
    );
  }
}

export default SchemaHistory;
