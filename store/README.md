# Eco Schema Catalog Store

Eco Schema Catalog Store is a Spring Boot auto-configuration library that provides service to access data in the [Schema Registry](https://www.confluent.io/product/confluent-platform/data-compatibility/) store and manage metadata.

The library can be obtained from Maven by adding the following dependency in the pom.xml:

```
<dependency>
    <groupId>com.epam.eco.schemacatalog</groupId>
    <artifactId>schema-catalog-store</artifactId>
    <version>${project.version}</version>
</dependency>

```

## Usage

To start using Eco Schema Catalog Store, add the corresponding jar on the classpath of your Spring Boot application, all the necessary beans are automatically created and wired to the application context.
```
@Autowired
private SchemaCatalogStore schemaCatalogStore;
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

Eco Schema Catalog Store is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
