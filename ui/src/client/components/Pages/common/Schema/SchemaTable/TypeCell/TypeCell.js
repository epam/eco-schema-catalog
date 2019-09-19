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

class TypeCell extends Component {
  static propTypes = {
    fullName: PropTypes.string,
  }

  static union = 'union'

  static parseUnionType(fullname) {
    if (fullname.slice(0, 5) === TypeCell.union) {
      const typesString = fullname.slice(5);
      const cleanTypesString = typesString.slice(1, typesString.length - 1);
      return cleanTypesString.split(',').join(' | ');
    }
    return fullname;
  }

  render() {
    const { fullName } = this.props;
    return (
      <div
        data-for="schema-table-tooltip"
        data-tip={fullName}
        className="with-ellipsis"
      >
        {TypeCell.parseUnionType(fullName)}
      </div>
    );
  }
}

export default TypeCell;
