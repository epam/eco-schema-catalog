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
import collapseIcon from '../../../../assets/collapse.svg';
import closeIcon from '../../../../assets/close.svg';
import Schema from '../../common/Schema';
import './SchemaContainerExpanded.scss';

class SchemaContainerExpanded extends Component {
  static propTypes = {
    closeSchema: PropTypes.func,
    collapseSchema: PropTypes.func,
  }

  handleCloseSchema = () => {
    const { closeSchema } = this.props;
    closeSchema();
  }

  handleExpande = () => {
    const { collapseSchema } = this.props;
    collapseSchema();
  }

  render() {
    return (
      <React.Fragment>
        <div className="only-schema-background" />
        <div className="only-schema-container">
          <div className="expanded-actions">
            <IconButton
              onClick={this.handleExpande}
            >
              <img src={collapseIcon} alt="" />
            </IconButton>

            <IconButton
              className="close-button"
              onClick={this.handleCloseSchema}
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

export default SchemaContainerExpanded;
