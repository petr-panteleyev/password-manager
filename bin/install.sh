#!/bin/sh

if [ -z "$1" ]
then
  echo "Usage: sudo install.sh <install dir>"
  exit
fi

LAUNCH_DIR=$(cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)
INSTALL_DIR=$1/password-manager

echo -n "Installing into $INSTALL_DIR..."
mkdir -p $INSTALL_DIR
rm -rf $INSTALL_DIR/*
cp $LAUNCH_DIR/../icons/icon.png $INSTALL_DIR
cp -r $LAUNCH_DIR/../target/jlink/* $INSTALL_DIR

echo "
#!/bin/sh
$INSTALL_DIR/bin/java \\
  --module password.manager/org.panteleyev.pwdmanager.PasswordManagerApplication
" > $INSTALL_DIR/password-manager.sh

chmod +x $INSTALL_DIR/password-manager.sh

echo "[Desktop Entry]
Type=Application
Version=1.5
Name=Password Manager
Name[ru_RU]=Менеджер паролей
Comment=Application to store passwords and other sensitive information
Comment[ru_RU]=Хранение паролей и другой секретной информации
Icon=$INSTALL_DIR/icon.png
Exec=/bin/sh $INSTALL_DIR/password-manager.sh
Categories=Utility;Java;
" > $INSTALL_DIR/password-manager.desktop

echo "done"
