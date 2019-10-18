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
import { TableWithDetails, Loader } from 'react-eco-ui';
import Schema from '../../common/Schema';
import DeletedIcon from '../../common/DeletedIcon/DeletedIcon';
import parseSearchString from '../../../../utils/parseSearchString/parseSearchString';
import './Schemas.scss';

class Schemas extends Component {
  static propTypes = {
    schemas: PropTypes.array,
    totalElements: PropTypes.number,
    limitedTotalElements: PropTypes.number,
    subject: PropTypes.string,
    version: PropTypes.number,
    applySearchParams: PropTypes.func,
    getSchemas: PropTypes.func,
    selectSchema: PropTypes.func,
    expandeSchema: PropTypes.func,
    closeSchema: PropTypes.func,
    isLoading: PropTypes.bool,
    isExeedingMaxResult: PropTypes.bool,
    location: PropTypes.object,
  }

  constructor(props) {
    super(props);
    this.tableHeader = [
      { key: 'subject', weight: 3, alias: 'Subject' },
      { key: 'version', weight: 1, alias: 'Version' },
      { key: 'name', weight: 4, alias: 'Name' },
      { key: 'namespace', weight: 3, alias: 'Namespace' },
      { key: 'deleted', weight: 0.5, alias: ' ' },
    ];
  }

  componentDidMount() {
    const {
      schemas,
      location,
      getSchemas,
      applySearchParams,
    } = this.props;
    const params = parseSearchString(location.search);
    if (!schemas.length) {
      applySearchParams(params);
      getSchemas();
    }
  }

  handleSelectSchema = (schemasIndex) => {
    const {
      schemas,
      selectSchema,
      subject,
      version,
    } = this.props;
    const newSubject = schemas[schemasIndex].subject;
    const newVersion = schemas[schemasIndex].version;
    if (subject !== newSubject || version !== newVersion) {
      selectSchema(newSubject, newVersion);
    }
  }

  handleExpandeDetails = () => {
    const { expandeSchema } = this.props;
    expandeSchema(true);
  }

  hadleCloseSchema = () => {
    const { closeSchema } = this.props;
    closeSchema();
  }

  mutateSchemas = (schemas = []) => schemas.map((schema) => {
    if (schema.deleted) {
      const mutateSchema = schema;
      mutateSchema.deleted = <DeletedIcon />;
      return mutateSchema;
    }
    return schema;
  })

  render() {
    const {
      schemas = [],
      totalElements,
      limitedTotalElements,
      subject,
      version,
      isLoading,
      isExeedingMaxResult,
    } = this.props;
    return (
      <div className="schemas">

        {isExeedingMaxResult
          ? (
            <React.Fragment>
              <p>
                <span>Found Elements: </span>
                <span className="number">{totalElements}</span>
                <span className="warning"> (result is limited to </span>
                <span className="number">{limitedTotalElements}</span>
                <span className="warning"> elements returned, please, narrow your search) </span>
              </p>
            </React.Fragment>
          )
          : (
            <p>
              <span>Found Elements: </span>
              <span>{limitedTotalElements}</span>
            </p>
          )
        }

        <div className="schemas-table">

          <TableWithDetails
            key={subject}
            headers={this.tableHeader}
            body={this.mutateSchemas(schemas)}
            onRowClick={this.handleSelectSchema}
            onCloseDetails={this.hadleCloseSchema}
            onExpandeDetails={this.handleExpandeDetails}
            selectedRowIndex={schemas.findIndex(schema => (
              schema.subject === subject && schema.version === version))
            }
            hoverableRow
            isStripes
          >
            {subject ? (<Schema />) : null}
          </TableWithDetails>

          {isLoading
            && (
            <React.Fragment>
              <div className="backgroung" />
              <div className="loader">
                <Loader type="spinner" color="lime-green" />
              </div>
            </React.Fragment>
            )
          }
        </div>

      </div>
    );
  }
}

export default Schemas;
