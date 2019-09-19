# Eco Schema Catalog FTS

It's a Spring Boot auto-configuration library, provides service that enables Full-Text Search (FTS) capabilities against the data in the [Schema Registry](https://www.confluent.io/confluent-schema-registry/).

The library can be obtained from the Maven by adding the following dependency in the pom.xml:

```
<dependency>
    <groupId>com.epam.eco.schemacatalog</groupId>
    <artifactId>schema-catalog-fts</artifactId>
    <version>${project.version}</version>
</dependency>

```

## Usage

To start using it, just add the corresponding jar on the classpath of your Spring Boot application, all the necessary beans are automatically created and wired to the application context.

```
@Autowired
private SchemaDocumentRepository schemaDocumentRepository;
```

## Minimum configuration

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

## License

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
