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
import IconButton from '../IconButton/IconButton';
import closeIcon from '../../assets/close_white.svg';
import './AlertPopUp.scss';

class AlertPopUp extends Component {
  static propTypes = {
    resetMessages: PropTypes.func,
    error: PropTypes.shape({
      message: PropTypes.string,
    }),
    success: PropTypes.shape({
      message: PropTypes.string,
    }),
  }

  handleCloseAlert = () => {
    const { resetMessages } = this.props;
    resetMessages();
  }

  render() {
    const { error, success } = this.props;
    let message = '';
    let color = '';
    if (error) {
      message = error.message;
      color = 'raspberry';
    } else if (success) {
      setTimeout(this.handleCloseAlert, 5000);
      message = success.message || 'success';
      color = 'green';
    }

    return (
      <div>
        {(error || success) && (
          <div
            className={`alert-wrapper ${color}`}
            role="alert"
          >
            <div className="alert-message">
              {error && <i className="fa fa-exclamation-triangle" />}
              <span>
                { message }
              </span>
            </div>
            <div className="alert-close">
              <IconButton
                className="close-button"
                onClick={this.handleCloseAlert}
              >
                <img src={closeIcon} alt="" />
              </IconButton>
            </div>
          </div>
        )
        }
      </div>
    );
  }
}

export default AlertPopUp;
