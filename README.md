# ConfigAPI

[![Download](https://api.bintray.com/packages/siroshun09/maven/ConfigAPI/images/download.svg) ](https://bintray.com/siroshun09/maven/ConfigAPI/_latestVersion)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/Siroshun09/ConfigAPI/Java%20CI)
![GitHub](https://img.shields.io/github/license/Siroshun09/ConfigAPI)
[![Bintray](https://img.shields.io/bintray/v/siroshun09/maven/ConfigAPI?color=orange&label=Javadoc)](https://siroshun09.github.io/ConfigAPI/)

A configuration library for Spigot and BungeeCord.

## Requirements

- Java 8+

## Usage (Maven)

Javadoc is [here](https://siroshun09.github.io/ConfigAPI/)

### Maven Repository

```xml
    <repository>
        <id>jcenter</id>
        <url>https://jcenter.bintray.com</url>
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

This project is under the Apache License version 2.0. Please see [LICENSE](LICENSE) for more info.

Copyright © 2020, Siroshun09