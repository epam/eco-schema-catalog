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
import { Button, Loader } from 'react-eco-ui';
import './Tags.scss';

class Tags extends Component {
  static propTypes = {
    tags: PropTypes.array,
    getTags: PropTypes.func,
    tagClick: PropTypes.func,
  }

  state = {
    isLoading: false,
  }

  componentDidMount() {
    const { getTags, tags } = this.props;

    if (!tags.length) {
      this.setState({ isLoading: true });
      getTags().finally(() => { this.setState({ isLoading: false }); });
    }
  }

  render() {
    const { tags, tagClick } = this.props;
    const { isLoading } = this.state;
    return (
      <div>
        {isLoading
          && (
          <div className="metadata-loading">
            <div className="metadata-loading-backgroung" />
            <Loader type="spinner" color="lime-green" />
          </div>
          )
        }
        <div className="metadata-tags">
          {tags.map(tag => (
            <Button
              key={tag.name}
              name={tag.name}
              className="grey"
              onClick={() => tagClick(tag.template)}
              transparent
            />
          ))
          }
        </div>

      </div>
    );
  }
}

export default Tags;
