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
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Selector, Loader } from 'react-eco-ui';
import ReactTooltip from 'react-tooltip';
import selectorArrow from '../../SelectorArrow/SelectorArrow';

export default class SchemaCompatibilitySelector extends Component {
  static propTypes = {
    levels: PropTypes.array,
    level: PropTypes.string,
    getCompitabilityLevels: PropTypes.func,
    setCompatibilityLevel: PropTypes.func,
  }

  state = {
    isLoading: false,
    isOpen: false,
  }

  componentDidMount() {
    const { levels, getCompitabilityLevels } = this.props;
    if (!levels.length) {
      this.setState({ isLoading: true });
      getCompitabilityLevels().finally(() => this.setState({ isLoading: false }));
    }
  }

  handleSelectLevel = (newLevel) => {
    const { setCompatibilityLevel } = this.props;
    setCompatibilityLevel(newLevel);
  }

  render() {
    const { levels, level, setCompatibilityLevel } = this.props;
    const { isLoading, isOpen } = this.state;
    return (
      <React.Fragment>
        <div
          data-tip={!isOpen ? 'set compatibility level' : ''}
          data-for="select-compatibility-levels"
        >
          {isLoading && <Loader width={26} height={26} type="spinner" color="lime-green" />}
          {!isLoading
            && (
            <Selector
              buttonHeight={26}
              options={levels}
              selectedOption={level}
              onOptionChange={setCompatibilityLevel}
              arrowComponent={selectorArrow}
              handleIsOpen={value => this.setState({ isOpen: value })}
            />
            )
          }
        </div>
        <ReactTooltip
          id="select-compatibility-levels"
          place="top"
        >
          {}
        </ReactTooltip>
      </React.Fragment>
    );
  }
}
