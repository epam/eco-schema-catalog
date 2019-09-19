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

/* eslint-disable no-undef */
import {
  isValueInvalid,
  calculateAllAvailablePickers,
  calculateVisiblePickers,
  createSequence,
  calculateIndexesUp,
  calculateIndexesDown,
  calculateIndexes,
  addValue,
  addValues,
  producePickerMap,
} from './paginator';

const wrapPicker = value => ({ value, disabled: false, picked: false });
const wrapPickers = (values = []) => values.map(wrapPicker);

describe('paginator utils spec', () => {
  describe('isValueInvalid', () => {
    it('should return true if value is invalid', () => {
      expect(isValueInvalid(0)).toBeTruthy();
      expect(isValueInvalid(-1)).toBeTruthy();
      expect(isValueInvalid(null)).toBeTruthy();
      expect(isValueInvalid(undefined)).toBeTruthy();
      expect(isValueInvalid(false)).toBeTruthy();
    });

    it('should return false if value is valid', () => {
      expect(isValueInvalid(1)).toBeFalsy();
      expect(isValueInvalid(123)).toBeFalsy();
      expect(isValueInvalid(Infinity)).toBeFalsy();
      expect(isValueInvalid(true)).toBeFalsy();
    });
  });

  describe('calculateAllAvailablePickers', () => {
    it('should return 0, if provided values is not valid', () => {
      expect(calculateAllAvailablePickers(0, 12)).toBe(0);
      expect(calculateAllAvailablePickers(12, 0)).toBe(0);
    });

    it('should retun ceil value, as a result of division', () => {
      const all = 90;
      const perPage = 12;
      const expected = Math.ceil(all / perPage);

      expect(calculateAllAvailablePickers(all, perPage)).toBe(expected);
    });
  });

  describe('calculateVisiblePickers', () => {
    it('should return 0 if provided values are not valid', () => {
      expect(calculateVisiblePickers(0, 12)).toBe(0);
      expect(calculateVisiblePickers(12, 0)).toBe(0);
    });

    it('should return value - how many pickers to show, depend on arguments', () => {
      expect(calculateVisiblePickers(123, 12)).toBe(12);
      expect(calculateVisiblePickers(10, 12)).toBe(10);
      expect(calculateVisiblePickers(12, 12)).toBe(12);
      expect(calculateVisiblePickers(1, 12)).toBe(1);
      expect(calculateVisiblePickers(100, 99)).toBe(99);
    });
  });

  describe('createSequence', () => {
    it('should return empty array, if provided value is not valid', () => {
      expect(createSequence()).toEqual([]);
      expect(createSequence(null, null)).toEqual([]);
      expect(createSequence(NaN, NaN)).toEqual([]);
      expect(createSequence(NaN, 12)).toEqual([]);
      expect(createSequence(12, null)).toEqual([]);
    });

    it('should return sequence, depending on provided values', () => {
      expect(createSequence(0, 5)).toEqual([0, 1, 2, 3, 4, 5]);
      expect(createSequence(10, 15)).toEqual([10, 11, 12, 13, 14, 15]);
      expect(createSequence(-1, 3)).toEqual([-1, 0, 1, 2, 3]);
      expect(createSequence(-10, -7)).toEqual([-10, -9, -8, -7]);
      expect(createSequence(3, 0)).toEqual([3]);
      expect(createSequence(0, -10)).toEqual([0]);
    });

    it('should return sequence, with provided step value', () => {
      expect(createSequence(0, 5, 2)).toEqual([0, 2, 4]);
      expect(createSequence(0, 5, 5)).toEqual([0, 5]);
      expect(createSequence(0, 100, 20)).toEqual([0, 20, 40, 60, 80, 100]);
      expect(createSequence(0, 99, 20)).toEqual([0, 20, 40, 60, 80]);
      expect(createSequence(0, -20, -20)).toEqual([0, -20]);
      expect(createSequence(0, -34, -12)).toEqual([0, -12, -24]);
    });
  });

  describe('calculateIndexesUp', () => {
    it('should return empty arr, if value is not valid', () => {
      expect(calculateIndexesUp(12, 5, 6)).toEqual([]);
    });

    it('should return sequence with full visible count, if it is include last value', () => {
      expect(calculateIndexesUp(8, 4, 12)).toEqual([8, 9, 10, 11, 12]);
      expect(calculateIndexesUp(9, 4, 12)).toEqual([8, 9, 10, 11, 12]);
      expect(calculateIndexesUp(10, 4, 12)).toEqual([8, 9, 10, 11, 12]);
      expect(calculateIndexesUp(11, 4, 12)).toEqual([8, 9, 10, 11, 12]);
      expect(calculateIndexesUp(12, 4, 12)).toEqual([8, 9, 10, 11, 12]);

      expect(calculateIndexesUp(1, 4, 5)).toEqual([1, 2, 3, 4, 5]);
      expect(calculateIndexesUp(2, 4, 5)).toEqual([1, 2, 3, 4, 5]);
      expect(calculateIndexesUp(3, 4, 5)).toEqual([1, 2, 3, 4, 5]);
      expect(calculateIndexesUp(4, 4, 5)).toEqual([1, 2, 3, 4, 5]);
      expect(calculateIndexesUp(5, 4, 5)).toEqual([1, 2, 3, 4, 5]);
    });

    it('should return correct sequence, depending on visible count -1', () => {
      expect(calculateIndexesUp(1, 4, 12)).toEqual([1, 2, 3, 4]);
      expect(calculateIndexesUp(2, 4, 12)).toEqual([2, 3, 4, 5]);
      expect(calculateIndexesUp(3, 4, 12)).toEqual([3, 4, 5, 6]);
      expect(calculateIndexesUp(4, 4, 12)).toEqual([4, 5, 6, 7]);
      expect(calculateIndexesUp(5, 4, 12)).toEqual([5, 6, 7, 8]);
      expect(calculateIndexesUp(6, 4, 12)).toEqual([6, 7, 8, 9]);
      expect(calculateIndexesUp(7, 4, 12)).toEqual([7, 8, 9, 10]);

      expect(calculateIndexesUp(12, 3, 18)).toEqual([12, 13, 14]);
      expect(calculateIndexesUp(13, 3, 18)).toEqual([13, 14, 15]);
      expect(calculateIndexesUp(14, 3, 18)).toEqual([14, 15, 16]);
    });
  });

  describe('calculateIndexesDown', () => {
    it(`should return sequence from 1 to visible count - 1,
if current index less then visible count -1`, () => {
      expect(calculateIndexesDown(1, 4, 5)).toEqual([1, 2, 3, 4]);
      expect(calculateIndexesDown(2, 4, 5)).toEqual([1, 2, 3, 4]);
      expect(calculateIndexesDown(3, 4, 5)).toEqual([1, 2, 3, 4]);
      expect(calculateIndexesDown(4, 4, 5)).toEqual([1, 2, 3, 4]);

      expect(calculateIndexesDown(1, 9, 15)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
      expect(calculateIndexesDown(2, 9, 15)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
      expect(calculateIndexesDown(3, 9, 15)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
      expect(calculateIndexesDown(4, 9, 15)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
      expect(calculateIndexesDown(5, 9, 15)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
      expect(calculateIndexesDown(6, 9, 15)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
      expect(calculateIndexesDown(7, 9, 15)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
      expect(calculateIndexesDown(8, 9, 15)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
      expect(calculateIndexesDown(9, 9, 15)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
    });

    it(`should invoke calculateIndexesUp,
so that current index should be last value in array, or include last value`, () => {
      expect(calculateIndexesDown(5, 4, 15)).toEqual([2, 3, 4, 5]);
      expect(calculateIndexesDown(6, 4, 15)).toEqual([3, 4, 5, 6]);
      expect(calculateIndexesDown(7, 4, 15)).toEqual([4, 5, 6, 7]);
      expect(calculateIndexesDown(8, 4, 15)).toEqual([5, 6, 7, 8]);
      expect(calculateIndexesDown(9, 4, 15)).toEqual([6, 7, 8, 9]);
      expect(calculateIndexesDown(10, 4, 15)).toEqual([7, 8, 9, 10]);
      expect(calculateIndexesDown(11, 4, 15)).toEqual([8, 9, 10, 11]);
      expect(calculateIndexesDown(12, 4, 15)).toEqual([9, 10, 11, 12]);
      expect(calculateIndexesDown(13, 4, 15)).toEqual([10, 11, 12, 13]);
      expect(calculateIndexesDown(14, 4, 15)).toEqual([11, 12, 13, 14, 15]);
      expect(calculateIndexesDown(15, 4, 15)).toEqual([11, 12, 13, 14, 15]);

      expect(calculateIndexesDown(3, 2, 5)).toEqual([2, 3]);
      expect(calculateIndexesDown(4, 2, 5)).toEqual([3, 4, 5]);
      expect(calculateIndexesDown(5, 2, 5)).toEqual([3, 4, 5]);
    });
  });

  describe('calculateIndexes', () => {
    it('should return empty array if provided values is not valid', () => {
      expect(calculateIndexes({ currentIndex: -1 })).toEqual([]);
      expect(calculateIndexes({ })).toEqual([]);
      expect(calculateIndexes({ all: 0 })).toEqual([]);
      expect(calculateIndexes({ visibleAmount: 0 })).toEqual([]);
      expect(calculateIndexes({ up: true })).toEqual([]);
    });

    it('should return empty arr if both directions are provided', () => {
      expect(calculateIndexes({ up: true, down: true })).toEqual([]);
    });

    it(`should return array of one item, or from 1 to 2,
  if visible amount is less or equal 2`, () => {
      expect(calculateIndexes({ visibleAmount: 1, currentIndex: 1, all: 20 })).toEqual([1]);
      expect(calculateIndexes({ visibleAmount: 1, currentIndex: 13, all: 20 })).toEqual([13]);


      expect(calculateIndexes({ visibleAmount: 2, currentIndex: 1, all: 20 })).toEqual([1, 2]);
      expect(calculateIndexes({ visibleAmount: 2, currentIndex: 2, all: 20 })).toEqual([2, 3]);
      expect(calculateIndexes({ visibleAmount: 2, currentIndex: 3, all: 20 })).toEqual([3, 4]);
      expect(calculateIndexes({ visibleAmount: 2, currentIndex: 4, all: 20 })).toEqual([4, 5]);

      expect(calculateIndexes({ visibleAmount: 2, currentIndex: 20, all: 20 })).toEqual([19, 20]);
      expect(calculateIndexes({ visibleAmount: 2, currentIndex: 19, all: 20 })).toEqual([19, 20]);

      expect(calculateIndexes({
        visibleAmount: 2, down: true, all: 20, currentIndex: 1,
      })).toEqual([1, 2]);
      expect(calculateIndexes({
        visibleAmount: 2, down: true, all: 20, currentIndex: 2,
      })).toEqual([1, 2]);
      expect(calculateIndexes({
        visibleAmount: 2, down: true, all: 20, currentIndex: 3,
      })).toEqual([2, 3]);
      expect(calculateIndexes({
        visibleAmount: 2, down: true, all: 20, currentIndex: 4,
      })).toEqual([3, 4]);

      expect(calculateIndexes({
        visibleAmount: 2, down: true, all: 20, currentIndex: 19,
      })).toEqual([18, 19]);
      expect(calculateIndexes({
        visibleAmount: 2, down: true, all: 20, currentIndex: 20,
      })).toEqual([19, 20]);
    });

    it('should use up direction by default', () => {
      expect(calculateIndexes({ visibleAmount: 5, currentIndex: 1, all: 20 }))
        .toEqual([1, 2, 3, 4]);
      expect(calculateIndexes({ visibleAmount: 5, currentIndex: 2, all: 20 }))
        .toEqual([2, 3, 4, 5]);
      expect(calculateIndexes({ visibleAmount: 5, currentIndex: 3, all: 20 }))
        .toEqual([3, 4, 5, 6]);
    });

    it('should call calculate indexes, depending on provided direction', () => {
      const calcIndexUp = jest.fn();
      const calIndexDown = jest.fn();

      expect(calculateIndexes({
        visibleAmount: 5,
        currentIndex: 1,
        all: 20,
        up: true,
      })).toEqual([1, 2, 3, 4]);

      calculateIndexes({
        visibleAmount: 5,
        currentIndex: 1,
        all: 20,
        up: true,
      }, calcIndexUp, calIndexDown);

      expect(calcIndexUp).toHaveBeenCalledTimes(1);
      expect(calcIndexUp).toHaveBeenCalledWith(1, 4, 20);

      expect(calculateIndexes({
        visibleAmount: 5,
        currentIndex: 6,
        all: 20,
        down: true,
      })).toEqual([3, 4, 5, 6]);

      calculateIndexes({
        visibleAmount: 5,
        currentIndex: 6,
        all: 20,
        down: true,
      }, calcIndexUp, calIndexDown);

      expect(calIndexDown).toHaveBeenCalledTimes(1);
      expect(calIndexDown).toHaveBeenCalledWith(6, 4, 20);
    });
  });

  describe('addValue', () => {
    it('should return function', () => {
      const f = addValue();

      expect(f).toBeInstanceOf(Function);
    });

    it('should return new array, if values provided', () => {
      const arr = [12, 12];

      expect(addValue(13)(arr)).not.toBe(arr);
    });

    it('should return old arr, if no values provided', () => {
      const arr = [12, 12];

      expect(addValue()(arr)).toBe(arr);
    });

    it('should add new value', () => {
      expect(addValue(12)([13])).toEqual([13, 12]);
      expect(addValue(12)([])).toEqual([12]);
      expect(addValue(12)([13, 14])).toEqual([13, 14, 12]);
      expect(addValue(12)()).toEqual([12]);
    });
  });

  describe('addValues', () => {
    it('should return function', () => {
      const f = addValues();

      expect(f).toBeInstanceOf(Function);
    });

    it('should return new array, if values provided', () => {
      const arr = [12, 12];

      expect(addValues([13])(arr)).not.toBe(arr);
    });

    it('should return old arr, if no values provided', () => {
      const arr = [12, 12];

      expect(addValues()(arr)).toBe(arr);
    });

    it('should concat provided values, to existing array', () => {
      expect(addValues([1, 2, 3])([5])).toEqual([5, 1, 2, 3]);
      expect(addValues([1, 2, 3, 4, 5])([5, 6])).toEqual([5, 6, 1, 2, 3, 4, 5]);

      expect(addValues()()).toEqual([]);

      expect(addValues([12])()).toEqual([12]);
      expect(addValues()([12])).toEqual([12]);

      expect(addValues([])([12])).toEqual([12]);
      expect(addValues([12])([])).toEqual([12]);
    });
  });

  describe('producePickerMap', () => {
    it('should return new array', () => {
      expect(producePickerMap()).toEqual([]);
      expect(producePickerMap({ lastIndex: 12 })).toEqual([]);
    });

    it('should return array with pickers, depending on provided options', () => {
      const indexes = [1, 2, 3, 4, 5];

      const firstLabel = 'FIRST';
      const lastLabel = 'LAST';

      const onlyFirstLable = { firstLabel };
      const onlyLastLabel = { lastLabel };
      const labels = { firstLabel, lastLabel };

      const controlUp = '>';
      const controlDown = '<';

      const onlyUpConrol = { controlUp };
      const onlyDownControl = { controlDown };
      const controls = { controlUp, controlDown };

      const withLast = true;
      const lastIndex = 6;
      const delimeter = '...';

      // only indexes
      const onlyIndexes = producePickerMap({ indexes, lastIndex });
      const expectedOnlyIndexes = wrapPickers(indexes);

      expect(onlyIndexes).toEqual(expectedOnlyIndexes);

      // indexes, first label
      const indexesAndFirstLabel = producePickerMap({ indexes, labels: onlyFirstLable, lastIndex });
      const expectedIndexesAndFirstLabel = wrapPickers([firstLabel, ...indexes]);

      expect(indexesAndFirstLabel).toEqual(expectedIndexesAndFirstLabel);

      // indexes, last label
      const indexesAndLastLabels = producePickerMap({ indexes, labels: onlyLastLabel, lastIndex });
      const expectedIndexesAndLastLabels = wrapPickers([...indexes, lastLabel]);

      expect(indexesAndLastLabels).toEqual(expectedIndexesAndLastLabels);

      // indexes, all labels
      const indexesAndAllLabels = producePickerMap({ indexes, labels, lastIndex });
      const expectedIndexesAndAllLabels = wrapPickers([firstLabel, ...indexes, lastLabel]);

      expect(indexesAndAllLabels).toEqual(expectedIndexesAndAllLabels);

      // indexes, controlUp
      const indexesAndControlUp = producePickerMap({ indexes, controls: onlyUpConrol, lastIndex });
      const expectedIndexesAndControlUp = wrapPickers([...indexes, controlUp]);

      expect(indexesAndControlUp).toEqual(expectedIndexesAndControlUp);

      // indexes, controlDown
      const indexesAndContolDown = producePickerMap({
        indexes,
        controls: onlyDownControl,
        lastIndex,
      });
      const expectedIndexesAndContolDown = wrapPickers([controlDown, ...indexes]);

      expect(indexesAndContolDown).toEqual(expectedIndexesAndContolDown);

      // indexes, all controls
      const indexesAndAllControls = producePickerMap({ indexes, controls, lastIndex });
      const expectedIndexesAndAllControls = wrapPickers([controlDown, ...indexes, controlUp]);

      expect(indexesAndAllControls).toEqual(expectedIndexesAndAllControls);

      // indexes, labels, controls
      const indexesAndAllLabelsAndAllControls = producePickerMap({
        indexes, controls, labels, lastIndex,
      });
      const expectedIndexesAndAllLabelsAndAllControls = [
        expectedIndexesAndAllLabels[0],
        expectedIndexesAndAllControls[0],
        ...wrapPickers(indexes),
        expectedIndexesAndAllControls[expectedIndexesAndAllControls.length - 1],
        expectedIndexesAndAllLabels[expectedIndexesAndAllLabels.length - 1],
      ];

      expect(indexesAndAllLabelsAndAllControls).toEqual(expectedIndexesAndAllLabelsAndAllControls);

      // with last no delimeter
      const withLastNoDelimeter = producePickerMap({
        indexes, controls, labels, withLast, lastIndex,
      });
      const expectedWithLastNoDelimeter = [
        expectedIndexesAndAllLabels[0],
        expectedIndexesAndAllControls[0],
        ...wrapPickers([...indexes, lastIndex]),
        expectedIndexesAndAllControls[expectedIndexesAndAllControls.length - 1],
        expectedIndexesAndAllLabels[expectedIndexesAndAllLabels.length - 1],
      ];

      expect(withLastNoDelimeter).toEqual(expectedWithLastNoDelimeter);

      // with last, delimeter, last index
      const withLastAndDelimeter = producePickerMap({
        indexes, controls, labels, withLast, lastIndex, delimeter,
      });
      const expectedWithLastAndDelimeter = [
        expectedIndexesAndAllLabels[0],
        expectedIndexesAndAllControls[0],
        ...wrapPickers([...indexes, delimeter, lastIndex]),
        expectedIndexesAndAllControls[expectedIndexesAndAllControls.length - 1],
        expectedIndexesAndAllLabels[expectedIndexesAndAllLabels.length - 1],
      ];

      expect(withLastAndDelimeter).toEqual(expectedWithLastAndDelimeter);

      // with currentIndex first
      const withCurrentIndexOne = producePickerMap({
        indexes, controls, labels, withLast, lastIndex, delimeter, currentIndex: 1,
      });
      const expectedWithCurrentIndex = [
        expectedIndexesAndAllLabels[0],
        expectedIndexesAndAllControls[0],
        ...wrapPickers([...indexes, delimeter, lastIndex]),
        expectedIndexesAndAllControls[expectedIndexesAndAllControls.length - 1],
        expectedIndexesAndAllLabels[expectedIndexesAndAllLabels.length - 1],
      ];
      expectedWithCurrentIndex[0].disabled = true;
      expectedWithCurrentIndex[1].disabled = true;
      expectedWithCurrentIndex[2].picked = true;

      expect(withCurrentIndexOne).toEqual(expectedWithCurrentIndex);

      // with current index last
      const withCurrentIndexTwo = producePickerMap({
        indexes,
        controls,
        labels,
        withLast,
        lastIndex,
        delimeter,
        currentIndex: indexes[indexes.length - 1],
      });

      const expectedWithCurrentIndexTwo = [
        expectedIndexesAndAllLabels[0],
        expectedIndexesAndAllControls[0],
        ...wrapPickers([...indexes, delimeter, lastIndex]),
        expectedIndexesAndAllControls[expectedIndexesAndAllControls.length - 1],
        expectedIndexesAndAllLabels[expectedIndexesAndAllLabels.length - 1],
      ];
      expectedWithCurrentIndexTwo[expectedWithCurrentIndexTwo.length - 1].disabled = true;
      expectedWithCurrentIndexTwo[expectedWithCurrentIndexTwo.length - 2].disabled = true;
      expectedWithCurrentIndexTwo[expectedWithCurrentIndexTwo.length - 3].picked = true;

      expect(withCurrentIndexTwo).toEqual(withCurrentIndexTwo);
    });
  });
});
