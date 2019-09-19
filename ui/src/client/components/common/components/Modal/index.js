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
import Modal from './Modal';

function withOutsideClick(WrappedComponent) {
  return class extends Component {
    static propTypes = {
      isShowModal: PropTypes.bool,
      closeModal: PropTypes.func,
    }

    componentDidMount() {
      document.addEventListener('mousedown', this.handleOutsideClick);
    }

    componentWillUnmount() {
      document.removeEventListener('mousedown', this.handleOutsideClick);
    }

    getChildRef = (ref) => {
      this.ref = ref;
    }

    handleOutsideClick = (e) => {
      if (this.ref) {
        const { isShowModal, closeModal } = this.props;
        if (!this.ref.contains(e.target) && isShowModal) {
          closeModal();
        }
      }
    }

    render() {
      return (
        <WrappedComponent
          {...this.props}
          getChildRef={this.getChildRef}
        />
      );
    }
  };
}

export default withOutsideClick(Modal);
