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
import React from 'react';
import { render } from 'react-dom';
import thunk from 'redux-thunk';
import { createStore, applyMiddleware } from 'redux';
import { ConnectedRouter, routerMiddleware } from 'connected-react-router';
import { createBrowserHistory } from 'history';
import { Provider } from 'react-redux';
import { createLogger } from 'redux-logger';
import createRootReducer from './reducers';
import App from './components/App';

import 'font-awesome/css/font-awesome.min.css';
import '../../node_modules/normalize.css/normalize.css';
import './styles/index.scss';

const history = createBrowserHistory({
  basename: process.env.BASE_HREF,
});

const logger = createLogger({
  collapsed: true,
});

const middlewares = [thunk, routerMiddleware(history)];

if (process.env.NODE_ENV === 'development') {
  middlewares.push(logger);
}

const store = createStore(createRootReducer(history), applyMiddleware(...middlewares));

const Wrapp = () => (
  <Provider store={store}>
    <ConnectedRouter history={history}>
      <App />
    </ConnectedRouter>
  </Provider>
);

render(<Wrapp />, document.getElementById('root'));
