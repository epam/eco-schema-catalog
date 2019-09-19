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
import { pipe } from './functional';

export const isValueInvalid = value => !value || value < 1;

export const calculateAllAvailablePickers = (all, perPage) => (
  isValueInvalid(all) || isValueInvalid(perPage)
    ? 0
    : Math.ceil(all / perPage)
);

export const calculateVisiblePickers = (all, toShow) => {
  if (isValueInvalid(all) || isValueInvalid(toShow)) {
    return 0;
  }

  if (all >= toShow) {
    return toShow;
  }

  return all;
};

export const createSequence = (from, to, step = 1) => {
  // eslint-disable-next-line no-restricted-globals
  if (isNaN(parseInt(from, 10)) || isNaN(parseInt(to, 10))) {
    return [];
  }

  const max = Math.max(from, to);
  const min = Math.min(from, to);

  const getBreakCase = value => (step > 0 && value > max) || (step < 0 && value < min);

  const makeStep = () => {
    // eslint-disable-next-line no-param-reassign
    from += step;
    return from;
  };

  const result = [from];

  while (!getBreakCase(makeStep())) {
    result.push(from);
  }

  return result;
};

export const calculateIndexesUp = (currentIndex, indexCount, lastIndex) => {
  if (currentIndex > lastIndex) {
    return [];
  }

  const withoutLast = currentIndex + indexCount < lastIndex;

  if (!withoutLast) {
    return createSequence(lastIndex - indexCount, lastIndex);
  }

  return createSequence(currentIndex, indexCount + (currentIndex - 1));
};

export const calculateIndexesDown = (currentIndex, indexCount, lastIndex) => {
  if (currentIndex - indexCount <= 0) {
    return createSequence(1, indexCount);
  }

  const from = currentIndex - (indexCount - 1);

  return calculateIndexesUp(from, indexCount, lastIndex);
};

export const calculateIndexes = (
  {
    currentIndex, up, down, all, visibleAmount,
  },
  calcIndexUp = calculateIndexesUp,
  calIndexDown = calculateIndexesDown,
) => {
  if (up && down) {
    return [];
  }

  if (isValueInvalid(currentIndex) || isValueInvalid(all) || isValueInvalid(visibleAmount)) {
    return [];
  }

  if (visibleAmount === 1) {
    return [currentIndex];
  }

  if (!up && !down) {
    // eslint-disable-next-line no-param-reassign
    up = true;
  }

  if (visibleAmount === 2) {
    if (currentIndex === 1) {
      return [1, 2];
    }

    if (currentIndex === all) {
      return [all - 1, all];
    }

    return up ? [currentIndex, currentIndex + 1] : [currentIndex - 1, currentIndex];
  }

  const args = [currentIndex, visibleAmount - 1, all];

  return up ? calcIndexUp(...args) : calIndexDown(...args);
};

export const addValue = value => (arr = []) => (value ? [...arr, value] : arr);
export const addValues = (values = []) => (arr = []) => (values.length ? arr.concat(values) : arr);

export const producePickerMap = ({
  withLast,
  currentIndex,
  lastIndex,
  indexes = [],
  labels = {},
  controls = {},
  delimeter,
} = {}) => pipe(
  addValue(labels.firstLabel),
  addValue(controls.controlDown),
  addValues(indexes),
  addValue(withLast && delimeter),
  addValue(withLast && lastIndex),
  addValue(controls.controlUp),
  addValue(labels.lastLabel),
)([]).map((value) => {
  const isControlUp = value === controls.controlUp;
  const isControlDown = value === controls.controlDown;

  const isFirsLabel = value === labels.firstLabel;
  const isLastLabel = value === labels.lastLabel;

  const isFirst = currentIndex === 1;
  const isLast = currentIndex === lastIndex;

  const disableFirst = isControlDown || isFirsLabel;
  const disableLast = isControlUp || isLastLabel;

  const disabled = !lastIndex || ((disableFirst && isFirst) || (disableLast && isLast));
  const picked = value === currentIndex;

  return { value, disabled, picked };
});
