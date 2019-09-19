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
import { Selector, Loader } from 'react-eco-ui';
import VersionSelectorOptions from './VersionSelectorOptions/VersionSelectorOptions';
import selectorArrow from '../../SelectorArrow/SelectorArrow';
import './VersionSelector.scss';

class VersionSelector extends PureComponent {
  static propTypes = {
    subject: PropTypes.string,
    version: PropTypes.number,
    selectSchema: PropTypes.func,
    getLatestVersion: PropTypes.func,
  }

  constructor(props) {
    super(props);
    this.state = {
      isLoading: false,
      latestVersion: null,
    };
  }

  async componentDidMount() {
    const { getLatestVersion, subject } = this.props;
    this.setState({ isLoading: true });
    try {
      const latestVersion = await getLatestVersion(subject);
      this.setState({ latestVersion, isLoading: false });
    } catch (_error) {
      this.setState({ isLoading: false });
    }
  }

  getVersionsStack = (latestVersion) => {
    const versions = [];
    for (let index = 1; index <= latestVersion; index++) {
      versions.push(index.toString());
    }
    return versions;
  }

  handleSelectVersion = (currentVersion) => {
    const { selectSchema, subject, version } = this.props;
    if (version === +currentVersion) {
      return;
    }
    selectSchema(subject, +currentVersion);
  }

  render() {
    const { version } = this.props;
    const { latestVersion, isLoading } = this.state;
    if (Number.isNaN(version)) {
      return null;
    }
    return (
      <div className="version-selector">
        {isLoading && <Loader width={26} height={26} type="spinner" color="lime-green" />}
        {!isLoading
          && (
            <Selector
              // className="version-selector"
              buttonHeight={26}
              // buttonClassName="version-selector-button"
              options={this.getVersionsStack(latestVersion)}
              selectedOption={version.toString()}
              onOptionChange={this.handleSelectVersion}
              arrowComponent={selectorArrow}
              optionsComponent={VersionSelectorOptions}
              offsetOptions={0}
            />
          )
        }
      </div>
    );
  }
}

export default VersionSelector;
