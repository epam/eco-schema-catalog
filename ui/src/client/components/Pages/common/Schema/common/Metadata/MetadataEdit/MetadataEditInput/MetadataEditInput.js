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
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Input } from 'react-eco-ui';
import Tags from './Tags';
import './MetadataEditInput.scss';

class MetadataEditInput extends Component {
  static propTypes = {
    value: PropTypes.string,
    handleChange: PropTypes.func,
  }

  state = {
    inputValue: this.props.value,
  }

  handleChange = (value) => {
    const { handleChange } = this.props;
    handleChange(value);
    this.setState({ inputValue: value });
  }

  render() {
    const { inputValue } = this.state;
    return (
      <div>
        <Tags
          tagClick={tagTempate => this.handleChange(inputValue + tagTempate)}
        />
        <Input
          isLongText
          onChange={this.handleChange}
          value={inputValue}
        />
      </div>
    );
  }
}

export default MetadataEditInput;
