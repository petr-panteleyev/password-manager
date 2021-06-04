/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.bundles.UiBundle;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;

public interface Constants {
    ResourceBundle BUILD_INFO_BUNDLE = getBundle("buildInfo");
    ResourceBundle UI_BUNDLE = getBundle(UiBundle.class.getCanonicalName());

    String APP_TITLE = "Password Manager";
    String MASK = "*****";
}
