![Verify](https://github.com/RedHatInsights/kafka-config-providers/workflows/Verify/badge.svg)

# Kafka Config Providers

This project provides implementations of Kafka's [ConfigProvider](https://kafka.apache.org/27/javadoc/org/apache/kafka/common/config/provider/ConfigProvider.html) interface.
These implementations make it possible to configure Kafka/Kafka Connect using various configuration sources.

The library can be downloaded from Maven central using `com.redhat.insights.kafka:config-providers:${version}` coordinates.

## PlainFileConfigProvider

This implementation is similar to the built-in [FileConfigProvider](https://kafka.apache.org/27/javadoc/org/apache/kafka/common/config/provider/FileConfigProvider.html).
Unlike FileConfigProvider, PlainFileConfigProvider does not require the configuration file to be a properties file.
Instead, the entire file content is read and used as a configuration value for the given key.

This is useful when working with [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/) that are mounted to containers.

Consider the following Kubernetes secret:

```yaml
- apiVersion: v1
  kind: Secret
  metadata:
    name: database
  stringData:
    db.host: database.database.svc.cluster.local
    db.port: 5432
    db.name: inventory
    db.user: inventory
    db.password: secret
```

When the secret is mounted to a container's filesystem, a file structure is created, such as:

```shell
/opt/kafka/external-configuration/database
├── db.host
├── db.name
├── db.password
├── db.port
└── db.user
```

FileConfigProvider then allows for these individual files to be referenced from Kafka configuration.

First, registed FileConfigProvider with Kafka or Kafka Connect:

```properties
config.providers: file
config.providers.file.class: com.redhat.insights.kafka.config.providers.PlainFileConfigProvider
```

Then, reference the secret files in Kafka or Kafka Connect configuration:

```json
{
    "config": {
        "database.hostname": "${file:/opt/kafka/external-configuration/database/db.host}",
        "database.port": "${file:/opt/kafka/external-configuration/database/db.port}",
        "database.user": "${file:/opt/kafka/external-configuration/database/db.user}",
        "database.password": "${file:/opt/kafka/external-configuration/database/db.password}",
        "database.dbname": "${file:/opt/kafka/external-configuration/database/db.name}"

        // the rest of the connector configuration left out for brevity
    }
}
```

## EnvironmentConfigProvider

This reads the value from an environment variable. Useful when using the [env option of externalConfiguration](https://strimzi.io/docs/operators/0.22.1/using.html#property-kafka-connect-external-env-reference).

Add the following to the KafkaConnect custom resource's `spec.config`:


```properties
config.providers: env
config.providers.file.class: com.redhat.insights.kafka.config.providers.EnvironmentConfigProvider
```

Then, reference the environment variables in a Kafka or Kafka Connect configuration:

```json
{
    "config": {
        "database.hostname": "${env:HBI_DB_HOSTNAME}"
    }
}
```
