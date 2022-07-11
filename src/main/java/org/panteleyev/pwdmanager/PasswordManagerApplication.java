/*
 Copyright © 2017-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.panteleyev.pwdmanager.model.Picture;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javafx.application.Platform.runLater;
import static org.panteleyev.pwdmanager.Constants.APP_TITLE;
import static org.panteleyev.pwdmanager.GlobalContext.files;
import static org.panteleyev.pwdmanager.GlobalContext.settings;

public final class PasswordManagerApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(PasswordManagerApplication.class.getName());

    private final static String LOG_PROPERTIES = """
            handlers                                = java.util.logging.FileHandler
                        
            java.util.logging.FileHandler.level     = ALL
            java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter
            java.util.logging.FileHandler.pattern   = %FILE_PATTERN%
            java.util.logging.FileHandler.append    = true
                        
            java.util.logging.SimpleFormatter.format = %1$tF %1$tk:%1$tM:%1$tS %2$s%n%4$s: %5$s%6$s%n
            """;


    private static PasswordManagerApplication application;

    public static void main(String[] args) {
        launch(args);
    }

    public PasswordManagerApplication() {
        application = this;
    }

    @Override
    public void start(Stage stage) throws Exception {
        files().initialize();
        settings().load();

        var logProperties = LOG_PROPERTIES.replace("%FILE_PATTERN%",
                files().getLogDirectory().resolve("PasswordManager.log").toString());
        try (var inputStream = new ByteArrayInputStream(logProperties.getBytes(UTF_8))) {
            LogManager.getLogManager().readConfiguration(inputStream);
        }

        setDefaultUncaughtExceptionHandler((t, e) -> uncaughtException(e));

        stage.setTitle(APP_TITLE);
        stage.getIcons().add(Picture.WALLET.getImage());

        new MainWindowController(stage);
        stage.show();
    }

    public static void showDocument(String uri) {
        if (application != null) {
            application.getHostServices().showDocument(uri);
        }
    }

    private static void uncaughtException(Throwable e) {
        LOGGER.log(Level.SEVERE, "Uncaught exception", e);
        runLater(() -> new Alert(Alert.AlertType.ERROR, e.toString()).showAndWait());
    }
}
