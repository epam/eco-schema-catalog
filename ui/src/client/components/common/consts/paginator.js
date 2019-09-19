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
import { CORE } from './core';

export const PAGINATOR = `${CORE}_paginator`;

export const DEFAULT_INIT_INDEX = 1;
export const DEFAULT_DELIMETER = '...';
export const DEFAULT_VALUE_PER_PAGE = 10;
export const DEFAULT_AMOUNT_PICKERS_TO_SHOW = 4;

export const DEFAULT_ENABLE = {
  DELIMETER: true,
  LABELS: true,
  CONTROLS: true,
  INPUT_CONTROL: true,
};
