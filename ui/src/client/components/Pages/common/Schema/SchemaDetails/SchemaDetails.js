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
import JSONTree from 'react-json-tree';
import ReactTooltip from 'react-tooltip';
import { saveAs } from 'file-saver';
import { Loader, IconButton } from 'react-eco-ui';

import './SchemaDetails.scss';

class Details extends Component {
  static propTypes = {
    subject: PropTypes.string,
    version: PropTypes.number,
    getDetails: PropTypes.func,
    details: PropTypes.any,
    isLoading: PropTypes.bool,
  }

  componentDidMount() {
    const {
      subject, version, getDetails,
    } = this.props;
    getDetails(subject, version);
  }

  handleSave = () => {
    const { subject, version, details } = this.props;
    const blob = new Blob([JSON.stringify(details, null, '\t')], { type: 'application/json' });
    saveAs(blob, `${subject}[${version}].avsc`);
  }

  render() {
    const { details, isLoading } = this.props;

    if (isLoading) {
      return (<Loader type="spinner" color="lime-green" />);
    }

    const theme = {
      scheme: 'bright',
      author: 'chris kempson',
      base00: '#000000',
      base01: '#303030',
      base02: '#505050',
      base03: '#b0b0b0',
      base04: '#d0d0d0',
      base05: '#e0e0e0',
      base06: '#f5f5f5',
      base07: '#ffffff',
      base08: '#fb0120',
      base09: '#fc6d24',
      base0A: '#fda331',
      base0B: '#a1c659',
      base0C: '#76c7b7',
      base0D: '#6fb3d2',
      base0E: '#d381c3',
      base0F: '#be643c',
    };

    return (
      <div className="schema-details">
        <div className="schema-details-actions">
          <div
            data-tip="Download .avsc"
            data-for="schema-details"
          >
            <IconButton
              className="download-schema-button"
              onClick={this.handleSave}
            >
              <i className="fa fa-download" />
            </IconButton>
          </div>
        </div>

        <JSONTree
          data={details}
          theme={theme}
          invertTheme
        />

        <ReactTooltip id="schema-details" place="left" />
      </div>
    );
  }
}

export default Details;
