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
import Classnames from 'classnames';
import { DropDownButton } from 'react-eco-ui';

import './SchemaDeleteActions.scss';

export default class SchemaDeleteActions extends Component {
  static propTypes = {
    deleteSchema: PropTypes.func,
    deleteSchemas: PropTypes.func,
  }

  render() {
    const { deleteSchema, deleteSchemas } = this.props;
    return (
      <div className="schema-delete-actions">
        <DropDownButton
          className="delete-menu"
          menuName="Delete"
          arrowComponent={props => (
            <i className={Classnames(
              'fa',
              { 'fa-chevron-down': !props.isOpen },
              { 'fa-chevron-up': props.isOpen },
            )}
            />
          )
          }
        >
          <div
            className="delete-menu-item"
            onClick={() => deleteSchema()}
            role="button"
            tabIndex={0}
          >
            delete current version
          </div>
          <div
            className="delete-menu-item"
            onClick={() => deleteSchemas()}
            role="button"
            tabIndex={0}
          >
            delete all verions
          </div>
        </DropDownButton>
      </div>
    );
  }
}
