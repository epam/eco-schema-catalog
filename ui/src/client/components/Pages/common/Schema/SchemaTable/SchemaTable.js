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
import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Loader } from 'react-eco-ui';
import ReactTooltip from 'react-tooltip';

import TypeHeaderCell from './TypeSelector';
import TypeCell from './TypeCell/TypeCell';
import SchemaNameCell from './SchemaNameCell/SchemaNameCell';
import NativeDocCell from './NativeDocCell/NativeDocCell';
import DefaultValueCell from './DefaultValueCell/DefaultValueCell';
import MetadataCell from './MetadataCell/index';

import './SchemaTable.scss';
import FieldNameCell from './FieldNameCell/FieldNameCell';

class SchemaTable extends PureComponent {
  static propTypes = {
    subject: PropTypes.string,
    version: PropTypes.number,
    rows: PropTypes.array,
    getSchemaAsync: PropTypes.func,
    isLoading: PropTypes.bool,
    isHaveUnsaved: PropTypes.bool,
  };

  constructor(props) {
    super(props);
    this.tableHeader = [
      {
        key: 'schemaName', alias: 'Schema name', cellComponent: SchemaNameCell,
      },
      {
        key: 'name', alias: 'Field name', cellComponent: FieldNameCell,
      },
      {
        key: 'fullName', alias: 'Type', customHeaderCell: TypeHeaderCell, cellComponent: TypeCell,
      },
      {
        key: 'nativeDoc', alias: 'Native doc', cellComponent: NativeDocCell,
      },
      {
        key: 'defaultValue', alias: 'Default value', cellComponent: DefaultValueCell,
      },
      {
        key: 'metadata', alias: 'Description', cellComponent: MetadataCell,
      },
    ];
  }

  componentDidMount() {
    const {
      subject, version, getSchemaAsync,
    } = this.props;
    getSchemaAsync(subject, version).finally(() => ReactTooltip.rebuild());
  }

  getHeaderCell = (customHeaderCell, name) => {
    if (!customHeaderCell) {
      return name;
    }
    const CustomHeaderCell = customHeaderCell;
    return <CustomHeaderCell name={name} />;
  }

  render() {
    const { rows, isLoading } = this.props;

    if (isLoading) {
      return (<Loader type="spinner" color="lime-green" />);
    }

    return (
      <div className="schema-table">
        <ReactTooltip id="schema-table-tooltip" place="top" />
        <div className="table">

          {
            this.tableHeader.map(item => (
              <div key={item.key} className="cell header-cell">
                {this.getHeaderCell(item.customHeaderCell, item.alias)}
              </div>
            ))
          }

          {
            rows.map((row, index) => this.tableHeader.map((header) => {
              const Cell = header.cellComponent;
              return (
                <div
                  key={`${header.key}${index.toString()}`}
                  className="cell"
                >
                  {Cell
                    ? <Cell {...row} />
                    : row[header.key]
                  }

                </div>
              );
            })).reduce((acc, arr) => acc.concat(arr), [])
          }

        </div>
      </div>
    );
  }
}

export default SchemaTable;
