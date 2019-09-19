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

import { EMPTY } from '../../../consts/core';
import { DELIMETER, DEFAULT_DELIMETER } from '../../../consts/delimeter';

import { skipEmptyClassNames } from '../../../utils/string';

const Delimeter = ({ value, className }) => (
  <span className={skipEmptyClassNames([DELIMETER, className])}>
    {value}
  </span>
);

Delimeter.defaultProps = {
  className: EMPTY,
  value: DEFAULT_DELIMETER,
};

Delimeter.propTypes = {
  className: PropTypes.string,
  value: PropTypes.any,
};

export default Delimeter;
