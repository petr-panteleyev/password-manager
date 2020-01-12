/*
 * Copyright (c) 2016, 2020, Petr Panteleyev <petr@panteleyev.org>
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
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.pwdmanager.model.Card;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.panteleyev.fx.FxFactory.newLabel;
import static org.panteleyev.fx.FxFactory.newMenuItem;
import static org.panteleyev.fx.FxFactory.newTab;
import static org.panteleyev.pwdmanager.PasswordManagerApplication.RB;

class EditCardDialog extends BaseDialog<Card> implements Styles {
    private final ObservableList<EditableField> editableFields;

    private final TableView<EditableField> cardContentView = new TableView<>();
    private final TextField fieldNameEdit = new TextField();
    private final ComboBox<FieldType> fieldTypeCombo = new ComboBox<>();
    private final TextField cardNameEdit = new TextField();
    private final ComboBox<Picture> pictureList = new ComboBox<>();

    EditCardDialog(Card card) {
        super(MainWindowController.CSS_PATH);

        editableFields = FXCollections.observableArrayList(
            card.getFields().stream().map(EditableField::new).collect(Collectors.toList()));

        setTitle(RB.getString("editCardDialog.title"));

        var fieldNameColumn = new TableColumn<EditableField, String>();
        fieldNameColumn.setSortable(false);
        fieldNameColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        var fieldValueColumn = new TableColumn<EditableField, String>();
        fieldValueColumn.setSortable(false);
        fieldValueColumn.setStyle("-fx-alignment: CENTER-LEFT;");

        cardContentView.getColumns().setAll(fieldNameColumn, fieldValueColumn);
        cardContentView.setContextMenu(createContextMenu());
        cardContentView.setEditable(true);

        var grid1 = new GridPane();
        grid1.getStyleClass().add(GRID_PANE);
        grid1.addRow(0, newLabel(RB, "label.FieldName"), fieldNameEdit);
        grid1.addRow(1, newLabel(RB, "label.FieldType"), fieldTypeCombo);

        var pane = new BorderPane(cardContentView, null, null, grid1, null);
        BorderPane.setAlignment(grid1, Pos.CENTER);
        BorderPane.setMargin(grid1, new Insets(5, 0, 0, 0));

        var grid3 = new GridPane();
        grid3.getStyleClass().add(GRID_PANE);
        grid3.setPadding(new Insets(5, 5, 5, 5));
        grid3.addRow(0, newLabel(RB, "label.Name"), cardNameEdit);
        grid3.addRow(1, newLabel(RB, "label.Icon"), pictureList);
        cardNameEdit.setPrefColumnCount(30);

        var noteEditor = new TextArea();

        var tabPane = new TabPane(
            newTab(RB, "editCardDialog.tab.fields", false, pane),
            newTab(RB, "editCardDialog.tab.notes", false, noteEditor),
            newTab(RB, "editCardDialog.tab.properties", false, grid3));

        getDialogPane().setContent(tabPane);
        createDefaultButtons(RB);

        fieldValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        fieldNameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
        fieldValueColumn.setCellValueFactory(p -> p.getValue().valueProperty());

        fieldNameColumn.prefWidthProperty().bind(cardContentView.widthProperty().divide(2).subtract(1));
        fieldValueColumn.prefWidthProperty().bind(cardContentView.widthProperty().divide(2).subtract(1));

        cardContentView.setItems(editableFields);

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
                return Card.newCard(
                    card.getUuid(),
                    System.currentTimeMillis(),
                    cardNameEdit.getText(),
                    pictureList.getSelectionModel().getSelectedItem(),
                    new ArrayList<>(editableFields.stream()
                        .map(EditableField::toField).collect(Collectors.toList())),
                    noteEditor.getText(),
                    card.isFavorite());
            } else {
                return null;
            }
        });
    }

    private ContextMenu createContextMenu() {
        return new ContextMenu(
            newMenuItem(RB, "editCardDialog.menu.addField",
                new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN), a -> onNewField()),
            new SeparatorMenuItem(),
            newMenuItem(RB, "editCardDialog.menu.deleteField",
                new KeyCodeCombination(KeyCode.DELETE), a -> onDeleteField()),
            new SeparatorMenuItem(),
            newMenuItem(RB, "menu.item.up",
                new KeyCodeCombination(KeyCode.U, KeyCombination.SHORTCUT_DOWN), a -> onFieldUp()),
            newMenuItem(RB, "menu.item.down",
                new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN), a -> onFieldDown())
        );
    }

    private Optional<EditableField> getSelectedField() {
        return Optional.ofNullable(cardContentView.getSelectionModel().getSelectedItem());
    }

    private void onFieldSelected() {
        getSelectedField().ifPresent(x -> {
            fieldNameEdit.setText(x.getName());
            fieldTypeCombo.getSelectionModel().select(x.getType());
        });
    }

    private void onNewField() {
        var f = new EditableField(FieldType.STRING, "New field", "");
        editableFields.add(f);
        cardContentView.getSelectionModel().select(f);
    }

    private void onDeleteField() {
        getSelectedField().ifPresent(sel -> {
            var alert = new Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().filter(x -> x == ButtonType.YES).ifPresent(x -> editableFields.remove(sel));
        });
    }

    private void onFieldNameChanged() {
        getSelectedField().ifPresent(sel -> {
            var name = fieldNameEdit.getText();
            if (!name.equals(sel.getName())) {
                sel.nameProperty().set(name);
            }
        });
    }

    private void onFieldTypeComboChanged() {
        getSelectedField().ifPresent(sel -> {
            var type = fieldTypeCombo.getValue();
            if (type != sel.getType()) {
                sel.typeProperty().set(type);
            }
        });
    }

    private void onFieldUp() {
        move(0, -1);
    }

    private void onFieldDown() {
        move(editableFields.size() - 1, 1);
    }

    private void move(int stopPosition, int add) {
        getSelectedField().ifPresent(sel -> {
            var index = editableFields.indexOf(sel);
            if (index == stopPosition) {
                return;
            }

            editableFields.remove(sel);
            editableFields.add(index + add, sel);
            cardContentView.getSelectionModel().select(sel);
        });
    }
}
