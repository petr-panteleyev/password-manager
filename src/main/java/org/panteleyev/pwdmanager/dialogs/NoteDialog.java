// Copyright © 2017-2025 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.dialogs;

import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.pwdmanager.model.Note;

import java.util.List;

import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.functional.Scope.apply;
import static org.panteleyev.fx.factories.LabelFactory.label;
import static org.panteleyev.fx.factories.StringFactory.COLON;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.TextFieldFactory.textField;
import static org.panteleyev.fx.factories.grid.GridPaneFactory.gridPane;
import static org.panteleyev.fx.factories.grid.GridRow.gridRow;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NOTE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE;

public final class NoteDialog extends BaseDialog<Note> {
    private final ValidationSupport validation = new ValidationSupport();

    private final TextField nameEdit = textField(20);

    public NoteDialog(Controller owner) {
        super(owner, settings().getDialogCssFileUrl());

        setTitle(UI_BUNDLE.getString(I18N_NOTE));

        getDialogPane().setContent(apply(gridPane(
                        List.of(gridRow(label(string(UI_BUNDLE, I18N_TITLE, COLON)), nameEdit))),
                pane -> pane.getStyleClass().add(STYLE_GRID_PANE)));
        createDefaultButtons(UI_BUNDLE);

        setResultConverter(buttonType -> OK.equals(buttonType) ? new Note(nameEdit.getText()) : null);

        Platform.runLater(this::setupValidator);
    }

    private void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String _) ->
                ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
