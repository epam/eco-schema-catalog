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
import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import PrimarySearch from './PrimarySearch';
import AdvancedSearch from './AdvancedSearch';
import Schemas from './Schemas';
import Paginator from './Paginator';

import './SchemasContainer.scss';

class SchemasContainer extends PureComponent {
  static propTypes = {
    query: PropTypes.string,
    isHaveAnyCheckedAggregation: PropTypes.bool,
  }

  state = {
    isAdvancedSearchOpen: false,
  }

  toggleAdvancedSearch = () => {
    const { isHaveAnyCheckedAggregation } = this.props;
    if (!isHaveAnyCheckedAggregation) {
      const { isAdvancedSearchOpen } = this.state;
      this.setState({ isAdvancedSearchOpen: !isAdvancedSearchOpen });
    }
  }

  render() {
    const { query, isHaveAnyCheckedAggregation } = this.props;
    const { isAdvancedSearchOpen } = this.state;
    return (
      <React.Fragment>
        <PrimarySearch key={query} toggleAdvancedSearch={this.toggleAdvancedSearch} />
        <div className="schemas-container">
          { (isAdvancedSearchOpen || isHaveAnyCheckedAggregation) ? <AdvancedSearch /> : null }
          <Schemas />
          <Paginator />
        </div>
      </React.Fragment>
    );
  }
}

export default SchemasContainer;
