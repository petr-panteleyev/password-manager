# Building Password Manager
## Prerequisites
### Source Code
The following repositories must be cloned to build Password Manager:

1. git clone https://github.com/petr-panteleyev/java-utilities.git utilities
2. git clone https://github.com/petr-panteleyev/java-crypto.git crypto
3. git clone https://github.com/petr-panteleyev/java-password-manager.git password-manager

Crypto is available from Maven Central which means it should be cloned only if full sources are required for IDE project.

### Temporary Maven repository

In order to use custom jar files with module-info setup additional Maven repository in settings.xml and make it appear first:

```
<repository>
  <id>panteleyev.org</id>
  <url>http://www.panteleyev.org/maven/</url>
</repository>
```

## Building Dependencies

```
cd utilities
mvn install
```

## Building JAR File

```
cd password-manager
mvn package
```

## Building Native Packages

```
cd password-manager
mvn clean package
mvn exec:exec@<native-dist>
```

Where &lt;native-dist> depends on native OS and packaging.

`dist-mac` produces DMG file. Its content can be copied to the Applications folder as is.

`dist-win` produces EXE file with a simple installer. This option requires additional software. Please refer to
[javapackager](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javapackager.html) documentation for details.

`dist-linux` produces DEB file. This option was tested on Ubuntu.

`dist-rpm` produces RPM file. This option was tested on OpenSUSE Leap 42.2.

Resulting package can be found in `password-manager/target/dists/bundles`.
