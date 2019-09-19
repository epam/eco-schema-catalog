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
import React, { Component } from 'react';
import { Route, Switch } from 'react-router-dom';
import PropTypes from 'prop-types';

import Alert from '../Alert';
import Header from '../Header/CommonHeader/CommonHeader';
import Footer from '../Footer/CommonFooter/CommonFooter';
import ModalRoot from '../ModalRoot/ModalRoot';
import ConfirmModal from '../ModalRoot/ConfirmModal';
import CreateSchemaModal from '../ModalRoot/CreateSchemaModal';
import TestSchemaModal from '../ModalRoot/TestSchemaModal';
import SchemasConatiner from '../Pages/SchemasContainer';
import SchemaContainerFirst from '../Pages/SchemaContainer/SchemaContainerFirst';
import SchemaConatinerExpanded from '../Pages/SchemaContainer/SchemaContainerExpanded';
import NoMatch from '../Pages/NoMatch';
import './App.scss';

export default class App extends Component {
  static propTypes = {
    getQueryExamples: PropTypes.func,
    schemas: PropTypes.array,
    isExpandedSchema: PropTypes.bool,
  }

  componentDidMount() {
    const { getQueryExamples } = this.props;
    getQueryExamples();
  }

  getContainer = () => {
    const { schemas, isExpandedSchema } = this.props;
    if (isExpandedSchema) {
      return <SchemaConatinerExpanded />;
    }
    if (!isExpandedSchema && !schemas.length) {
      return <SchemaContainerFirst />;
    }
    return <SchemasConatiner />;
  }

  render() {
    return (
      <div className="schema-catalog">
        <Header />
        <Alert />
        <main>
          <Switch>
            <Route exact path="/" component={SchemasConatiner} />
            <Route
              exact
              path="/schema/:subject/:version?"
              render={this.getContainer}
            />
            <Route path="*" component={NoMatch} />
          </Switch>
        </main>
        <Footer />

        <ModalRoot>
          <ConfirmModal />
          <CreateSchemaModal />
          <TestSchemaModal />
        </ModalRoot>

      </div>
    );
  }
}
