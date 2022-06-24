# Eco Schema Catalog

Eco Schema Catalog is a set of services, components, and utilities aimed at providing additional features on top of the [Schema Registry](https://docs.confluent.io/platform/current/schema-registry/index.html).

With Eco Schema Catalog you can use:

* Web UI to search, view, evolve, and configure schemas.
* Various services to work with schemas.
* Utilities, helpers, and other classes that extend and augment the Schema Registry library.

The currently supported version of Confluent is [7.0.1](https://docs.confluent.io/platform/7.0.1/schema-registry/index.html)

## Project structure

The repository contains the following packages:

* [rest](/rest) - RESTful interface (backend)
* [ui](/ui) - web UI (frontend)
* [commons](/commons) - common models, utilities, extensions, etc
* [store](/store) - service to access data in the [Schema Registry](https://docs.confluent.io/platform/current/schema-registry/index.html) store, manage metadata, etc
* [fts](/fts) - service that enables Full-Text Search (FTS) capabilities against the store
* [client](/client) - REST client

## Building artifacts

To build artifacts, run the following command sequence:
```
git clone git@github.com:epam/eco-schema-catalog.git
cd eco-schema-catalog
mvn clean package
```
To skip tests, JavaDocs, and static code analysis, run:
```
mvn clean package -PpackageOnly
```

## Quick start

The prerequisites for the quick start include:
* [Docker](https://www.docker.com/get-started)
* [Docker Compose](https://docs.docker.com/compose/install/)

### Installation to connect to an existing Confluent Platform

The installation consists of two services:
* Schema Catalog REST
* Schema Catalog UI

Note: Specify suitable values for  `SCHEMA_REGISTRY_URL` and `KAFKA_SERVERS_URL`.

For Linux, run the following command sequence:
```
git clone git@github.com:epam/eco-schema-catalog.git
cd eco-schema-catalog
export SCHEMA_REGISTRY_URL="http://schema-registry:8081"
export KAFKA_SERVERS_URL="kafka:9092"
docker-compose -f docker/docker-compose.yaml up
```

For Windows (Powershell), run the following command sequence:
```
git clone git@github.com:epam/eco-schema-catalog.git
cd eco-schema-catalog
$env:SCHEMA_REGISTRY_URL="http://schema-registry:8081"
$env:KAFKA_SERVERS_URL="kafka:9092"
docker-compose -f docker/docker-compose.yaml up
```

To open Schema Catalog web interface, go to [http://localhost:8282](http://localhost:8282)

To stop all services, run:
```
docker-compose -f docker/docker-compose.yaml down
```

### All-in-one installation

The installation consists of:
* Zookeeper
* Kafka
* Schema Registry
* Schema Catalog REST
* Schema Catalog UI

Run the following command sequence:
```
git clone git@github.com:epam/eco-schema-catalog.git
cd eco-schema-catalog
docker-compose -f docker/docker-compose-all.yaml up
```

To open Schema Catalog web interface, go to [http://localhost:8282](http://localhost:8282)

To stop all services, run:
```
docker-compose -f docker/docker-compose-all.yaml down
```

## Compatibility matrix

Eco Schema Catalog | Confluent Platform | Kafka
---  | --- | ---
2.0.x | 7.0.1 | 3.0.x
1.3.x | 5.3.x | 2.3.x
1.2.x | 5.2.x | 2.2.x
1.1.x | 5.1.x | 2.1.x
1.0.x | 5.0.x | 2.0.x
0.1.x | 4.0.x | 1.0.x

## License

Eco Schema Catalog is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
