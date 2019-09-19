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
import { Button } from 'react-eco-ui';
import ReactTooltip from 'react-tooltip';
import { TABLE } from '../../../../../consts/consts';

import './SchemaAddVersionButton.scss';

export default class SchemaAddVersionButton extends Component {
  static propTypes = {
    view: PropTypes.string,
    versionLatest: PropTypes.bool,
    showTestSchemaModal: PropTypes.func,
  }

  render() {
    const { view, versionLatest, showTestSchemaModal } = this.props;
    if (view !== TABLE) {
      return null;
    }
    return (
      <React.Fragment>
        <div
          data-tip="availble only for the last schema version"
          data-for="add-schema"
        >
          <Button
            className="green transparent add-version-button"
            name="Add version"
            onClick={showTestSchemaModal}
            disable={!versionLatest}
          />
        </div>
        <ReactTooltip id="add-schema" place="top" />
      </React.Fragment>
    );
  }
}
