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

import { createSelector } from 'reselect';
import getCustomDate from '../../utils/getCustomDate/getCustomDate';

export const getSchemaMetadata = state => state.schemaReducer.schemaMetadata;
export const getSchemaVersion = state => state.schemaReducer.version;

export const getUpdatedBy = createSelector(
  getSchemaMetadata,
  schemaMetadata => (schemaMetadata ? schemaMetadata.value.updatedBy : null),
);

export const getUpdatedAt = createSelector(
  getSchemaMetadata,
  schemaMetadata => (schemaMetadata ? getCustomDate(schemaMetadata.value.updatedAt) : null),
);

export const getOriginMetadataVersion = createSelector(
  getSchemaMetadata,
  getSchemaVersion,
  (schemaMetadata, currentVersion) => (
    schemaMetadata === null ? currentVersion : schemaMetadata.key.version
  ),
);
