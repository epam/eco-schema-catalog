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
import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Selector } from 'react-eco-ui';
import './TypeSelector.scss';

class TypeSelector extends PureComponent {
  static propTypes = {
    availableTypes: PropTypes.array,
    checkedTypes: PropTypes.array,
    applyAggregation: PropTypes.func,
    clearAppliedAggregations: PropTypes.func,
  };

  getArrow = (params) => {
    const { isOpen } = params;
    return isOpen
      ? (<div className="type-selector-caret up" />)
      : (<div className="type-selector-caret down" />);
  }

  render() {
    const {
      availableTypes = [], checkedTypes = [], applyAggregation, clearAppliedAggregations,
    } = this.props;
    const isHaveAtLeastOneCheked = !!checkedTypes.length;
    return (
      <Selector
        buttonClassName={`
        type-selector-button
        ${isHaveAtLeastOneCheked ? 'have-checked-item' : ''}
      `}
        options={availableTypes}
        selectedOption={checkedTypes}
        valueType="multiple"
        onOptionChange={
          option => applyAggregation('types', option)
        }
        onClear={() => clearAppliedAggregations()}
        arrowComponent={this.getArrow}
        placeholder="Types"
        offsetOptions={5}
        isClearIcon
      />
    );
  }
}

export default TypeSelector;
