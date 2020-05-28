/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.pwdmanager.model.Card;
import java.util.Objects;
import static org.panteleyev.fx.LabelFactory.newLabel;
import static org.panteleyev.pwdmanager.PasswordManagerApplication.RB;

class EditNoteDialog extends BaseDialog<Card> {
    private final ValidationSupport validation = new ValidationSupport();

    private final TextField nameEdit = new TextField();
    private final TextArea noteEdit = new TextArea();

    private final Card note;

    EditNoteDialog(Card note) {
        super(MainWindowController.CSS_PATH);

        Objects.requireNonNull(note);

        this.note = note;

        initialize();
    }

    private void initialize() {
        setTitle(RB.getString("editNoteDialog.title"));

        var box = new HBox(5, newLabel(RB, "label.Name"), nameEdit);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 5, 0));
        nameEdit.setPrefColumnCount(25);

        var pane = new BorderPane(noteEdit, box, null, null, null);
        BorderPane.setAlignment(noteEdit, Pos.CENTER);
        BorderPane.setAlignment(box, Pos.CENTER_LEFT);

        getDialogPane().setContent(pane);
        createDefaultButtons(RB);

        nameEdit.setText(note.name());
        noteEdit.setText(note.note());

        setResultConverter((ButtonType b) -> (b == ButtonType.OK) ?
            Card.newNote(note.uuid(), nameEdit.getText(), noteEdit.getText(), note.favorite())
            : null);

        Platform.runLater(this::setupValidator);
        Platform.runLater(nameEdit::requestFocus);
    }

    private ContextMenu createContextMenu() {
        var menu = new ContextMenu();

        var m1 = new MenuItem("Copy...");
        m1.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
        m1.setOnAction(x -> {

        });

        menu.getItems().addAll(m1);

        /*
        MenuItem m1 = new MenuItem("Add field...");
        m1.setAccelerator(new KeyCodeCombination(KeyCode.INSERT));
        m1.setOnAction(x -> {
            Field f = new Field(FieldType.STRING, "New field", "");
            cardContentView.getItems().add(f);
            cardContentView.getSelectionModel().select(f);
        });

        MenuItem m2 = new MenuItem("Delete Field");
        m2.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        m2.setOnAction(x -> onDeleteField());

        menu.getItems().addAll(m1, new SeparatorMenuItem(), m2);

*/
        return menu;
    }

    private void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String value) ->
            ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
