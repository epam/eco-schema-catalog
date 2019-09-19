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
import { Button, modals, Loader } from 'react-eco-ui';
import { postSchema, testSchemaCompitability } from '../../../services/httpService';
import TestSchemaModalBody from './TestSchemaModalBody/TestSchemaModalBody';

import './TestSchemaModal.scss';

const {
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
} = modals;

class TestSchemaModal extends Component {
  initialState = {
    isLoading: false,
    tested: false,
    error: null,
    testErrors: [],
  }

  constructor(props) {
    super(props);
    this.state = Object.assign({}, this.initialState, { avro: '' });
  }

  onPostSchema = () => {
    const { subject, closeModal, handlePostSchema } = this.props;
    const { avro } = this.state;
    this.setState(Object.assign({}, this.initialState, { isLoading: true }));
    postSchema(subject, avro)
      .then((res) => {
        this.setState({ error: '' });
        handlePostSchema(subject, res.version);
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
    const { avro } = this.state;
    const { subject } = this.props;
    this.setState(Object.assign({}, this.initialState, { isLoading: true }));
    testSchemaCompitability(subject, avro)
      .then((res) => {
        this.setState({ error: '', testErrors: res.errors, tested: true });
      })
      .catch((error) => {
        this.setState({ error, tested: false });
      })
      .finally(() => {
        this.setState({ isLoading: false });
      });
  }

  render() {
    const { isLoading, avro } = this.state;
    const { closeModal } = this.props;
    return (
      <Modal {...this.props} className="test-schema-modal">

        <ModalHeader
          header="Test schema compatibility"
          onClose={closeModal}
        />

        <ModalBody>
          <TestSchemaModalBody
            {...this.state}
            {...this.props}
            onChangeAvro={newValue => this.setState({ avro: newValue })}
            onGetError={newValue => this.setState({ error: newValue })}
          />
        </ModalBody>

        <ModalFooter>
          {isLoading && <Loader type="spinner" color="lime-green" height={36} />}
          <Button
            className="green"
            name="Test"
            disable={isLoading || !avro}
            onClick={() => this.onTest()}
          />
          <Button
            className="green"
            name="Create"
            disable={isLoading || !avro}
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

TestSchemaModal.propTypes = {
  isShowModal: PropTypes.bool,
  subject: PropTypes.string,
  version: PropTypes.number,
  closeModal: PropTypes.func,
  getDetails: PropTypes.func,
  handlePostSchema: PropTypes.func,
};

export default TestSchemaModal;
