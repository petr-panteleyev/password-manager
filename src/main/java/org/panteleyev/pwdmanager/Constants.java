/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;
import static org.panteleyev.fx.FxUtils.fxString;

public interface Constants {
    ResourceBundle RB = getBundle("org.panteleyev.pwdmanager.ui");
    ResourceBundle BUILD_INFO_BUNDLE = getBundle("org.panteleyev.pwdmanager.buildInfo");
    ResourceBundle FIELD_TYPE_BUNDLE = getBundle("org.panteleyev.pwdmanager.FieldType");

    String APP_TITLE = fxString(RB, "Application Title");

    String MASK = "*****";
}
