# Password Manager

[![BSD-2 license](https://img.shields.io/badge/License-BSD--2-informational.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://jdk.java.net/17/)
[![JavaFX](https://img.shields.io/badge/JavaFX-18-orange?logo=java)](https://openjfx.io/)

Desktop application to store passwords, credit card numbers and other sensitive information. 
Application uses 256-bit AES encryption.

![Screenshot](docs/main-window.png)

## Security Considerations

Application enforces security via file encryption only. Application makes no effort to counter attacks targeted 
to user account, operating system or hardware including RAM.

## Build

Make sure ```JAVA_HOME``` is set to JDK 17.

Execute the following:
```shell script
mvn clean verify
```

Application JAR and all dependencies will be placed in ```target/jmods```.

## Run

```shell script
mvn javafx:run
```

To open specific file add ```-Dpassword.file=<file>``` to the command line.

## Binary Packages

To build binary installers perform the following steps:
* On Microsoft Windows: install [WiX Toolset](https://wixtoolset.org/releases/), add its binary directory to ```PATH``` 
environment variable
* Execute one of the following commands depending on the platform:

```shell script
mvn clean package jpackage:jpackage@mac
```

```shell script
mvn clean package jpackage:jpackage@win
```

```shell script
mvn clean package jpackage:jpackage@linux
```

Installation packages will be found in ```target/dist``` directory.

## Support

There is no support for this application.
