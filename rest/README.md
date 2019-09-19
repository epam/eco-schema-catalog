# Eco Schema Catalog REST

It's a Spring Boot web application, exposes RESTful interface for interaction of third-party services and applications with the Schema Catalog.

## Minimum configuration file

**application.properties**
```
eco.schemacatalog.store.schemaRegistryUrl=http://schema-registry:8081
eco.schemacatalog.store.schema.kafka.bootstrapServers=kafka:9092
eco.schemacatalog.store.metadata.kafka.bootstrapServers=kafka:9092
```

or **application.yml**
```
eco:
    schemacatalog:
        store:
            schemaRegistryUrl: http://schema-registry:8081
            schema:
                kafka:
                    bootstrapServers: kafka:9092
            metadata:
                kafka:
                    bootstrapServers: kafka:9092
```

## Running as standalone

Prerequisites:
* Java 8+
* [Confluent Platform (Kafka)](https://www.confluent.io/)
* [Schema Registry](https://www.confluent.io/confluent-schema-registry/)

Build and run:
```
git clone git@github.com:epam/eco-schema-catalog.git
cd /eco-schema-catalog/rest
mvn clean package
java -jar ./target/schema-catalog-rest-<version>.jar --spring.config.location=file://<path-to-config-file>
```

## Running in docker

Prerequisites:
* [Docker](https://www.docker.com/get-started)

Build the image:
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

Follow the link to open Schema Catalog REST API (swagger): [http://localhost:8082/swagger-ui.html#](http://localhost:8082/swagger-ui.html#)

If you need to reference some files from config file you can just mount them with
`-v` option and if you want to tune JVM consider `-e 'JAVA_OPTS=<some JVM options>'` .
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
`eco.schemacatalog.store.schemaRegistryUrl` | URL to the Schema Registry REST API. | 
`eco.schemacatalog.store.schema.kafka.bootstrapServers` | A comma-separated list of Kafka brokers used by the Schema Registry to store schemas. |
`eco.schemacatalog.store.schema.kafka.bootstrapTimeoutInMs` | Max duration in milliseconds for bootstrapping data from the Schema Registry schema storage (topic). If timeout is too small, you may observe stale data for some time (gets consistent eventually) after service is started. | 60000
`eco.schemacatalog.store.schema.kafka.consumerConfig.<property>=<value>` | Kafka [consumer properties](https://kafka.apache.org/10/documentation.html#consumerconfigs) to connect to the Schema Registry schema storage (topic). |
`eco.schemacatalog.store.metadata.kafka.bootstrapServers` | A comma-separated list of Kafka brokers used by Schema Catalog to store metadata. In most cases should contain the same value as `eco.schemacatalog.store.schema.kafka.bootstrapServers`. |
`eco.schemacatalog.store.metadata.kafka.bootstrapTimeoutInMs` | Max duration in milliseconds for bootstrapping data from the Schema Catalog metadata storage (topic). If timeout is too small, you may observe stale data for some time (gets consistent eventually) after service is started. | 60000
`eco.schemacatalog.store.metadata.kafka.consumerConfig.<property>=<value>` | Kafka [consumer properties](https://kafka.apache.org/10/documentation.html#consumerconfigs) to connect to the Schema Catalog metadata storage (topic). |
`eco.schemacatalog.store.metadata.kafka.producerConfig.<property>=<value>` | Kafka [producer properties](https://kafka.apache.org/10/documentation.html#producerconfigs) to connect to the Schema Catalog metadata storage (topic). |

## License

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
