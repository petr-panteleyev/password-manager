/*
 * Copyright (c) 2016, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class EditCardDialog extends Dialog<Card> {
    private TableView<Field>            cardContentView = new TableView<>();
    private TableColumn<Field,String>   fieldNameColumn = new TableColumn<>();
    private TableColumn<Field,String>   fieldValueColumn = new TableColumn<>();

    private TextField                   fieldNameEdit = new TextField();
    private ComboBox<FieldType>         fieldTypeCombo = new ComboBox<>();

    private final TextField             cardNameEdit = new TextField();
    private final TextArea              noteEditor = new TextArea();
    private final ComboBox<Picture>     pictureList = Picture.getComboBox();

    public EditCardDialog(Card card) {
        setTitle("Edit Card");
        initControls(card);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        BorderPane pane = new BorderPane();
        pane.setCenter(cardContentView);

        GridPane fieldTypePane = new GridPane();
        fieldTypePane.setHgap(5);
        fieldTypePane.setVgap(5);
        fieldTypePane.add(new Label("Field Name:"), 1, 1);
        fieldTypePane.add(fieldNameEdit, 2, 1);
        fieldTypePane.add(new Label("Field Type:"), 1, 2);
        fieldTypePane.add(fieldTypeCombo, 2, 2);
        pane.setBottom(fieldTypePane);

        GridPane cardPropsPane = new GridPane();
        cardPropsPane.setHgap(5);
        cardPropsPane.setVgap(5);
        cardPropsPane.add(new Label("Name:"), 1, 1);
        cardPropsPane.add(cardNameEdit, 2, 1);
        cardPropsPane.add(new Label("Icon:"), 1, 2);
        cardPropsPane.add(pictureList, 2, 2);

        Tab tab1 = new Tab("Fields", pane);
        tab1.setClosable(false);
        Tab tab2 = new Tab("Note", noteEditor);
        tab2.setClosable(false);
        Tab tab3 = new Tab("Properties", cardPropsPane);
        tab3.setClosable(false);

        getDialogPane().setContent(new TabPane(tab1, tab2, tab3));

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

    @SuppressWarnings("unchecked")
    private void initControls(Card card) {
        Objects.requireNonNull(card);

        cardContentView.getColumns().addAll(fieldNameColumn, fieldValueColumn);
        cardContentView.setContextMenu(createContextMenu());
        cardContentView.setEditable(true);
        cardContentView.getSelectionModel()
            .selectedIndexProperty().addListener(x -> onFieldSelected());

        fieldNameColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        fieldNameColumn.setSortable(false);
        fieldNameColumn.prefWidthProperty().bind(cardContentView.widthProperty().divide(2).subtract(1));
//        fieldNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        fieldValueColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        fieldValueColumn.setSortable(false);
        fieldValueColumn.prefWidthProperty().bind(cardContentView.widthProperty().divide(2).subtract(1));
        fieldValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        fieldNameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
        fieldValueColumn.setCellValueFactory(p -> p.getValue().valueProperty());

        cardContentView.setItems(FXCollections.observableArrayList(
            card.getFields().stream().map(Field::new).collect(Collectors.toList())
        ));

        fieldNameEdit.setOnAction(x -> onFieldNameChanged());

        fieldTypeCombo.setItems(FXCollections.observableArrayList(FieldType.values()));
        fieldTypeCombo.setOnAction(x -> onFieldTypeComboChanged());

        cardNameEdit.setText(card.getName());
        noteEditor.setText(card.getNote());
        pictureList.getSelectionModel().select(card.getPicture());
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

    private void onDeleteField() {
        getSelectedField().ifPresent(sel -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().filter(x -> x == ButtonType.YES).ifPresent(x -> cardContentView.getItems().remove(sel));
        });
    }

    private ContextMenu createContextMenu() {
        ContextMenu menu = new ContextMenu();

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
        return menu;
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
