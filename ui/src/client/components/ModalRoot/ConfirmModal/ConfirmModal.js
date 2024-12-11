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

import './ConfirmModal.scss';

const {
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
} = modals;

class ConfirmModal extends Component {
  static propTypes = {
    header: PropTypes.string,
    message: PropTypes.string,
    buttonLabel: PropTypes.string,
    closeModal: PropTypes.func,
    callbackAsync: PropTypes.func,
  }

  state = {
    isDeleting: false,
  }

  render() {
    const { isDeleting } = this.state;
    const {
      message,
      closeModal,
      callbackAsync,
      header,
      buttonLabel
    } = this.props;
    return (
      <Modal {...this.props} className="confirm-modal">

        <ModalHeader
          header={header}
          onClose={closeModal}
        />

        <ModalBody>
          <div className="confirm-modal-message">
            {message}
          </div>
        </ModalBody>

        <ModalFooter>
          {isDeleting && <Loader type="spinner" color="lime-green" height={36} />}
          <Button
            className="red"
            name={buttonLabel}
            disable={isDeleting}
            onClick={() => {
              if (callbackAsync) {
                this.setState({ isDeleting: true });
                callbackAsync()
                  .finally(() => {
                    this.setState({ isDeleting: false });
                    closeModal();
                  });
              } else {
                closeModal();
              }
            }}
          />
          <Button
            name="Cancel"
            onClick={closeModal}
            disable={isDeleting}
          />
        </ModalFooter>
      </Modal>
    );
  }
}

export default ConfirmModal;
