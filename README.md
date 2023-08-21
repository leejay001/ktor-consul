
# Ktor Consul Plugin

This plugin allows easy integration of Consul for service discovery and registration in Ktor applications.

## Requirements

Before using this plugin, please make sure you have Consul installed and running properly. See [Consul Installation Guide](https://developer.hashicorp.com/consul/docs/install?spm=a2c6h.12873639.article-detail.73.b1da281bE9Cdmo) for details.

## Usage

To use the plugin, call the `consul` function and configure as needed:

```kotlin
fun Application.configureConsul(){

    consul {
        config.withHttps(false) 
            .withConsulBuilder {
               // consul builder config
            }
            
            .withRegistrationBuilder {
               // registration builder config
            }
    }

}
```

This will register the Ktor service in Consul for service discovery.

See [Consul.kt](src/main/kotlin/com/lee/consul/plugin/Consul.kt) for more details on configuration options.

## Installation

Add the following dependency in your `build.gradle`:

```groovy
implementation 'com.lee:consul:0.0.1'
```

Or in your `pom.xml`:

```xml
<dependency>
  <groupId>com.lee</groupId>
  <artifactId>consul</artifactId>
  <version>0.0.1</version>
</dependency>
```

## Contributing

Contributions are welcome! Please create an issue or submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
