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
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Loader } from 'react-eco-ui';
import { getDetails } from '../../../../services/httpService';
import SchemaEditor from '../../common/SchemaEditor/SchemaEditor';
import TestResult from '../../common/TestResult/TestResult';

import './TestSchemaModalBody.scss';

export default class TestSchemaModalBody extends Component {
  static propTypes = {
    subject: PropTypes.string,
    version: PropTypes.number,
    compatibilityLevel: PropTypes.string,
    avro: PropTypes.string,
    testErrors: PropTypes.array,
    error: PropTypes.object,
    tested: PropTypes.bool,
    onChangeAvro: PropTypes.func,
    onGetError: PropTypes.func,
  }

  state = {
    isLoading: false,
  }

  async componentDidMount() {
    const {
      subject,
      version,
      onChangeAvro,
      onGetError,
    } = this.props;
    try {
      this.setState({ isLoading: true });
      const res = await getDetails(subject, version);
      this.setState({ isLoading: false });
      onChangeAvro(JSON.stringify(res, null, 2));
    } catch (error) {
      onGetError(error);
    }
  }

  render() {
    const {
      subject,
      compatibilityLevel,
      avro,
      testErrors,
      error,
      tested,
      onChangeAvro,
    } = this.props;

    const { isLoading } = this.state;

    return (
      <div className="test-schema-modal-body">

        <div className="field">
          <span>Subject:</span>
          <span className="field-value">{subject}</span>
        </div>

        <div className="field">
          <span>Compatibility Level:</span>
          <span className="field-value">{compatibilityLevel}</span>
        </div>

        <div className="field">
          <span>Avro Schema:</span>
        </div>

        <div>
          {isLoading && <Loader type="spinner" color="lime-green" height={36} />}
          {!isLoading
            && (
            <SchemaEditor
              avro={avro}
              onChangeValue={onChangeAvro}
            />
            )
          }
        </div>

        <TestResult
          tested={tested}
          testErrors={testErrors}
        />

        {error
          ? (
            <div className="error">
              <span>{error.toString()}</span>
            </div>
          ) : null
        }

      </div>
    );
  }
}
