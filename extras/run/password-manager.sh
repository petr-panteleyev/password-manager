#!/bin/sh

java --module-path `dirname $0` -Dfile.encoding=UTF-8 -m password.manager/org.panteleyev.pwdmanager.PasswordManagerApplication
