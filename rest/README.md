# Eco Schema Catalog REST

Eco Schema Catalog REST is a Spring Boot web application that exposes RESTful interface for interaction of third-party services and applications with the Schema Catalog.

## Minimum configuration file

**application.properties**
```
eco.schemacatalog.store.schemaRegistryUrl=http://schema-registry:8081
eco.schemacatalog.store.kafka.bootstrapServers=kafka:9092
```

or **application.yml**
```
eco:
    schemacatalog:
        store:
            schemaRegistryUrl: http://schema-registry:8081
            kafka:
                bootstrapServers: kafka:9092
```

## Running Eco Schema Catalog REST 

You can run Eco Schema Catalog: 

* As a standalone service 

* In Docker 

### Running as standalone

The prerequisites include:
* Java 8+
* [Confluent Platform (Kafka)](https://www.confluent.io/)
* [Schema Registry](https://www.confluent.io/confluent-schema-registry/)

Run the following command sequence:
```
git clone git@github.com:epam/eco-schema-catalog.git
cd /eco-schema-catalog/rest
mvn clean package
java -jar ./target/schema-catalog-rest-<version>.jar --spring.config.location=file://<path-to-config-file>
```

### Running in docker

The prerequisite includes [Docker](https://www.docker.com/get-started).

To build the image, run the following command sequence:
```
git clone git@github.com:epam/eco-schema-catalog.git
cd /eco-schema-catalog/rest
docker build -f ./Dockerfile -t epam/schema-catalog-rest:latest ./../
```

Run the container:
```
docker run --name schema-catalog-rest \
 --rm \
 -p 8082:8082 \
 -v <path-to-config-file>:/app/config/application.properties \
 epam/schema-catalog-rest:latest
```

To open Schema Catalog REST API (swagger), go to [http://localhost:8082/swagger-ui.html#](http://localhost:8082/swagger-ui.html#)

### Note:

To reference files from the config file, it is possible to mount them with the
`-v` option. <br />
To tune JVM, use `-e 'JAVA_OPTS=<some JVM options>'`.
For example:
```
docker run --name schema-catalog-rest \
 --rm \
 -p 8082:8082 \
 -v <path-to-config-file>:/app/config/application.properties \
 -v <host-path-to-file-referenced-from-config>:<docker-path-to-file-referenced-from-config> \
 -m 3g \
 -e 'JAVA_OPTS=-Xms1g -Xmx1g' \
 epam/schema-catalog-rest:latest
```

## Configuration properties

Name | Description | Default
---  | ---         | --- 
`eco.schemacatalog.store.schemaRegistryUrl` | URL to the Schema Registry REST API. | `http://localhost:8081`
`eco.schemacatalog.store.kafka.bootstrapServers` | A comma-separated list of Kafka brokers used by the Schema Registry to store schemas. | `localhost:9092`
`eco.schemacatalog.store.kafka.bootstrapTimeoutInMs` | Max duration in milliseconds for bootstrapping data from the Schema Registry storage. If the timeout is too small, you may observe stale data for some time (it gets consistent eventually) after the service is started. | `60000`
`eco.schemacatalog.store.kafka.clientConfig[property]` | Common Kafka [client properties](https://kafka.apache.org/20/documentation.html#adminclientconfigs), used to connect to the Schema Registry schema storage (topic). |

## License

Eco Schema Catalog REST is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
