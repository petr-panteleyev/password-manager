/*
 * Copyright (c) 2016, 2017, Petr Panteleyev <petr@panteleyev.org>
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import org.panteleyev.utilities.fx.BaseDialog;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditCardDialog extends BaseDialog<Card> implements Initializable {
    private static final String FXML_PATH = "/org/panteleyev/pwdmanager/EditCardDialog.fxml";

    @FXML private TableView<Field>          cardContentView;
    @FXML private TableColumn<Field,String> fieldNameColumn;
    @FXML private TableColumn<Field,String> fieldValueColumn;
    @FXML private TextField                 fieldNameEdit;
    @FXML private ComboBox<FieldType>       fieldTypeCombo;
    @FXML private TextField                 cardNameEdit;
    @FXML private ComboBox<Picture>         pictureList;
    @FXML private TextArea                  noteEditor;

    private Card card;

    EditCardDialog(Card card) {
        super(FXML_PATH, MainWindowController.UI_BUNDLE_PATH);
        this.card = card;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTitle(resources.getString("editCardDialog.title"));
        createDefaultButtons();

        fieldValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        fieldNameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
        fieldValueColumn.setCellValueFactory(p -> p.getValue().valueProperty());

        fieldNameColumn.prefWidthProperty().bind(cardContentView.widthProperty().divide(2).subtract(1));
        fieldValueColumn.prefWidthProperty().bind(cardContentView.widthProperty().divide(2).subtract(1));

        cardContentView.setItems(FXCollections.observableArrayList(
                card.getFields().stream().map(Field::new).collect(Collectors.toList())
        ));

        cardContentView.getSelectionModel()
                .selectedIndexProperty().addListener(x -> onFieldSelected());

        fieldNameEdit.setOnAction(x -> onFieldNameChanged());

        fieldTypeCombo.setItems(FXCollections.observableArrayList(FieldType.values()));
        fieldTypeCombo.setOnAction(x -> onFieldTypeComboChanged());

        noteEditor.setText(card.getNote());

        Picture.setupComboBox(pictureList);
        cardNameEdit.setText(card.getName());
        pictureList.getSelectionModel().select(card.getPicture());

        setResultConverter((ButtonType b) -> {
            if (b == ButtonType.OK) {
                return new Card(card.getId(), cardNameEdit.getText(),
                        pictureList.getSelectionModel().getSelectedItem(),
                        new ArrayList<>(cardContentView.getItems()), noteEditor.getText());
            } else {
                return null;
            }
        });
    }

    private Optional<Field> getSelectedField() {
        return Optional.ofNullable(cardContentView.getSelectionModel().getSelectedItem());
    }

    private void onFieldSelected() {
        getSelectedField().ifPresent(x -> {
            fieldNameEdit.setText(x.getName());
            fieldTypeCombo.getSelectionModel().select(x.getType());
        });
    }

    public void onNewField() {
        Field f = new Field(FieldType.STRING, "New field", "");
        cardContentView.getItems().add(f);
        cardContentView.getSelectionModel().select(f);
    }

    public void onDeleteField() {
        getSelectedField().ifPresent(sel -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().filter(x -> x == ButtonType.YES).ifPresent(x -> cardContentView.getItems().remove(sel));
        });
    }

    private void onFieldNameChanged() {
        getSelectedField().ifPresent((Field sel) -> {
            String name = fieldNameEdit.getText();
            if (!name.equals(sel.getName())) {
                sel.nameProperty().set(name);
            }
        });
    }

    private void onFieldTypeComboChanged() {
        getSelectedField().ifPresent((Field sel) -> {
            FieldType type = fieldTypeCombo.getValue();
            if (type != sel.getType()) {
                sel.typeProperty().set(type);
            }
        });
    }
}
