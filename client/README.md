# Eco Schema Catalog Client

It's a Spring Boot auto-configuration library, provides client for the Schema Catalog REST service. 

The library can be obtained from the Maven by adding the following dependency in the pom.xml:

```
<dependency>
    <groupId>com.epam.eco.schemacatalog</groupId>
    <artifactId>schema-catalog-client</artifactId>
    <version>${project.version}</version>
</dependency>
```

## Usage

To start using it, just add the corresponding jar on the classpath of your Spring Boot application, all the necessary beans are automatically created and wired to the application context.

```
@Autowired
private SchemaCatalogClient schemaCatalogClient;
```

## Minimum configuration

**application.properties**
```
eco.schemacatalog.client.schemaCatalogUrl=http://schema-catalog-rest:8080
```

or **application.yml**
```
eco:
    schemacatalog:
        client:
            schemaCatalogUrl: http://schema-catalog-rest:8080
```

## Configuration properties

Name | Description | Default
---  | ---         | --- 
`eco.schemacatalog.client.schemaCatalogUrl` | Eco Schema Catalog REST Url | 

## License

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)