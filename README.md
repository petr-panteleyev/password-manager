# Password Manager

![JDK](docs/java-24.svg)
[![License](docs/license.svg)](LICENSE)

Desktop application to store passwords, credit card numbers and other sensitive information. 
Application uses 256-bit AES encryption.

![Screenshot](docs/main-window.png)

## Security Considerations

Application enforces security via file encryption only. Application makes no effort to counter-attacks targeted 
to user account, operating system or hardware including RAM.

## Build

* Set ```JAVA_HOME``` to JDK 24+.
* Execute:

```shell script
./mvnw clean verify
```

Application JAR and all dependencies will be placed in ```target/jmods```.

## Run

```shell script
./mvnw exec:exec@run
```

To open specific file add ```-Dpassword.file=<file>``` to the command line.

## Custom Run-Time Image

```shell script
./mvnw jlink:jlink
```

Run-time image will be found in ```target/jlink``` directory.

## Binary Packages


```shell script
./mvnw jpackage:jpackage
```

Installation packages will be found in ```target/dist``` directory.

## Support

There is no support for this application.
