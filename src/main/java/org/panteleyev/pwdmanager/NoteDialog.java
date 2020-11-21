/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.pwdmanager.model.Card;
import java.util.List;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.pwdmanager.Constants.RB;
import static org.panteleyev.pwdmanager.Constants.STYLE_GRID_PANE;

class NoteDialog extends BaseDialog<Card> {
    private final ValidationSupport validation = new ValidationSupport();

    private final TextField nameEdit = new TextField();

    NoteDialog() {
        super(MainWindowController.CSS_PATH);

        setTitle(RB.getString("noteDialog.title"));

        var grid = gridPane(
            List.of(gridRow(label(fxString(RB, "label.Name")), nameEdit)),
            b -> b.withStyle(STYLE_GRID_PANE)
        );

        getDialogPane().setContent(grid);
        createDefaultButtons(RB);

        nameEdit.setPrefColumnCount(20);

        setResultConverter(b -> b == ButtonType.OK ?
            Card.newNote(nameEdit.getText(), "", false) : null);

        Platform.runLater(this::setupValidator);
    }

    private void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String value) ->
            ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
