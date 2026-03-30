#!/bin/sh

# Copyright © 2026 Petr Panteleyev
# SPDX-License-Identifier: BSD-2-Clause
if [ -z "$1" ]
then
  echo "Usage: install.sh <install dir>"
  exit
fi

LAUNCH_DIR=$(cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)
INSTALL_DIR=$(realpath -m "$1")/password-manager

echo -n "Installing into $INSTALL_DIR... "
mkdir -p $INSTALL_DIR
rm -rf $INSTALL_DIR/*
cp -r $LAUNCH_DIR/../target/dist/Password\ Manager/* $INSTALL_DIR
echo "done"

echo -n "Creating desktop entry... "
echo "[Desktop Entry]
Type=Application
Version=1.5
Name=Password Manager
Name[ru_RU]=Менеджер паролей
Comment=Application to store passwords and other sensitive information
Comment[ru_RU]=Хранение паролей и другой секретной информации
Icon=$INSTALL_DIR/lib/Password\sManager.png
Exec=\"$INSTALL_DIR/bin/Password Manager\"
Categories=Utility;Java;
" > $HOME/.local/share/applications/password-manager.desktop
echo "done"
