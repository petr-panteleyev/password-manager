# Менеджер паролей

[![GitHub](https://img.shields.io/github/license/petr-panteleyev/password-manager)](LICENSE)
[![Java](https://img.shields.io/badge/Java-18-orange?logo=java)](https://jdk.java.net/18/)
[![JavaFX](https://img.shields.io/badge/JavaFX-18-orange?logo=java)](https://openjfx.io/)

Приложение для хранения паролей, номеров банковских карт и прочей секретной информации.
Используется алгоритм шифрования 256-bit AES.

![Screenshot](docs/main-window.png)

## Защита информации

Приложение реализует защиту информации исключительно посредством шифрования файла. Не придпринимается никаких усилий
по защите от атак, направленных на пользователя операционной системы, а также аппаратное обеспечение, например,
оперативную память.

## Как собрать

Убедитесь, что переменная окружения ```JAVA_HOME``` указывает на JDK 18.

Выполните следующую команду:

```shell script
mvn clean verify
```

JAR-файл приложения и все зависимости будут находиться в каталоге ```target/jmods```.

## Как запустить

```shell script
mvn javafx:run
```

Чтобы открыть конкретный файл с паролями добавьте ```-Dpassword.file=<file>``` к командной строке.

## Бинарные пакеты

Чтобы собрать пакет для установки приложения выполните следующие шаги:
* На Microsoft Windows: установите [WiX Toolset](https://wixtoolset.org/releases/), добавьте каталог с утилитами в
  переменную окружения ```PATH```
* Выполните одну из следующих команд в зависимости от операционной системы:

```shell script
mvn clean verify jpackage:jpackage@mac
```

```shell script
mvn clean verify jpackage:jpackage@win
```

```shell script
mvn clean verify jpackage:jpackage@linux
```

Пакет для установки будет находиться в каталоге ```target/dist```.

## Поддержка

Поддержка данного приложения не осуществляется.
