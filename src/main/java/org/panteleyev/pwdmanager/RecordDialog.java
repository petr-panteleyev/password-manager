/*
 * Copyright (c) 2016, 2018, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.panteleyev.pwdmanager;

import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationResult;
import org.panteleyev.utilities.fx.BaseDialog;
import java.util.ResourceBundle;

abstract class RecordDialog<T extends Record> extends BaseDialog<NewRecordDescriptor<T>> implements Styles {
    final ResourceBundle               rb = PasswordManagerApplication.getBundle();

    private final TextField            nameEdit = new TextField();
    private final ComboBox<RecordType> typeList = new ComboBox<>();
    private final ComboBox<Picture>    pictureList = new ComboBox<>();
    private final Label                typeLabel = new Label(rb.getString("label.type"));
    private final CheckBox             parentRoot = new CheckBox(rb.getString("label.createFromRoot"));

    RecordDialog() {
        super(MainWindowController.CSS_PATH);

        var grid = new GridPane();
        grid.getStyleClass().add(GRID_PANE);

        grid.addRow(0, new Label(rb.getString("label.Name")), nameEdit);
        grid.addRow(1, typeLabel, typeList);
        grid.addRow(2, new Label(rb.getString("label.Icon")), pictureList);
        grid.addRow(3, parentRoot);

        nameEdit.setPrefColumnCount(25);
        GridPane.setColumnSpan(parentRoot, 2);

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

    CheckBox getParentRoot() {
        return parentRoot;
    }

    void setTypeLabelText(String text) {
        typeLabel.setText(text);
    }

    boolean isParentRoot() {
        return parentRoot.isSelected();
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
