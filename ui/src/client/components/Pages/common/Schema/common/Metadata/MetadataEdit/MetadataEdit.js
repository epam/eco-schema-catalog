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

/* eslint-disable import/no-unresolved */
/* eslint-disable import/extensions */
import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Button } from 'react-eco-ui';
import PencilIconSVG from './PencilIconSVGComponent/PencilIconSVGComponent';
import MetadataEditInput from './MetadataEditInput/MetadataEditInput';
import MetadataEditCheckbox from './MetadataEditCheckbox/MetadataEditCheckbox';

import getOverridingAvailabilty from '../utils/getOverridingAvailabilty';

import './MetadataEdit.scss';

class MetadataEdit extends PureComponent {
  static propTypes = {
    doc: PropTypes.string,
    formattedDoc: PropTypes.string,
    isSaved: PropTypes.bool,
    updateMetada: PropTypes.func,
    saveMetada: PropTypes.func,
    handleLoading: PropTypes.func,
    currentVersion: PropTypes.number,
    originVersion: PropTypes.number,
  }

  state = {
    inputValue: this.props.doc.toString(),
    isEdit: false,
    isOverrideVersion: false,
  }

  handleChange = (value) => {
    this.setState({ inputValue: value });
  }

  handleIsEditOn = (e) => {
    if (e.target.tagName !== 'A') {
      this.setState({ isEdit: true });
    }
  }

  handleActionMetadata = (action) => {
    const { handleLoading, currentVersion, originVersion } = this.props;
    const { inputValue, isOverrideVersion } = this.state;
    if (inputValue) {
      handleLoading(true);
      action(inputValue.toString(), isOverrideVersion ? currentVersion : originVersion)
        .then(() => {
          this.setState({ isEdit: false });
        })
        .catch(() => {
          this.setState({ isEdit: false });
        })
        .finally(() => {
          handleLoading(false);
        });
    }
  }

  handleCheckOverrideVersion = (isChecked) => {
    this.setState({ isOverrideVersion: isChecked });
  }

  render() {
    const {
      doc,
      formattedDoc,
      isSaved,
      updateMetada,
      saveMetada,
      currentVersion,
      originVersion,
    } = this.props;
    const { inputValue, isEdit, isOverrideVersion } = this.state;

    return (
      <div className="metadata-edit">

        {isEdit
          && (
            <MetadataEditInput
              key={doc}
              value={doc}
              handleChange={this.handleChange}
            />
          )
        }

        {!isEdit
          && (
            <div
              className="metadata-no-edit"
              onClick={e => this.handleIsEditOn(e)}
              role="presentation"
            >
              <PencilIconSVG />
              <div
                className={`formatted-doc ${formattedDoc ? 'haveDoc' : ''}`}
                dangerouslySetInnerHTML={{ __html: formattedDoc || 'Enter description...' }}
              />

            </div>
          )
        }

        {getOverridingAvailabilty(currentVersion, originVersion) && !isSaved
          && (
            <MetadataEditCheckbox
              isChecked={isOverrideVersion}
              handleCheck={this.handleCheckOverrideVersion}
            />
          )
        }

        <div className="actions">
          {isEdit
            && (
              <Button
                name="preview"
                transparent
                disable={doc === inputValue || !inputValue}
                onClick={() => this.handleActionMetadata(updateMetada)}
              />
            )
          }
          {(!isSaved)
            && (
              <Button
                className="green"
                name="save"
                transparent
                disable={!inputValue}
                onClick={() => this.handleActionMetadata(saveMetada)}
              />
            )
          }
          {isEdit
            && (
              <Button
                className="grey"
                name="cancel"
                transparent
                onClick={() => this.setState({ isEdit: false })}
              />
            )
          }
        </div>

      </div>
    );
  }
}

export default MetadataEdit;
