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

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Metadata from '../../common/Metadata/Metadata';
import DeleteMetadataButton from '../../common/DeleteMetadataButton/DeleteMetadataButton';
import getCustomDate from '../../../../../../utils/getCustomDate/getCustomDate';
import './MetadataCell.scss';

class MetadataCell extends Component {
  static propTypes = {
    metadata: PropTypes.object,
    currentVersion: PropTypes.number,
    deleteMetadata: PropTypes.func,
  }

  render() {
    const { metadata, currentVersion, deleteMetadata } = this.props;
    const { updatedAt } = metadata === null ? {} : metadata.value;
    const originVersion = metadata === null ? currentVersion : metadata.key.version;
    return (
      <div className="metadata-cell">

        <Metadata {...this.props} />

        {updatedAt
          && (
          <div className="metadata-cell-info">
            <span>last update: </span>
            <span className="at">
              {getCustomDate(updatedAt)}
            </span>
            <DeleteMetadataButton
              deleteMetadata={deleteMetadata}
              originVersion={originVersion}
            />
          </div>
          )
        }

      </div>
    );
  }
}

export default MetadataCell;
