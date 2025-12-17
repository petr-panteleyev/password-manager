// Copyright © 2017-2025 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.pwdmanager.model.Note;

import static java.util.Objects.requireNonNull;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.fx.factories.LabelFactory.label;
import static org.panteleyev.fx.factories.StringFactory.COLON;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.TextFieldFactory.textField;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NOTE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE;

public final class EditNoteDialog extends BaseDialog<Note> {
    private final ValidationSupport validation = new ValidationSupport();

    private final TextField nameEdit = textField(25);
    private final TextArea noteEdit = new TextArea();

    private final Note note;

    public EditNoteDialog(Controller owner, Note note) {
        super(owner, settings().getDialogCssFileUrl());
        this.note = requireNonNull(note);
        initialize();
    }

    private void initialize() {
        setTitle(UI_BUNDLE.getString(I18N_NOTE));

        var box = new HBox(5, label(string(UI_BUNDLE, I18N_TITLE, COLON)), nameEdit);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 5, 0));

        var pane = new BorderPane(noteEdit, box, null, null, null);
        BorderPane.setAlignment(noteEdit, Pos.CENTER);
        BorderPane.setAlignment(box, Pos.CENTER_LEFT);

        getDialogPane().setContent(pane);
        createDefaultButtons(UI_BUNDLE);

        nameEdit.setText(note.name());
        noteEdit.setText(note.note());

        setResultConverter(buttonType -> OK.equals(buttonType) ?
                new Note(note.uuid(), nameEdit.getText(), noteEdit.getText(), note.favorite())
                : null);

        Platform.runLater(this::setupValidator);
        Platform.runLater(nameEdit::requestFocus);
    }

    private void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String _) ->
                ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
