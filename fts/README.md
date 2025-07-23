# Eco Schema Catalog FTS

Eco Schema Catalog FTS is a Spring Boot autoconfiguration library, provides service that enables Full-Text Search (FTS) capabilities against the data in the [Schema Registry](https://docs.confluent.io/platform/current/schema-registry/index.html).<br>
**Requires** Elasticsearch deployment, see [Elasticsearch](https://www.elastic.co/)<br>
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
spring.elasticsearch.rest.uris=localhost:9200
```

or **application.yml**
```
eco:
  schemacatalog:
    store:
      schemaRegistryUrl: http://schema-registry:8081
      kafka:
        bootstrapServers: kafka:9092
spring:
  elasticsearch:
    rest:
      uris: localhost:9200
```

The infrastructure example could be found in [docker-compose.yml](../docker-compose.yml), which also could be used for launching Manual integration tests.

## Adding new fields to existing index
#### 1. Update mapping of existing indices. i.e adding the new field to the existing mapping of 
indices.
```
PUT /schemacatalog_index/_mapping
{
"properties": {
    "new_field_name" : {
    "type" : "long"
    }   
  }
}
```
#### 2. Run an update_by_query to add the new field
```
POST /schemacatalog_index/_update_by_query?wait_for_completion=false
{
    "query": {
        "match_all": {}
    },
    "script": {   
        "source":"ctx._source.new_field_name=0",
        "lang": "painless"
    }
}
```

## License

Eco Schema Catalog FTS is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
