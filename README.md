# Password Manager

[![BSD-2 license](https://img.shields.io/badge/License-BSD--2-informational.svg)](LICENSE)
[![Licence](https://img.shields.io/badge/Java-14-orange?logo=java)](https://www.oracle.com/java/technologies/javase-downloads.html)

Desktop application to store passwords, credit card numbers and other sensitive information. 
Application uses 256-bit AES encryption.

![Screenshot](docs/main-window.png)

## Security Considerations

Application enforces security via file encryption only. Application makes no effort to counter attacks targeted 
to user account, operating system or hardware including RAM.

## Build

Configure [Maven toolchain](http://maven.apache.org/guides/mini/guide-using-toolchains.html) to provide ```jdk``` version ```14```.

Execute the following:
```shell script
$ mvn clean package
```

Application JAR and all dependencies will be placed in ```target/jmods```.

## Run

```shell script
$ mvn javafx:run
```

To open specific file add ```-Dpassword.file=<file>``` to the command line.

## Binary Packages

To build binary installers perform the following steps:
* On Microsoft Windows: install [WiX Toolset](https://wixtoolset.org/releases/), add its binary directory to ```PATH``` 
environment variable
* Execute the following commands:

```shell script
$ mvn clean package
$ mvn jpackage:jpackage@mac
  or
$ mvn jpackage:jpackage@win
```

Installation packages will be found in ```target/dist``` directory.

## Support

There is no support for this application.