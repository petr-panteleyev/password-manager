# Building Password Manager
## Prerequisites
### Source Code
The following repositories must be cloned to build Password Manager:

1. git clone https://github.com/petr-panteleyev/java-utilities.git utilities
2. git clone https://github.com/petr-panteleyev/java-crypto.git crypto
3. git clone https://github.com/petr-panteleyev/java-password-manager.git password-manager

Crypto is available from Maven Central which means it should be cloned only if full sources are required for IDE project.

### Encryption
JDK must be updated with [Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

### JAR Signing configuration
The following properties must be set in `settings.xml`:

```
<keystore.path>/path/to/keystore</keystore.path>
<keystore.alias>keystore-alias</keystore.alias>
<keystore.password>keystore-password</keystore.password>
```

## Building Dependencies

```
cd <utilities>
mvn install -f shared.xml
mvn install
```

## Building JAR File

```
cd <password-manager>
mvn package
```

## Building Signed JAR File

```
cd <password-manager>
mvn package -P sign
```

## Building Standalone JAR
Standalone (fat) JAR is always signed.

```
cd <password-manager>
mvn package -P fatjar
```
