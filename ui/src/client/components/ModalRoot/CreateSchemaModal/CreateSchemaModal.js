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
import {
  Button, modals, Loader, Input,
} from 'react-eco-ui';
import { postSchema, testSchemaCompitability } from '../../../services/httpService';
import SchemaEditor from '../common/SchemaEditor/SchemaEditor';
import TestResult from '../common/TestResult/TestResult';

import './CreateSchemaModal.scss';

const {
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
} = modals;

class CreateSchemaModal extends Component {
  initialState = {
    isLoading: false,
    createdVersion: null,
    testErrors: [],
    tested: false,
    error: '',
  }

  constructor(props) {
    super(props);
    this.state = Object.assign({}, this.initialState, { subject: '', avro: '' });
  }

  onPostSchema = () => {
    const { subject, avro } = this.state;
    const { handlePostSchema, closeModal } = this.props;
    this.setState(Object.assign({}, this.initialState, { isLoading: true }));
    postSchema(subject, avro)
      .then((res) => {
        this.setState({ error: '', createdVersion: res.version });
        handlePostSchema(res.version);
        closeModal();
      })
      .catch((error) => {
        this.setState({ error });
      })
      .finally(() => {
        this.setState({ isLoading: false });
      });
  }

  onTest = () => {
    const { subject, avro } = this.state;
    this.setState(Object.assign({}, this.initialState, { isLoading: true }));
    testSchemaCompitability(subject, avro)
      .then((res) => {
        this.setState({ error: '', testErrors: res.errors, tested: true });
      })
      .catch((error) => {
        debugger;
        this.setState({ error });
      })
      .finally(() => {
        this.setState({ isLoading: false });
      });
  }

  render() {
    const {
      isLoading,
      subject,
      avro,
      createdVersion,
      testErrors,
      error,
      tested,
    } = this.state;
    const { closeModal } = this.props;
    return (
      <Modal {...this.props} className="create-schema-modal">

        <ModalHeader
          header="Create new schema"
          onClose={closeModal}
        />

        <ModalBody>

          <div className="create-schema-modal-body">

            <div>
              <span>Subject:</span>
            </div>

            <div className="subject-input">
              <Input
                className="input"
                value={subject}
                onChange={value => this.setState({ subject: value })}
                isError={!subject}
              />
            </div>

            <div>
              <span>Avro Schema:</span>
            </div>

            <SchemaEditor
              avro={avro}
              onChangeValue={newValue => this.setState({ avro: newValue })}
            />

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

            {createdVersion
              ? (
                <div className="version">
                  <span>Created version: </span>
                  <span>{createdVersion}</span>
                </div>
              ) : null
            }

          </div>

        </ModalBody>

        <ModalFooter>
          {isLoading && <Loader type="spinner" color="lime-green" height={36} />}
          <Button
            className="green"
            name="Test"
            disable={isLoading || !subject || !avro}
            onClick={() => this.onTest()}
          />
          <Button
            className="green"
            name="Create"
            disable={isLoading || !subject || !avro}
            onClick={() => this.onPostSchema()}
          />
          <Button
            name="Cancel"
            onClick={closeModal}
            disable={isLoading}
          />
        </ModalFooter>
      </Modal>
    );
  }
}

CreateSchemaModal.propTypes = {
  closeModal: PropTypes.func,
  handlePostSchema: PropTypes.func,
};

export default CreateSchemaModal;
