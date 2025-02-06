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

import isNaN from 'lodash/isNaN';
import qs from 'qs';

const parseTerms = (terms) => {
  const toReturn = {};
  Object.keys(terms).forEach((key) => {
    if (terms[key].length > 0) {
      const items = terms[key].split(',');
      toReturn[key] = items;
    }
  });
  return toReturn;
};

const parseSearchString = (search) => {
  if (typeof search === 'string') {
    let {
      query,
      page = 1,
      pageSize = 20,
    } = qs.parse(search, { ignoreQueryPrefix: true });
    const {
      namespaceTerm = [],
      compatibilityTerm = [],
      metadataUpdatedByTerm = [],
      versionTerm = [],
      versionLatestTerm = [],
      deletedTerm = [],
    } = qs.parse(search, { ignoreQueryPrefix: true });
    if (!query) {
      query = '*';
    }
    page = Number.parseInt(page, 10);
    pageSize = Number.parseInt(pageSize, 10);
    if (page <= 0 || isNaN(page)) {
      page = 0;
    }
    if (pageSize <= 0 || isNaN(pageSize)) {
      pageSize = 20;
    }
    // pages starts from 1 for user, but for REST from 0
    if (page > 0) {
      page -= 1;
    }

    return {
      query,
      page,
      pageSize,
      ...parseTerms({
        namespaceTerm,
        compatibilityTerm,
        metadataUpdatedByTerm,
        versionTerm,
        versionLatestTerm,
        deletedTerm,
      }),
    };
  }
  return { query: '*', page: 0, pageSize: 20 };
};

export default parseSearchString;
