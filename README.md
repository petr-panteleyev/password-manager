# Password Manager

Desktop application to store passwords, credit card numbers and other sensitive information. 
Application uses 256-bit AES encryption.

![Screenshot](docs/main-window.png)

## Security Considerations

Application enforces security via file encryption only. Application makes no effort to counter attacks targeted 
to user account, operating system or hardware including RAM.

## Build and Run

JDK-14 is required to build and run the application.

### Build

Make sure Maven toolchain configuration ```toolchain.xml``` contains the following
definition:
```xml
<toolchain>
    <type>jdk</type>
    <provides>
        <version>14</version>
    </provides>
    <configuration>
        <jdkHome>/path/to/jdk-14</jdkHome>
    </configuration>
</toolchain>
```
Execute the following:
```shell script
$ mvn clean package
```

Application JAR and all dependencies will be placed in ```target/jmods```.

### Run

```shell script
$ mvn javafx:run
```

### Binary Packages

To build binary installers perform the following steps:
* On Microsoft Windows: install [WiX Toolset](https://wixtoolset.org/releases/), add its binary directory to ```PATH``` 
environment variable
* Execute the following commands:
```shell script
$ mvn clean package
$ mvn exec:exec@dist-mac
  or
$ mvn exec:exec@dist-win
```
Installation packages will be found in ```target/dist``` directory.

## Support

There is no support for this application.