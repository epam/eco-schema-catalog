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
import { nanoid } from 'nanoid';
import { Paginator, Selector } from 'react-eco-ui';
import './Paginator.scss';

class PaginatorConatainer extends Component {
  static propTypes = {
    page: PropTypes.number,
    totalElements: PropTypes.number,
    size: PropTypes.number,
    onChangePage: PropTypes.func,
    onChangeSize: PropTypes.func,
  }

  render() {
    const {
      page, totalElements, size, onChangePage, onChangeSize,
    } = this.props;
    if (totalElements === 0) {
      return null;
    }
    return (
      <div className="paginator-container">
        <Paginator
          key={nanoid()}
          className="paginator"
          amount={totalElements}
          initIndex={page}
          onPickerChange={onChangePage}
          valuePerPage={size}
          pickerClassName="picker"
          labelClassName="label"
          delimeterClassName="delimeter"
          controlClassName="control"
          enableInputControl={false}
          enableControls={false}
        />
        <div className="paginator-size-selector">
          <p>Rows per page: </p>
          <Selector
            options={[10, 20, 50]}
            selectedOption={size.toString()}
            onOptionChange={option => onChangeSize(+option)}
            buttonClassName="paginator-size-selector-button"
            arrowComponent={() => (<div className="paginator-arrow" />)}
          />
        </div>
      </div>
    );
  }
}

export default PaginatorConatainer;
