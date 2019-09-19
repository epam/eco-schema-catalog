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
import { SCHEMA } from '../../../../consts/consts';

const createMetadata = (...args) => {
  const [
    type, subject, version, doc, formattedDoc, schemaName, name,
  ] = args;
  let key;
  if (type === SCHEMA) {
    key = {
      subject,
      type,
      version,
    };
  } else {
    key = {
      field: name,
      schemaFullName: schemaName,
      subject,
      version,
      type,
    };
  }
  return {
    key,
    value: {
      doc,
      formattedDoc,
    },
  };
};

const assingMetadataDocs = (metadata, doc, formattedDoc) => {
  const { key, value } = metadata;
  const newValue = Object.assign({}, value, { doc, formattedDoc });
  return {
    key: Object.assign({}, key),
    value: newValue,
  };
};

export { createMetadata, assingMetadataDocs };
