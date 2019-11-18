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
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Selector } from 'react-eco-ui';
import {
  NAMESPACE_TERM,
  COMPATIBILITY_TERM,
  DELETED_TERM,
  VERSION_TERM,
  VERSION_LATEST_TERM,
  MODE_TERM,
} from '../../../../consts/terms';
import SelectorArrow from '../../common/SelectorArrow/SelectorArrow';
import CommonOptions from './CommonOptions/CommonOptions';
import NamespaceOptions from './NamespaceOptions/NamespaceOptions';
import YesNoOptions from './YesNoOptions/YesNoOptions';

import './AdvancedSearch.scss';

class AdvancedSearch extends Component {
  optionsMap = {
    [NAMESPACE_TERM]: NamespaceOptions,
    [COMPATIBILITY_TERM]: CommonOptions,
    [MODE_TERM]: CommonOptions,
    [VERSION_TERM]: CommonOptions,
    [DELETED_TERM]: YesNoOptions,
    [VERSION_LATEST_TERM]: YesNoOptions,
  }

  static propTypes = {
    compatibility: PropTypes.object,
    rootNamespace: PropTypes.object,
    deleted: PropTypes.object,
    version: PropTypes.object,
    versionLatest: PropTypes.object,
    mode: PropTypes.object,

    compatibilityTerm: PropTypes.array,
    namespaceTerm: PropTypes.array,
    deletedTerm: PropTypes.array,
    versionTerm: PropTypes.array,
    versionLatestTerm: PropTypes.array,
    modeTerm: PropTypes.array,

    isClearAll: PropTypes.bool,
    applyMultipleAggregation: PropTypes.func,
    clearAllTerms: PropTypes.func,
  }

  getSelector = (appliedTerms, allTerms, termName, placeholder) => {
    const { applyMultipleAggregation } = this.props;
    if (!Object.keys(allTerms).length) {
      return null;
    }
    const isHaveAtLeastOneCheked = appliedTerms.length !== 0;
    return (
      <Selector
        buttonClassName={`advanced-search-selector-button ${isHaveAtLeastOneCheked ? 'selected' : ''}`}
        options={Object.entries(allTerms)}
        selectedOption={appliedTerms}
        valueType="multiple"
        onOptionChange={option => applyMultipleAggregation(termName, option, appliedTerms)}
        onClear={() => applyMultipleAggregation(termName)}
        optionsComponent={this.optionsMap[termName]}
        arrowComponent={SelectorArrow}
        placeholder={placeholder}
        offsetOptions={0}
        isClearIcon
      />
    );
  }

  hadnleClearAll = (e) => {
    e.preventDefault();
    const { clearAllTerms } = this.props;
    clearAllTerms();
  }

  render() {
    const {
      rootNamespace,
      compatibility,
      mode,
      deleted,
      version,
      versionLatest,

      namespaceTerm,
      compatibilityTerm,
      modeTerm,
      deletedTerm,
      versionTerm,
      versionLatestTerm,

      isClearAll,
    } = this.props;
    return (
      <div className="advanced-search">
        {this.getSelector(namespaceTerm, rootNamespace, NAMESPACE_TERM, 'Namespace')}
        {this.getSelector(compatibilityTerm, compatibility, COMPATIBILITY_TERM, 'Compatibility')}
        {this.getSelector(modeTerm, mode, MODE_TERM, 'Mode')}
        {this.getSelector(versionTerm, version, VERSION_TERM, 'Version')}
        {this.getSelector(deletedTerm, deleted, DELETED_TERM, 'Show deleted schemas')}
        {this.getSelector(versionLatestTerm, versionLatest, VERSION_LATEST_TERM, 'Show latest version')}
        {
          isClearAll
            && (
            <a
              className="clear-all"
              onClick={this.hadnleClearAll}
              role="button"
              tabIndex={0}
            >
              Clear all
            </a>
            )
        }
      </div>
    );
  }
}

export default AdvancedSearch;
