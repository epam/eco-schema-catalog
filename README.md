# Eco Schema Catalog

It's a set of services, components and utilities that aimed at providing additional features on top of the [Schema Registry](https://www.confluent.io/confluent-schema-registry/).

The currently supported version is [4.0.0](https://docs.confluent.io/4.0.0/schema-registry/docs/index.html)

## Overview

* Easy to use web UI to search/view/evolve/configure schemas
* Various services to work with schemas
* Utilities, helpers and other classes that extend and augment Schema Registry library

## Project structure

The repository contains the following packages:

* [rest](/rest) - RESTful interface (backend)
* [ui](/ui) - web UI (frontend)
* [commons](/commons) - common models, utilities, extensions, etc
* [store](/store) - service to access data in the [Schema Registry](https://www.confluent.io/confluent-schema-registry/) store, manage metadata, etc
* [fts](/fts) - service that enables Full-Text Search (FTS) capabilities against the store
* [client](/client) - REST client

## Build

```
git clone git@github.com:epam/eco-schema-catalog.git
cd eco-schema-catalog
mvn clean package
```

Skipping tests, JavaDocs and static code analysis:
```
mvn clean package -PpackageOnly
```

## Quick start

Prerequisites:
* [Docker](https://www.docker.com/get-started)
* [Docker Compose](https://docs.docker.com/compose/install/)

### Installation to connect to an existing Confluent Platform

Consists of:
* Schema Catalog REST
* Schema Catalog UI

Please specify suitable values for `SCHEMA_REGISTRY_URL` and `KAFKA_SERVERS_URL`.

On Linux:
```
git clone git@github.com:epam/eco-schema-catalog.git
cd eco-schema-catalog
export SCHEMA_REGISTRY_URL="http://schema-registry:8081"
export KAFKA_SERVERS_URL="kafka:9092"
docker-compose -f docker/docker-compose.yaml up
```

On Windows (Powershell):
```
git clone git@github.com:epam/eco-schema-catalog.git
cd eco-schema-catalog
$env:SCHEMA_REGISTRY_URL="http://schema-registry:8081"
$env:KAFKA_SERVERS_URL="kafka:9092"
docker-compose -f docker/docker-compose.yaml up
```

Follow the link to open Schema Catalog web interface: [http://localhost:8282](http://localhost:8282)

Stop all services:
```
docker-compose -f docker/docker-compose.yaml down
```

### All-in-one installation

Consists of:
* Zookeeper
* Kafka
* Schema Registry
* Schema Catalog REST
* Schema Catalog UI

```
git clone git@github.com:epam/eco-schema-catalog.git
cd eco-schema-catalog
docker-compose -f docker/docker-compose-all.yaml up
```

Follow the link to open Schema Catalog web interface: [http://localhost:8282](http://localhost:8282)

Stop all services:
```
docker-compose -f docker/docker-compose-all.yaml down
```

## License

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
