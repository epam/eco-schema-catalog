/*
 * Copyright 2024 EPAM Systems
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
import React from 'react';
import PropTypes from "prop-types";
import './SchemaCompatibilityTypeSelector.scss'
import ReactTooltip from "react-tooltip";

export default class SchemaCompatibilityTypeSelector extends React.Component {
    static propTypes = {
        level: PropTypes.string,
        globalCompatibilityLevel: PropTypes.bool,
    }
    render() {

        const { level, globalCompatibilityLevel } = this.props;

        return (<React.Fragment>
                <h3 className="subject compatibility-type"
                    data-tip={globalCompatibilityLevel ?
                        'The compatibility level is implicitly inherited from cluster defaults.' :
                        'The compatibility level is explicitly set on a subject level.'}
                    data-for="compatibility-type">
                    {level}
                    ({globalCompatibilityLevel ? 'implicit' : 'explicit'})
                </h3>
            <ReactTooltip id="compatibility-type" place="top"/>
        </React.Fragment>
       );
    }
}