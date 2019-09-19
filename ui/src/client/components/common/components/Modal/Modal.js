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
import './Modal.scss';

class Modal extends Component {
  static propTypes = {
    isShowModal: PropTypes.bool,
    children: PropTypes.oneOfType([PropTypes.array, PropTypes.node]),
    getChildRef: PropTypes.func,
    className: PropTypes.string,
    zIndex: PropTypes.number,
    backgroundColor: PropTypes.string,
  }

  blockOuterScroll = {

  }

  componentDidUpdate() {
    const { isShowModal } = this.props;
    if (isShowModal) {
      document.body.classList.add('block-outer-scroll');
    } else {
      document.body.classList.remove('block-outer-scroll');
    }
  }

  render() {
    const {
      isShowModal, children, getChildRef, className, zIndex = 1000, backgroundColor = '#2C2F3C',
    } = this.props;

    if (!isShowModal) {
      return null;
    }

    return (
      <div className="dh-modal-root" style={{ zIndex }}>
        <div className="modal-overlay" style={{ backgroundColor }} />
        <div className="modal-content-wrapper">
          <div
            className={className}
            ref={(ref) => { getChildRef(ref); }}
          >
            {children}
          </div>
        </div>
      </div>
    );
  }
}

export default Modal;
