/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.pwdmanager.model.Note;
import java.util.List;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.fx.FxFactory.textField;
import static org.panteleyev.fx.FxUtils.COLON;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.Options.options;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NOTE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE;

final class NoteDialog extends BaseDialog<Note> {
    private final ValidationSupport validation = new ValidationSupport();

    private final TextField nameEdit = textField(20);

    NoteDialog() {
        super(options().getDialogCssFileUrl());

        setTitle(UI_BUNDLE.getString(I18N_NOTE));

        getDialogPane().setContent(gridPane(
            List.of(gridRow(label(fxString(UI_BUNDLE, I18N_TITLE, COLON)), nameEdit)),
            b -> b.withStyle(STYLE_GRID_PANE)
        ));
        createDefaultButtons(UI_BUNDLE);

        setResultConverter(buttonType -> OK.equals(buttonType) ? new Note(nameEdit.getText()) : null);

        Platform.runLater(this::setupValidator);
    }

    private void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String value) ->
            ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
