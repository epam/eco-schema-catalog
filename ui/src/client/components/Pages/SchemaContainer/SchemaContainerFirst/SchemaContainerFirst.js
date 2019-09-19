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
import { IconButton } from 'react-eco-ui';
import closeIcon from '../../../../assets/close.svg';
import Schema from '../../common/Schema';
import './SchemaContainerFirst.scss';
import '../SchemaContainer.scss';

class SchemaContainerFirst extends Component {
  static propTypes = {
    selectSchema: PropTypes.func,
    getLatestVersion: PropTypes.func,
    closeSchema: PropTypes.func,
    match: PropTypes.object,
    history: PropTypes.object,
  }

  componentDidMount() {
    const { getLatestVersion, selectSchema, match } = this.props;
    const { subject, version } = match.params;
    if (!version || Number.isNaN(+version)) {
      // to open last version
      getLatestVersion(subject)
        .then((latestVersion) => {
          selectSchema(subject, +latestVersion);
        });
    } else {
      selectSchema(subject, +version);
    }
  }

  handleBackToSchemas = () => {
    const { closeSchema, history } = this.props;
    history.push('/');
    closeSchema();
  }

  render() {
    return (
      <React.Fragment>
        <div className="only-schema-background" />

        <div className="only-schema-container">

          <div className="expanded-actions">
            <IconButton
              className="close-button"
              onClick={this.handleBackToSchemas}
            >
              <img src={closeIcon} alt="" />
            </IconButton>
          </div>

          <Schema />

        </div>

      </React.Fragment>
    );
  }
}

export default SchemaContainerFirst;
