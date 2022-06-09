/*
 Copyright © 2020-2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
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
