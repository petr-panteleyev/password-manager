#!/bin/sh

rm -rf target/dist

$JPACKAGE_HOME/bin/jpackage \
    --module password.manager/org.panteleyev.pwdmanager.PasswordManagerApplication \
    --runtime-image "$JAVA_HOME" \
    --dest target/dist \
    -p target/jmods \
    --java-options "-Dfile.encoding=UTF-8" \
    --icon icons/icons.icns \
    --name "Password Manager" \
    --app-version 20.1.0 \
    --vendor panteleyev.org
