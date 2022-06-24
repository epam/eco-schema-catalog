# Eco Schema Catalog FTS

Eco Schema Catalog FTS is a Spring Boot auto-configuration library, provides service that enables Full-Text Search (FTS) capabilities against the data in the [Schema Registry](https://docs.confluent.io/platform/current/schema-registry/index.html).

The library can be obtained from Maven by adding the following dependency in the pom.xml:

```
<dependency>
    <groupId>com.epam.eco.schemacatalog</groupId>
    <artifactId>schema-catalog-fts</artifactId>
    <version>${project.version}</version>
</dependency>

```

## Usage

To start using Eco Schema Catalog FTS, add the corresponding jar on the classpath of your Spring Boot application, all the necessary beans are automatically created and wired to the application context.

```
@Autowired
private SchemaDocumentRepository schemaDocumentRepository;
```

## Minimum configuration

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

## License

Eco Schema Catalog FTS is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
