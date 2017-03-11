/*
 * Copyright (c) 2017, Petr Panteleyev <petr@panteleyev.org>
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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationResult;
import org.panteleyev.utilities.fx.BaseDialog;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditCategoryDialog extends BaseDialog<Category> implements Initializable {
    private static final String FXML_PATH = "/org/panteleyev/pwdmanager/EditCategoryDialog.fxml";

    private final Category category;

    @FXML private TextField            nameEdit;
    @FXML private ComboBox<RecordType> typeList;
    @FXML private ComboBox<Picture>    pictureList;

    EditCategoryDialog(Category category) {
        super(FXML_PATH, MainWindowController.UI_BUNDLE_PATH);
        Objects.requireNonNull(category);
        this.category = category;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTitle(resources.getString("categoryDialog.title"));
        createDefaultButtons();

        initLists();

        nameEdit.setText(category.getName());
        typeList.getSelectionModel().select(category.getType());
        pictureList.getSelectionModel().select(category.getPicture());

        setResultConverter((ButtonType b) -> {
            if (b == ButtonType.OK) {
                return new Category(
                        nameEdit.getText(),
                        typeList.getSelectionModel().getSelectedItem(),
                        pictureList.getSelectionModel().getSelectedItem()
                );
            } else {
                return null;
            }
        });

        Platform.runLater(this::setupValidator);
        Platform.runLater(nameEdit::requestFocus);
    }

    private void initLists() {
        typeList.setItems(FXCollections.observableArrayList(RecordType.values()));
        typeList.setCellFactory(p -> new CardTypeListCell());
        typeList.setButtonCell(new CardTypeListCell());
        Picture.setupComboBox(pictureList);
    }

    private void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String value) ->
                ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
