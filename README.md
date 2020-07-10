# bootique-dotenv
A Bootique module for setting Java system properties using a properties file (.env by default).

This project is inspired by [java-dotenv](https://github.com/cdimascio/java-dotenv).

# Setup

## Add bootique-dotenv to your build tool:

**Maven**
```xml
<dependency>
    <groupId>io.bootique.dotenv</groupId>
    <artifactId>bootique-dotenv</artifactId>
    <version>${bootique.version}</version>
</dependency>
```

where the Bootique version is at least 2.0-SNAPSHOT.

## Available commands

### DOTENV COMMANDS AS OPTIONS

There is just one command (dotenv) with an optional resource parameter that
defaults to classpath:.env. Please note that just one property file is
allowed just like java-dotenv prescribes.

This commmand:

```bash
java -jar ... -h
```

will show something like this:


```text
...
      -d, --dotenv
           Reads one (optional) property resource that is specified with a
           '--resource' option. The default is 'classpath:.env'.
...					 
```

## Start your application with this command

This library contains this io.bootique.dotenv.App main class:

```text
package io.bootique.dotenv;

import io.bootique.Bootique;

public class App {

    public static void main(String[] args) {
        // first launch the dotenv module with the default resource argument        
        Bootique
            .app("--dotenv")
            .module(DotenvModule.class)
            .createRuntime()
            .run();

        // Now continue with the rest after the default resource has been read
        Bootique.app(args).autoLoadModules().exec().exit();
    }
}
```

You can either copy the main class or you can add the main class to your Maven POM:

```text
  <properties>
    <main.class>io.bootique.dotenv.App</main.class>
  </properties>		
```
