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

## Building Dependencies

```
cd utilities
mvn install
```

## Building Standalone JAR File

```
cd password-manager
mvn package
```

## Building Native Packages

```
cd password-manager
mvn clean
mvn package
mvn exec:exec@<native-dist>
```

Where &lt;native-dist> depends on native OS and packaging.

`dist-mac` produces DMG file. Its content can be copied to the Applications folder as is.

`dist-win` produces EXE file with a simple installer. This option requires additional software. Please refer to
[javapackager](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javapackager.html) documentation for details.

`dist-linux` produces DEB file. This option was tested on Ubuntu.

`dist-rpm` produces RPM file. This option was tested on OpenSUSE Leap 42.2.

Resulting package can be found in `password-manager/target/dists/bundles`.
