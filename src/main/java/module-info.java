/*
 Copyright © 2017-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
open module password.manager {
    requires java.xml;
    requires java.logging;
    requires java.desktop;

    requires javafx.controls;
    requires javafx.graphics;

    requires org.panteleyev.fx;
    requires org.panteleyev.freedesktop;

    requires org.controlsfx.controls;
}
