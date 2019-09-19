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
import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Input, IconButton } from 'react-eco-ui';
import QueryExamples from './QueryExamples';
import questionIcon from '../../../../assets/question.svg';
import searchIcon from '../../../../assets/search.svg';
import filterIcon from '../../../../assets/filter.svg';
import './PrimarySearch.scss';

class PrimarySearch extends PureComponent {
  static propTypes = {
    query: PropTypes.string,
    getSchemas: PropTypes.func,
    queryExamples: PropTypes.object,
    toggleAdvancedSearch: PropTypes.func,
    openCreateSchemaModal: PropTypes.func,
  }

  constructor(props) {
    super(props);
    this.state = {
      queryValue: props.query,
      showQueryExamples: false,
    };
  }

  handleSearch = () => {
    const { getSchemas } = this.props;
    const { queryValue } = this.state;
    getSchemas({ query: queryValue, page: 0 });
  }

  handleChangeSearchInput = (value) => {
    this.setState({ queryValue: value });
  }

  handleEnterKey = (e) => {
    if (e.key === 'Enter') {
      this.handleSearch();
    }
  }

  handleShowQueryExamples = (isShow) => {
    this.setState({ showQueryExamples: isShow });
  }

  handlePickQueryExample = (value) => {
    this.setState({ queryValue: value }, () => {
      this.handleShowQueryExamples(false);
      this.handleSearch();
    });
  }

  render() {
    const { showQueryExamples, queryValue } = this.state;
    const { queryExamples, toggleAdvancedSearch, openCreateSchemaModal } = this.props;
    return (
      <div className="primary-search">
        <div
          className="primary-search-controll"
          onKeyPress={this.handleEnterKey}
          role="presentation"
        >

          { showQueryExamples
            && (
            <QueryExamples
              queryExamples={queryExamples}
              onPick={this.handlePickQueryExample}
              outsideClickCallback={() => this.handleShowQueryExamples(false)}
            />
            )
          }

          <IconButton
            className="help-button"
            onClick={() => this.handleShowQueryExamples(!showQueryExamples)}
          >
            <img src={questionIcon} alt="" />
          </IconButton>

          <Input
            className="search-text-input"
            onChange={this.handleChangeSearchInput}
            value={queryValue}
            placeholder="Search.."
          />

          <IconButton
            className="search-button"
            onClick={this.handleSearch}
          >
            <img src={searchIcon} alt="" />
            <span>search</span>
          </IconButton>

          <IconButton
            className="advanced-search-button"
            onClick={toggleAdvancedSearch}
          >
            <img src={filterIcon} alt="" />
            <span>Advanced search</span>
          </IconButton>

          <IconButton
            className="create-schema-button"
            onClick={openCreateSchemaModal}
          >
            <i className="fa fa-plus" />
            <span>Schema</span>
          </IconButton>

        </div>

      </div>
    );
  }
}

export default PrimarySearch;
