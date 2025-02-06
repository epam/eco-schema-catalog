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
import { Loader } from 'react-eco-ui';
import MetadataEdit from './MetadataEdit/MetadataEdit';
import './Metadata.scss';

class Metadata extends Component {
  static propTypes = {
    metadata: PropTypes.shape({
      key: PropTypes.shape({
        subject: PropTypes.string,
        version: PropTypes.number,
        schemaFullName: PropTypes.string,
        field: PropTypes.string,
        type: PropTypes.string,
      }),
      value: PropTypes.shape({
        doc: PropTypes.string,
        formattedDoc: PropTypes.string,
        updatedAt: PropTypes.string,
      }),
    }),
    isSaved: PropTypes.bool.isRequired,
    updateMetadaAsync: PropTypes.func.isRequired,
    saveMetadaAsync: PropTypes.func.isRequired,
    currentVersion: PropTypes.number,
  }

  state = {
    isLoading: false,
  }

  handleIsLoading = (isLoading) => {
    this.setState({ isLoading });
  }

  render() {
    // metadata could be null or an object like in propTypes
    const {
      metadata,
      isSaved,
      updateMetadaAsync,
      saveMetadaAsync,
      currentVersion,
    } = this.props;
    const { isLoading } = this.state;
    const doc = metadata === null ? '' : metadata.value.doc;
    const formattedDoc = metadata === null ? '' : metadata.value.formattedDoc;
    const originVersion = metadata === null ? currentVersion : metadata.key.version;
    return (
      <div className="metadata">
        {isLoading
          && (
            <div className="metadata-loading">
              <div className="metadata-loading-backgroung" />
              <Loader type="spinner" color="lime-green" />
            </div>
          )
        }
        <MetadataEdit
          doc={doc}
          formattedDoc={formattedDoc}
          isSaved={isSaved}
          currentVersion={currentVersion}
          originVersion={originVersion}
          updateMetada={updateMetadaAsync}
          saveMetada={saveMetadaAsync}
          handleLoading={this.handleIsLoading}
        />
      </div>
    );
  }
}

export default Metadata;
