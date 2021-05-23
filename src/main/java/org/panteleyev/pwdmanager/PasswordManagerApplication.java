/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import static java.util.logging.LogManager.getLogManager;
import static javafx.application.Platform.runLater;
import static org.panteleyev.pwdmanager.Constants.APP_TITLE;
import static org.panteleyev.pwdmanager.Options.options;

public final class PasswordManagerApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(PasswordManagerApplication.class.getName());
    private static final String FORMAT_PROP = "java.util.logging.SimpleFormatter.format";
    private static final String FORMAT = "%1$tF %1$tk:%1$tM:%1$tS %2$s%n%4$s: %5$s%6$s%n";

    private static final String ICON_PATH = "org/panteleyev/pwdmanager/icon.png";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        options().initialize();
        options().loadFontOptions();
        options().loadColorOptions();
        options().generateCssFiles();

        var formatProperty = System.getProperty(FORMAT_PROP);
        if (formatProperty == null) {
            System.setProperty(FORMAT_PROP, FORMAT);
        }
        getLogManager()
            .readConfiguration(PasswordManagerApplication.class.getResourceAsStream("logger.properties"));

        setDefaultUncaughtExceptionHandler((t, e) -> uncaughtException(e));

        stage.setTitle(APP_TITLE);
        stage.getIcons().add(new Image(ICON_PATH));

        new MainWindowController(stage);
        stage.show();
    }

    private static void uncaughtException(Throwable e) {
        LOGGER.log(Level.SEVERE, "Uncaught exception", e);
        runLater(() -> new Alert(Alert.AlertType.ERROR, e.toString()).showAndWait());
    }
}
