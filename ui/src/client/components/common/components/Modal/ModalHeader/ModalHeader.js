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
import IconButton from '../../IconButton/IconButton';
import closeIcon from '../../../assets/close.svg';

import './ModalHeader.scss';

class ModalHeader extends Component {
  static propTypes = {
    header: PropTypes.string,
    onClose: PropTypes.func,
  }

  render() {
    const { header, onClose } = this.props;
    return (
      <div className="modal-header">
        <h3>{header}</h3>
        <IconButton
          className="close-button"
          onClick={onClose}
        >
          <img src={closeIcon} alt="" />
        </IconButton>
      </div>
    );
  }
}

export default ModalHeader;
