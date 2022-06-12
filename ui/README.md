# Eco Schema Catalog UI

Eco Schema Catalog UI is a UI web part of the Schema Catalog application. It enables users to view Avro schemas and makes the schemas more visual.

## Main features:

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

## Software requirements

Software requirements include:

* [Node.js](https://nodejs.org/en/) 14.18.1
* [npm](https://www.npmjs.com/) 6.14.15
* [Docker](https://www.docker.com/get-started) 19.03

## Technical stack

The required technical stack includes:

* Node.js
* React
* Redux
* SASS
* Jest

## Running Eco Schema Catalog UI

You can run Eco Schema Catalog UI using these three methods:

* From source
* From source in development mode
* With Docker

### Running from source

To run Eco Schema Catalog UI from source:

1. Modify .env file before building with your own values.
2. Install all dependencies: <br /> `npm install`
3. Run build of production version: <br /> `npm run build`
4. Start serving built files with: <br /> `npm run start`

### Running in development mode

To run Eco Schema Catalog UI from source in development mode:

1. Modify .env file before building with your own values.
2. Install all dependencies: <br /> `npm install`
3. Run build of development version: <br />  `npm run build:dev` <br />  or in watching mode: <br /> `npm run build:watch`.
4. Run new console instance and start serving built files with: <br /> `npm run start:dev`

### Running in docker

The prerequisite includes [Docker](https://www.docker.com/get-started).

To build the image, run the following command sequence:
```
git clone git@github.com:epam/eco-schema-catalog.git
cd /eco-schema-catalog/ui
docker build -t epam/eco-schema-catalog-ui:latest .
```

Run the container:
```
docker run --name eco-schema-catalog-ui \
 --rm \
 -p 8282:8282 \
 -e PORT=8282 \
 -e TARGET_API=http://schema-catalog-rest:8082 \
 epam/eco-schema-catalog-ui:latest
```

#### Dockerhub repo

[https://hub.docker.com/r/epam/eco-schema-catalog-ui/tags](https://hub.docker.com/r/epam/eco-schema-catalog-ui/tags)

## Configuration

| Name       | Description                                                                                                                                                                                           | Default                    |
|------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------|
| PORT       | Port of UI app.                                                                                                                                                                                       | 8282                       |
| BASE_HREF  | Base URL of the app. No base URL by default. e.g. `/the/base`.                                                                                                                                        | ''                         |
| NODE_ENV   | Node.js environment variable. Development version turns on logging in web console. Also, development version allows to run the application without using `https`. Values: `development`, `production` | development                |
| TARGET_API | Endpoint of schema-catalog REST API                                                                                                                                                                   | http://schema-catalog-rest |
| GA_UA      | To apply a unique ID for global tag (gtag.js for google analytics). It is turned off by default.                                                                                                      | ''                         |

## All npm commands

The list of npm commands includes:

* `npm run start` - start serving assets with `node`.

* `npm run start:dev` - start serving assets with `nodemon`. It automatically restarts serving when something has changed.

* `npm run build` - build the production version in the `dist` folder.

* `npm run build:watch` - build the dev version of assets in the `dist` folder when changing files.

* `npm run lint` - syntax and formatting check with eslint.

* `npm run test` - single run all tests.

* `npm run test:watch` - run tests in watching mode.

## License

Eco Schema Catalog UI is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
