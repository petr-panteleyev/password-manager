/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

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
import org.panteleyev.pwdmanager.model.Note;
import java.util.Objects;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.fx.FxFactory.textField;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.pwdmanager.Constants.RB;
import static org.panteleyev.pwdmanager.Options.options;

final class EditNoteDialog extends BaseDialog<Note> {
    private final ValidationSupport validation = new ValidationSupport();

    private final TextField nameEdit = textField(25);
    private final TextArea noteEdit = new TextArea();

    private final Note note;

    EditNoteDialog(Note note) {
        super(options().getDialogCssFileUrl());

        Objects.requireNonNull(note);

        this.note = note;

        initialize();
    }

    private void initialize() {
        setTitle(RB.getString("editNoteDialog.title"));

        var box = new HBox(5, label(fxString(RB, "label.Name")), nameEdit);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 5, 0));

        var pane = new BorderPane(noteEdit, box, null, null, null);
        BorderPane.setAlignment(noteEdit, Pos.CENTER);
        BorderPane.setAlignment(box, Pos.CENTER_LEFT);

        getDialogPane().setContent(pane);
        createDefaultButtons(RB);

        nameEdit.setText(note.name());
        noteEdit.setText(note.note());

        setResultConverter(buttonType -> OK.equals(buttonType) ?
            new Note(note.uuid(), nameEdit.getText(), noteEdit.getText(), note.favorite())
            : null);

        Platform.runLater(this::setupValidator);
        Platform.runLater(nameEdit::requestFocus);
    }

    private void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String value) ->
            ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
