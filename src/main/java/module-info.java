/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
open module password.manager {
    requires java.prefs;
    requires java.xml;
    requires java.logging;
    requires java.desktop;

    requires javafx.controls;
    requires javafx.graphics;

    requires org.panteleyev.fx;
    requires org.panteleyev.freedesktop;

    requires org.controlsfx.controls;
}
