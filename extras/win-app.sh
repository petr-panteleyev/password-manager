#!/bin/sh

rm -rf target/dist

$JPACKAGE_HOME/bin/jpackage \
    --module password.manager/org.panteleyev.pwdmanager.PasswordManagerApplication \
    --runtime-image "$JAVA_HOME" \
    --verbose \
    --dest target/dist \
    -p target/jmods \
    --java-options "-Dfile.encoding=UTF-8" \
    --icon icons/icons.ico \
    --name "Password Manager" \
    --app-version 20.1.1 \
    --vendor panteleyev.org \
    --win-menu \
    --win-dir-chooser \
    --win-upgrade-uuid 4a8438d2-f56f-4a5a-bfbe-5cf74ea70685 \
    --win-menu-group "panteleyev.org"