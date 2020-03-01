package org.panteleyev.pwdmanager;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.File;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class PasswordManagerApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(PasswordManagerApplication.class.getName());
    private static final String FORMAT_PROP = "java.util.logging.SimpleFormatter.format";
    private static final String FORMAT = "%1$tF %1$tk:%1$tM:%1$tS %2$s%n%4$s: %5$s%6$s%n";
    private static final String OPTIONS_DIRECTORY = ".password-manager";

    private static final String UI_BUNDLE_PATH = "org.panteleyev.pwdmanager.ui";

    private static PasswordManagerApplication application;

    public static final ResourceBundle RB = ResourceBundle.getBundle(UI_BUNDLE_PATH);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        application = this;

        if (initLogDirectory()) {
            String formatProperty = System.getProperty(FORMAT_PROP);
            if (formatProperty == null) {
                System.setProperty(FORMAT_PROP, FORMAT);
            }
            LogManager.getLogManager()
                .readConfiguration(PasswordManagerApplication.class.getResourceAsStream("logger.properties"));
        }

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> uncaughtException(e));

        stage.setTitle(AboutDialog.APP_TITLE);

        new MainWindowController(stage);
        stage.show();
    }

    static PasswordManagerApplication getApplication() {
        return application;
    }

    private static boolean initLogDirectory() {
        File optionsDir = getSettingsDirectory();
        File logDir = new File(optionsDir, "logs");

        return logDir.exists() ? logDir.isDirectory() : logDir.mkdir();
    }

    private static File getSettingsDirectory() {
        var dir = new File(System.getProperty("user.home") + File.separator + OPTIONS_DIRECTORY);
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdir();
        } else {
            if (!dir.isDirectory()) {
                throw new RuntimeException("Options directory cannot be opened/created");
            }
        }

        return dir;
    }

    private static void uncaughtException(Throwable e) {
        LOGGER.log(Level.SEVERE, "Uncaught exception", e);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        });
    }
}
