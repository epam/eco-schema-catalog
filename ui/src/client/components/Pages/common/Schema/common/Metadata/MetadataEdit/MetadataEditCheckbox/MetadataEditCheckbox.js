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
/* eslint-disable jsx-a11y/label-has-associated-control */
/* eslint-disable import/no-unresolved */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { nanoid } from 'nanoid';
import { Checkbox } from 'react-eco-ui';
import './MetadataEditCheckbox.scss';

class MetadataEditCheckbox extends Component {
  static propTypes = {
    isChecked: PropTypes.bool,
    handleCheck: PropTypes.func,
  }

  id = nanoid()

  render() {
    const { handleCheck, isChecked } = this.props;
    return (
      <div className="metadata-edit-checkbox">
        <Checkbox
          id={this.id}
          onCheck={handleCheck}
          isChecked={isChecked}
        />
        <label htmlFor={this.id}>override version</label>
      </div>
    );
  }
}

export default MetadataEditCheckbox;
