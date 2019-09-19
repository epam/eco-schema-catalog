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
import './NativeDocCell.scss';

class NativeDocCell extends Component {
  static propTypes = {
    nativeDoc: PropTypes.string,
  }

  constructor(props) {
    super(props);
    this.state = {
      showNativeDoc: false,
    };
  }

  componentDidMount() {
    document.addEventListener('mousedown', this.handleOutsideClick);
  }

  componentWillUnmount() {
    document.removeEventListener('mousedown', this.handleOutsideClick);
  }

  handleOutsideClick = (e) => {
    if (this.nativeDocContainer) {
      if (!this.nativeDocContainer.contains(e.target)) {
        this.setState({ showNativeDoc: false });
      }
    }
  }

  handleShowNativeDoc = () => {
    const { showNativeDoc } = this.state;
    this.setState({ showNativeDoc: !showNativeDoc });
  }

  render() {
    const { showNativeDoc } = this.state;
    const { nativeDoc } = this.props;
    if (nativeDoc === null) {
      return 'none';
    }
    return (
      <div className="native-doc-wrapper">
        <button
          className="native-doc-btn dark-green-color"
          onClick={this.handleShowNativeDoc}
          ref={(ref) => { this.queriesButton = ref; }}
          type="button"
        >
          <i className="fa fa-question-circle" />
        </button>
        {showNativeDoc
          && (
          <div
            className="native-doc-container"
            ref={(ref) => { this.nativeDocContainer = ref; }}
          >
            <p>{nativeDoc}</p>
          </div>
          )
        }
      </div>
    );
  }
}

export default NativeDocCell;
