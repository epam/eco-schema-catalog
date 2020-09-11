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

const dotenv = require('dotenv');
const express = require('express');
const compression = require('compression');
const path = require('path');
const https = require('https');
const { createProxyMiddleware } = require('http-proxy-middleware');
const helmet = require('helmet');

dotenv.config();

const app = express();
app.set('trust proxy');
app.use(helmet());
app.use(compression());

const apiConfig = {
  target: process.env.TARGET_API,
  pathRewrite: { [`^${process.env.BASE_HREF}/api`]: '/api' },
  changeOrigin: true,
};

if (process.env.NODE_ENV === 'production') {
  const httpsAgent = new https.Agent({ rejectUnauthorized: false });
  apiConfig.agent = httpsAgent;
}

const proxySchemaCatalog = createProxyMiddleware(apiConfig);

app.get('*/info', (req, res) => {
  res.status(200).end();
});
app.get('*/health', (req, res) => {
  res.send('ok');
});

app.use(express.static(path.join(__dirname, 'public')));
app.use(process.env.BASE_HREF, express.static(path.join(__dirname, 'public')));

app.use(`${process.env.BASE_HREF}/api`, proxySchemaCatalog);

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, './public/index.html'));
});

const port = process.env.PORT || 8080;
app.listen(port, () => {
  console.log(`Running in - ${process.env.NODE_ENV}`);
  console.log(`Target REST API - ${process.env.TARGET_API}`);
  console.log(`With base href - ${process.env.BASE_HREF}`);
  console.log(`Running Express on port - ${port}`);
});
