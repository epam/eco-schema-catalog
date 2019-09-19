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
import { UnControlled as CodeEditor } from 'react-codemirror2';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/material.css';
import 'codemirror/theme/neat.css';
import 'codemirror/mode/javascript/javascript.js';
import 'codemirror/addon/lint/lint';
import 'codemirror/addon/lint/json-lint';
import 'codemirror/addon/lint/lint.css';

import './SchemaEditor.scss';

const jsonlint = require('jsonlint-mod');

window.jsonlint = jsonlint;

export default class SchemaEditor extends Component {
  static propTypes = {
    onChangeValue: PropTypes.func,
    avro: PropTypes.string,
  }

  options = {
    mode: 'application/json',
    gutters: ['CodeMirror-lint-markers'],
    styleActiveLine: true,
    lineNumbers: true,
    line: true,
    lint: true,
  }

  render() {
    const { onChangeValue, avro } = this.props;

    return (
      <CodeEditor
        autoCursor={false}
        value={avro}
        options={this.options}
        onChange={(_editor, _data, value) => onChangeValue(value)}
      />
    );
  }
}
