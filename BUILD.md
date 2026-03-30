# Build

Prerequisites:
- JDK 25+
- JavaFX 26
- Maven 3.9.14

```shell script
export JAVA_HOME=/path/to/jdk-25
mvn clean verify
```

Application JAR and all dependencies will be placed in ```target/jmods```.

# Run

```shell
mvn exec:exec@run
```

To open specific file add ```-Dpassword.file=<file>``` to the command line.

# Binary Distribution

Download and unpack [JavaFX JMODs distribution](https://jdk.java.net/javafx26/).

```shell
export JAVAFX_JMODS=/path/to/javafx-jmods-26
mvn -DskipTests=true clean verify jlink:jlink jpackage:jpackage
```

## OS X and MS Windows

On these platfors ```target/dist``` directory will contain an installation package.

## Linux

On Linux ```target/dist``` directory will contain an application image that can be moved to the desired location
and launched as ```bin/Password\ Manager```.

There is a convenience script ```bin/install.sh``` that can be used to automatically install and create desktop link 
file ```$HOME/.local/share/applications/password-manager.desktop```

**Example**:

```shell
./bin/install.sh ~/apps
```

An application will be installed into ```~/apps/password-manager``` directory.
