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

import axios from 'axios';

axios.defaults.baseURL = process.env.BASE_HREF;

const hadleError = (error) => {
  const { data } = error.response;
  if (typeof data === 'object') {
    return data.body ? data.body.message : data.message;
  }
  return `${error.response.status} ${error.response.statusText}`;
};

/* enums */
export const getCompatibilityLevels = () => axios.get('/api/enums/compatibility-levels')
  .then(res => res.data)
  .catch(error => Promise.reject(hadleError(error)));

export const getTags = () => axios.get('/api/enums/tag-types', { params: { detailed: true } })
  .then(res => res.data);


/* schemas */
export const getSchemas = params => axios.get('/api/schemas/', { params }).then(res => res.data);

export const postSchema = (subject, schemaJson) => axios.post(
  '/api/schemas',
  JSON.stringify({ subject, schemaJson }),
  { headers: { 'Content-Type': 'application/json' } },
)
  .then(res => res.data);

export const deleteSchemas = subject => axios.delete(`/api/schemas/${subject}`)
  .then(res => res.data)
  .catch(error => Promise.reject(hadleError(error)));


/* schema */
export const getSchema = (subject, version, params) => axios.get(`/api/views/schemas/profile/${subject}/${version}/`, { params })
  .then(res => res.data)
  .catch(error => Promise.reject(hadleError(error)));

export const getDetails = (subject, version) => axios.get(`/api/views/schemas/json/${subject}/${version}/`)
  .then(res => res.data);

export const getFullDiff = (subject, params) => axios.get('/api/queries/schemas-diff', { params: { subject, full: true, ...params } })
  .then(res => res.data);

export const getSchemaIdentity = subject => axios.get(`/api/views/schemas/identity/${subject}`)
  .then(res => res.data)
  .catch(error => Promise.reject(hadleError(error)));

export const setCompatibilityLevel = (subject, compatibilityLevel) => axios.put(
  `/api/schemas/${subject}`,
  JSON.stringify({ compatibilityLevel }),
  { headers: { 'Content-Type': 'application/json' } },
)
  .then(res => res.data)
  .catch(error => Promise.reject(hadleError(error)));

export const deleteSchema = (subject, version) => axios.delete(`/api/schemas/${subject}/${version}`)
  .then(res => res.data)
  .catch(error => Promise.reject(hadleError(error)));

export const testSchemaCompitability = (subject, schemaJson) => axios.post(
  '/api/queries/schemas-compatibility-test?detailed=true',
  JSON.stringify({ subject, schemaJson }),
  { headers: { 'Content-Type': 'application/json' } },
)
  .then(res => res.data)
  .catch((error) => {
    if (error.response.status === 404) {
      return Promise.resolve({ errors: [] });
    }
    return Promise.reject(hadleError(error));
  });


/* metadata */
export const postSchemaMetadata = (subject, version, doc) => axios.put(
  `/api/metadata/schemas/${subject}/${version}`,
  JSON.stringify({ doc }),
  { headers: { 'Content-Type': 'application/json' } },
)
  .then(res => res.data);

export const postFieldMetadata = (...args) => {
  const [subject, version, schemaName, name, doc] = args;
  return axios.put(
    `/api/metadata/schemas/${subject}/${version}/fields/${schemaName}/${name}`,
    JSON.stringify({ doc }),
    { headers: { 'Content-Type': 'application/json' } },
  )
    .then(res => res.data);
};

export const deleteFieldMetadata = (...args) => {
  const [subject, version, schemaName, name] = args;
  return axios.delete(`/api/metadata/schemas/${subject}/${version}/fields/${schemaName}/${name}`)
    .then(res => res.data);
};

export const deleteSchemaMetadata = (subject, version) => axios.delete(`/api/metadata/schemas/${subject}/${version}`)
  .then(res => res.data);

export const renderHTMLDoc = doc => axios.post('/api/queries/html-formatted-metadata-doc', doc, { headers: { 'Content-Type': 'text/plain' } })
  .then(res => res.data);


/* other */
export const getQueryExamples = () => axios.get('/config/queryExamples.json')
  .then(res => res.data);
