/*
 Copyright © 2020-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.bundles.UiBundle;

import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;

public final class Constants {
    public static final ResourceBundle BUILD_INFO_BUNDLE = getBundle("buildInfo");
    public static final ResourceBundle UI_BUNDLE = getBundle(UiBundle.class.getCanonicalName());

    public static final String APP_TITLE = "Password Manager";
    public static final String MASK = "*****";

    private Constants() {
    }
}
