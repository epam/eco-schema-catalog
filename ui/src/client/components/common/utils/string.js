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
import { EMPTY } from '../consts/core';

export const constructClassName = (classes = [], config = {}) => classes.reduce(
  (prev, className) => {
    const needBreak = config.break && config.break.indexOf(className) !== -1;
    // eslint-disable-next-line no-param-reassign
    prev = needBreak ? prev : `${prev} ${className}`;
    return prev.trim();
  }, '',
);

export const skipEmptyClassNames = classes => constructClassName(classes, { break: [EMPTY] });
