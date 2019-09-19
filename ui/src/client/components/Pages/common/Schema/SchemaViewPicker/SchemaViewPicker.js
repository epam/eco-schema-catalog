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
import { TABLE, HISTORY, DETAILS } from '../../../../../consts/consts';
import './SchemaViewPicker.scss';

class SchemaViewPicker extends Component {
  static propTypes = {
    view: PropTypes.string,
    changeSchemaView: PropTypes.func,
  }

  views = [
    { key: TABLE, name: 'table view' },
    { key: DETAILS, name: 'json' },
    { key: HISTORY, name: 'version history' },
  ];

  handleClick = (currentView) => {
    const { view, changeSchemaView } = this.props;
    if (currentView !== view) {
      changeSchemaView(currentView);
    }
  }

  render() {
    const { view } = this.props;
    return (
      <div className="schema-view-picker">
        {
          this.views.map(viewItem => (
            <button
              key={viewItem.key}
              className={viewItem.key === view ? 'current-view' : ''}
              onClick={() => this.handleClick(viewItem.key)}
              type="button"
            >
              {viewItem.name}
            </button>
          ))
        }
      </div>
    );
  }
}

export default SchemaViewPicker;
