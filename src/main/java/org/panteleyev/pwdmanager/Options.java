package org.panteleyev.pwdmanager;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import javafx.stage.Stage;
import java.util.prefs.Preferences;

final class Options {
    private static final double DEFAULT_WIDTH = 800;
    private static final double DEFAULT_HEIGHT = 542;

    private Options() {
    }

    private enum Option {
        WINDOW_HEIGHT("windowHeight"),
        WINDOW_WIDTH("windowWidth");

        private String s;

        Option(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static final Preferences PREFS = Preferences.userNodeForPackage(PasswordManagerApplication.class);

    static void saveWindowDimensions(Stage stage) {
        PREFS.putDouble(Option.WINDOW_WIDTH.toString(), stage.widthProperty().doubleValue());
        PREFS.putDouble(Option.WINDOW_HEIGHT.toString(), stage.heightProperty().doubleValue());
    }

    static void loadWindowDimensions(Stage stage) {
        stage.setWidth(PREFS.getDouble(Option.WINDOW_WIDTH.toString(), DEFAULT_WIDTH));
        stage.setHeight(PREFS.getDouble(Option.WINDOW_HEIGHT.toString(), DEFAULT_HEIGHT));
    }
}
