// Copyright © 2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.dialogs;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.pwdmanager.otp.TotpGenerator;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.Objects.requireNonNull;
import static javafx.event.ActionEvent.ACTION;
import static org.panteleyev.fx.factories.ButtonFactory.button;
import static org.panteleyev.fx.factories.ButtonFactory.buttonType;
import static org.panteleyev.fx.factories.LabelFactory.label;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.grid.GridPaneFactory.gridPane;
import static org.panteleyev.fx.factories.grid.GridRow.gridRow;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CLOSE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_COPY;
import static org.panteleyev.pwdmanager.settings.Settings.settings;

public class TotpDialog extends BaseDialog<Void> {
    private static final long DELAY = 1000L;
    private static final long INTERVAL = 30;

    private final Timer timer = new Timer(true);

    private final String secret;

    private final Label codeLabel = label("");
    private final ProgressIndicator progressBar = new ProgressIndicator(0.0);

    public TotpDialog(String title, String secret) {
        super(null, settings().getDialogCssFileUrl());
        this.secret = secret;

        setTitle(title);

        codeLabel.getStyleClass().add("totpCode");
        codeLabel.setEllipsisString("");

        var image = new Image(requireNonNull(getClass().getResourceAsStream("/images/copy.png")));
        var copyButton = button("", new ImageView(image));
        copyButton.setOnAction(this::copyCode);

        var grid = gridPane(List.of(
                gridRow(codeLabel, progressBar, copyButton)
        ), List.of(), List.of(STYLE_GRID_PANE));

        getDialogPane().setContent(grid);

        var copyButtonType = buttonType(string(UI_BUNDLE, I18N_COPY), ButtonBar.ButtonData.LEFT);
        getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        getButton(copyButtonType).ifPresent(b -> b.addEventFilter(ACTION, this::copyCode));
        getButton(ButtonType.CLOSE).ifPresent(b -> b.setText(string(UI_BUNDLE, I18N_CLOSE)));

        generate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                generate();
            }
        }, DELAY, DELAY);

        this.setOnHiding(_ -> timer.cancel());
    }

    private void generate() {
        var result = TotpGenerator.generate(secret);
        Platform.runLater(() -> {
            codeLabel.setText(" " + result.code() + " ");
            var progress = (result.seconds() % INTERVAL) / (double) INTERVAL;
            progressBar.setProgress(progress);
            getDialogPane().getScene().getWindow().sizeToScene();
        });
    }

    private void copyCode(ActionEvent event) {
        event.consume();

        var cb = Clipboard.getSystemClipboard();
        var content = new ClipboardContent();
        content.putString(codeLabel.getText().trim());
        cb.setContent(content);
    }
}
