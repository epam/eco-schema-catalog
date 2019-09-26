# Eco Schema Catalog UI

Eco Schema Catalog UI is a UI web part of Schema Catalog application. It enables users to view Avro schemas and makes the schemas more visual.

## Features

* Performing full-text search against schemas, query DSL, aggregations, backed by [Elasticsearch](https://www.elastic.co/).
* Viewing schema definition (table or json) and filtering schemas by name or type.
* Switching schema versions, tracking schema evolution history and difference between versions in [Unified format](https://en.wikipedia.org/wiki/Diff#Unified_format).
* Creating and evolving schemas.
* Testing schema compatibility, viewing compatibility issues.
* Deleting subject or version.
* Changing subject compatibility.
* Adding metadata (with simple markup language) to schemas or fields.
* Searching by metadata.

![](sc.gif)

## Requirements
* [Node.js](https://nodejs.org/en/) development version was v10.16.1
* [npm](https://www.npmjs.com/) development version was 6.9.0
* [Docker](https://www.docker.com/get-started)

## Stack
* Node.js
* React
* Redux
* SASS
* Jest

## Running from source
1. modify .env file before building with your own values.
2. install all dependencies `npm install`.
3. run build of production version `npm run build`.
4. start serving built files with `npm run start`.

## Running from source in development mode
1. modify .env file before building with your own values.
2. install all dependencies `npm install`.
3. run build of development version `npm run build:dev` or in watching mode `npm run build:watch`.
4. run new console instance and start serving built files with `npm run start:dev`

## Running with docker
1. modify .env file before building with your own values
2. `docker build -t epam/schema-catalog-ui:latest .`
3. `docker run  --name schema-catalog-ui --rm -p 8282:8282 epam/schema-catalog-ui:latest`

## Configuration
Name | Description | Default
---  | ---         | ---
PORT | Port of ui app | 8282
BASE_HREF | Base url of the app. No base url by default. e.g. `/the/base`  | ''
NODE_ENV | Node.js environment variable. Development version turn on logger of actions in web console. Also development version allows to run the application without applied `https`. Values: `development`, `production` | development.
TARGET_API | Endpoint of schema-catalog rest api | http://schema-catalog-rest
GA_UA | To apply a unique id for global tag (gtag.js for google analytics). Turn off by default | ''

## All npm commands
* `npm run start` - start serving assets with `node`.

* `npm run start:dev` - start serving assets with `nodemon`. It automatically restarts serving when something has changed.

* `npm run build` - build the production version in the `dist` folder.

* `npm run build:watch` - build the dev version of assets in the `dist` folder when changing files.

* `npm run lint` - syntax and formatting check with eslint.

* `npm run test` - single run all tests.

* `npm run test:watch` - run tests when changing files.

## License

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
