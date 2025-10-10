# Build

Build requires JDK 25 and JavaFX 25.

```shell script
export JAVA_HOME=/path/to/jdk-25
./mvnw clean verify
```

Application JAR and all dependencies will be placed in ```target/jmods```.

# Run

```shell
./mvnw exec:exec@run
```

To open specific file add ```-Dpassword.file=<file>``` to the command line.

# Binary Distribution

## Step 1: Custom Image

Download and unpack [JavaFX JMODs distribution](https://jdk.java.net/javafx25/).

```shell
export JAVAFX_JMODS=/path/to/javafx-jmods-25
./mvnw -DskipTests=true clean verify jlink:jlink
```

Custom image will be found in ```target/jlink``` directory.

## Step 2: Installation

### OS X and MS Windows

```shell
./mvnw -DskipTests=true clean verify jlink:jlink jpackage:jpackage
```

Installer packages will be found in ```target/dist``` directory.

### Linux

```jpackage``` is currently broken on Linux such that produced application may crash with SIGSEGV.

Use the following script instead:

```shell
sudo ./bin/install.sh /opt
```

Application image will be installed into ```/opt/password-manager``` together with icon and .desktop file.
