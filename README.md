# ConfigAPI

[![Download](https://api.bintray.com/packages/siroplugins/maven-repo/ConfigAPI/images/download.svg) ](https://bintray.com/siroshun/maven-repo/ConfigAPI/_latestVersion)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/SiroPlugins/ConfigAPI/Java%20CI)
![GitHub](https://img.shields.io/github/license/SiroPlugins/ConfigAPI)
[![Bintray](https://img.shields.io/bintray/v/siroplugins/maven-repo/ConfigAPI?color=orange&label=Javadoc)](https://siroplugins.github.io/ConfigAPI/)

A library for handling configuration files with Spigot and BungeeCord.

Currently only Yaml files can be used.

## Requirements

- Java 8+

## Usage (Maven)

Javadoc is [here](https://siroplugins.github.io/ConfigAPI/)

### Maven Repository

```xml
        <repository>
            <id>bintray-siroplugins-maven-repo</id>
            <url>https://dl.bintray.com/siroplugins/maven-repo</url>
        </repository>
```

### For Bukkit (Spigot)

```xml
        <dependency>
            <groupId>com.github.siroshun09.configapi</groupId>
            <artifactId>bukkit</artifactId>
            <version>2.0.1</version>
            <scope>compile</scope>
        </dependency>
```

### For BungeeCord

```xml
        <dependency>
            <groupId>com.github.siroshun09.configapi</groupId>
            <artifactId>bungee</artifactId>
            <version>2.0.1</version>
            <scope>compile</scope>
        </dependency>
```

### Only interfaces

```xml
        <dependency>
            <groupId>com.github.siroshun09.configapi</groupId>
            <artifactId>common</artifactId>
            <version>2.0.1</version>
            <scope>compile</scope>
        </dependency>
```

## License

This project is licensed under the permissive MIT license. Please see [LICENSE](LICENSE) for more info.

Copyright © 2020, Siroshun09