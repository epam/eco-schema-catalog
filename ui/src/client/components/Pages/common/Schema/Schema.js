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
import { Button } from 'react-eco-ui';
import ReactTooltip from 'react-tooltip';
import { TABLE, DETAILS, HISTORY } from '../../../../consts/consts';
import DeletedIcon from '../DeletedIcon/DeletedIcon';
import VersionSelector from './VersionSelector';
import Metadata from './SchemaMetadata';
import DeleteMetadataButton from './common/DeleteMetadataButton/DeleteMetadataButton';
import SchemaViewPicker from './SchemaViewPicker';
import SchemaFilterByNameInput from './SchemaFilterByNameInput';
import SchemaTable from './SchemaTable';
import SchemaDetails from './SchemaDetails';
import SchemaHistory from './SchemaHistory';
import SchemaAddVersionButton from './SchemaAddVersionButton';
import SchemaCompatibilitySelector from './SchemaCompatibilitySelector';
import './Schema.scss';
import SchemaCompatibilityTypeSelector from "./SchemaCompatibilityTypeSelector";
import AuthService from "../../../../services/authService/authService";

class Schema extends PureComponent {
  static propTypes = {
    view: PropTypes.string,
    subject: PropTypes.string,
    version: PropTypes.number,
    mode: PropTypes.string,
    isDeleted: PropTypes.bool,
    updatedAt: PropTypes.string,
    updatedBy: PropTypes.string,
    originMetadataVersion: PropTypes.number,
    deleteMetadata: PropTypes.func,
    deleteSchema: PropTypes.func,
    deleteSchemas: PropTypes.func,
    resetCompatibility: PropTypes.func,
    globalCompatibilityLevel: PropTypes.bool,
    schemaRegistryId: PropTypes.number
  }

  constructor(props) {
    super(props);
    this.views = {
      [TABLE]: SchemaTable,
      [DETAILS]: SchemaDetails,
      [HISTORY]: SchemaHistory,
    };
    this.isAdmin = AuthService.getInstance(process.env.AUTH_PROVIDER).isAdmin();
  }

  render() {
    const {
      view,
      subject,
      version,
      isDeleted,
      mode,
      deleteSchema,
      deleteSchemas,
      updatedBy,
      updatedAt,
      originMetadataVersion,
      deleteMetadata,
      resetCompatibility,
      globalCompatibilityLevel,
      schemaRegistryId,
    } = this.props;
    if (!subject) {
      return null;
    }
    const SchemaView = this.views[view];
    return (
      <div className="schema">

        <div className="schema-row">
          <p>Subject: </p>
          <div className="actions">
            <h3 className="subject">{subject}</h3>
          </div>
        </div>
        <div className="schema-row">
          <p>Compatibility level: </p>

          <div className="actions">

             <SchemaCompatibilityTypeSelector/>
             {this.isAdmin && (
                     <React.Fragment>
                       <div>Set to:</div>
                       <SchemaCompatibilitySelector/>
                       <div
                           data-tip="reset compatibility level to inherited from cluster defaults value"
                           data-for="reset-compatibility"
                       >
                         <Button
                             className="green transparent reset-button"
                             name="Reset to default"
                             onClick={() => resetCompatibility()}
                             disable={globalCompatibilityLevel}
                             transparent
                         />
                       </div>
                       <ReactTooltip id="reset-compatibility" place="top"/>
                     </React.Fragment>
                 )
             }
           </div>
        </div>

        <div className="schema-row">
          <p>Version: </p>
          <div className="actions">
            <VersionSelector key={subject} />
            <SchemaAddVersionButton view={view} />
            {isDeleted
              ? <DeletedIcon />
              : (
                    <React.Fragment>
                      <div
                          data-tip="delete current version of the subject"
                          data-for="delete-schemas"
                      >
                        <Button
                            className="red delete-button"
                            name="Delete"
                            onClick={() => deleteSchema()}
                            transparent
                        />
                      </div>
                      <ReactTooltip id="delete-schemas" place="top"/>
                      <div
                          data-tip="delete all versions of the subject"
                          data-for="delete-schemas"
                      >
                        <Button
                            className="red delete-button"
                            name="Delete all"
                            onClick={() => deleteSchemas()}
                            transparent
                        />
                      </div>
                      <ReactTooltip id="delete-schemas" place="top"/>
                    </React.Fragment>
                )
            }
          </div>
        </div>

        <div className="schema-row">
          <p>Mode: </p>
          <div className="actions">
            <span className="mode">{mode}</span>
          </div>
        </div>

        <div className="schema-row">
           <p>Schema Id: </p>
            <div className="actions">
                <span className="schema-id">{schemaRegistryId}</span>
            </div>
        </div>

        <div className="schema-metadata">

          <div className="schema-metadata-row">
            <p className="description">Description: </p>
            <div className="content">
            <Metadata
                key={subject + version}
              />
            </div>
          </div>

          {updatedBy
            && (
            <div className="schema-metadata-row">
              <p>Updated by: </p>
              <div className="content">
                <span className="value by">{updatedBy}</span>
              </div>
            </div>
            )
          }

          {updatedAt
            && (
            <div className="schema-metadata-row">
              <p>Last update: </p>
              <div className="content">
                <span className="value at">{updatedAt}</span>
                <DeleteMetadataButton
                  originVersion={originMetadataVersion}
                  deleteMetadata={deleteMetadata}
                />
              </div>
            </div>
            )
          }
        </div>

        <div className="schema-actions">
          {view === TABLE && <SchemaFilterByNameInput key={subject + version} /> }
          <SchemaViewPicker />
        </div>

        <div className="schema-view">
          <SchemaView key={subject + version} />
        </div>

      </div>
    );
  }
}

export default Schema;
