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
    version: PropTypes.number,
    getHistory: PropTypes.func,
    isLoading: PropTypes.bool,
  }

  static VERSION = 'version'

  constructor(props) {
    super(props);
    this.state = {
      currentVersion: `${SchemaHistory.VERSION} ${props.version}`,
      currentVersionIndex: props.version - 1,
    };
  }

  componentDidMount() {
    const { subject, getHistory } = this.props;
    getHistory(subject);
  }

  handleTabClick = (version) => {
    const selectedIndex = +version.slice(SchemaHistory.VERSION.length);
    this.setState({ currentVersion: version, currentVersionIndex: selectedIndex - 1 });
  }

  selectRowColor = (char) => {
    if (char === '-') {
      return 'raspberry-color';
    } if (char === '+') {
      return 'green-color';
    }
    return null;
  }

  render() {
    const { diff, isLoading } = this.props;
    const { currentVersion, currentVersionIndex } = this.state;

    if (isLoading || !diff.length || !diff[currentVersionIndex]) {
      return (<Loader type="spinner" color="lime-green" />);
    }

    const tabs = diff.map((_item, index) => `${SchemaHistory.VERSION} ${index + 1}`);

    return (
      <TabsContainer
        tabs={tabs}
        selectedTab={currentVersion}
        onTabClick={this.handleTabClick}
      >
        <div className="schema-history">
          {!diff[currentVersionIndex].length && <span>no changes</span>}
          {
            diff[currentVersionIndex].map((row, index) => (
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
