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
/* eslint-disable react/no-find-dom-node */
import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import PropTypes from 'prop-types';

function withOutsideClick(WrappedComponent) {
  return class extends Component {
    static propTypes = {
      outsideClickCallback: PropTypes.func,
    }

    componentDidMount() {
      document.addEventListener('click', this.handleOutsideClick);
    }

    componentWillUnmount() {
      document.removeEventListener('click', this.handleOutsideClick);
    }

    handleOutsideClick = (e) => {
      const { outsideClickCallback } = this.props;
      const node = findDOMNode(this);
      if (node) {
        if (!node.contains(e.target) && typeof outsideClickCallback === 'function') {
          outsideClickCallback();
        }
      }
    }

    render() {
      return (
        <WrappedComponent {...this.props} />
      );
    }
  };
}

export default withOutsideClick;
