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

import React from 'react';
import PropTypes from 'prop-types';
import ReactTooltip from 'react-tooltip';
import deltedIcon from '../../../../assets/delete-file-interface-symbol.svg';
import './DeletedIcon.scss';

const DeletedIcon = props => (
  <div className="deleted-icon">
    <img
      data-tip="deleted schema"
      data-for="deleted-icon"
      src={deltedIcon}
      alt=""
    />
    <ReactTooltip id="deleted-icon" place={props.place} />
  </div>
);

DeletedIcon.propTypes = {
  place: PropTypes.string,
};

export default DeletedIcon;
