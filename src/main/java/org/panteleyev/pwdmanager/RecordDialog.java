package org.panteleyev.pwdmanager;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationResult;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.pwdmanager.model.Card;
import static org.panteleyev.fx.FxFactory.newLabel;
import static org.panteleyev.pwdmanager.PasswordManagerApplication.RB;

abstract class RecordDialog extends BaseDialog<Card> implements Styles {
    private final TextField nameEdit = new TextField();
    private final ComboBox<RecordType> typeList = new ComboBox<>();
    private final ComboBox<Picture> pictureList = new ComboBox<>();
    private final Label typeLabel = newLabel(RB, "label.type");

    RecordDialog() {
        super(MainWindowController.CSS_PATH);

        var grid = new GridPane();
        grid.getStyleClass().add(GRID_PANE);

        grid.addRow(0, newLabel(RB, "label.Name"), nameEdit);
        grid.addRow(1, typeLabel, typeList);
        grid.addRow(2, newLabel(RB, "label.Icon"), pictureList);

        nameEdit.setPrefColumnCount(25);

        getDialogPane().setContent(grid);
    }

    TextField getNameEdit() {
        return nameEdit;
    }

    ComboBox<RecordType> getTypeList() {
        return typeList;
    }

    ComboBox<Picture> getPictureList() {
        return pictureList;
    }

    void setTypeLabelText(String text) {
        typeLabel.setText(text);
    }

    void initLists() {
        typeList.setItems(FXCollections.observableArrayList(RecordType.values()));
        typeList.setCellFactory(p -> new CardTypeListCell());
        typeList.setButtonCell(new CardTypeListCell());
        Picture.setupComboBox(pictureList);
    }

    void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String value) ->
            ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
