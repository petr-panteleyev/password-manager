/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.generator.Generator;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.model.Picture;
import java.util.List;
import java.util.Optional;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.fx.FxFactory.newTab;
import static org.panteleyev.fx.FxUtils.COLON;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.MenuFactory.menuItem;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.Options.options;
import static org.panteleyev.pwdmanager.Shortcuts.DELETE;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_D;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_G;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_N;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_U;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18M_FIELDS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ADD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DELETE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DOWN;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FIELD_NAME;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FIELD_TYPE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_GENERATE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ICON;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NEW_FIELD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NOTES;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PROPERTIES;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_UP;

final class EditCardDialog extends BaseDialog<Card> {
    private final ObservableList<EditableField> editableFields;

    private final TableView<EditableField> cardContentView = new TableView<>();
    private final TextField fieldNameEdit = new TextField();
    private final ComboBox<FieldType> fieldTypeCombo = new ComboBox<>();
    private final TextField cardNameEdit = new TextField();
    private final ComboBox<Picture> pictureList = new ComboBox<>();

    private final MenuItem generateMenuItem = menuItem(fxString(UI_BUNDLE, I18N_GENERATE), SHORTCUT_G,
        a -> onGeneratePassword());

    EditCardDialog(Card card) {
        super(options().getDialogCssFileUrl());
        setResizable(true);

        editableFields = FXCollections.observableArrayList(
            card.fields().stream()
                .map(EditableField::new)
                .toList()
        );

        setTitle(UI_BUNDLE.getString(I18N_CARD));

        cardContentView.getColumns().setAll(
            createEditorColumns()
        );

        var contextMenu = createContextMenu();
        contextMenu.setOnShowing(event ->
            generateMenuItem.setDisable(getSelectedField()
                .map(EditableField::getType)
                .flatMap(Options::getPasswordOptions)
                .isEmpty())
        );

        cardContentView.setContextMenu(contextMenu);
        cardContentView.setEditable(true);

        var grid1 = gridPane(
            List.of(
                gridRow(label(fxString(UI_BUNDLE, I18N_FIELD_NAME, COLON)), fieldNameEdit),
                gridRow(label(fxString(UI_BUNDLE, I18N_FIELD_TYPE, COLON)), fieldTypeCombo)
            ), b -> b.withStyle(STYLE_GRID_PANE)
        );

        var pane = new BorderPane(cardContentView, null, null, grid1, null);
        BorderPane.setAlignment(grid1, Pos.CENTER);
        BorderPane.setMargin(grid1, new Insets(5, 0, 0, 0));

        var optionsPane = gridPane(
            List.of(
                gridRow(label(fxString(UI_BUNDLE, I18N_TITLE, COLON)), cardNameEdit),
                gridRow(label(fxString(UI_BUNDLE, I18N_ICON, COLON)), pictureList)
            ), b -> b.withStyle(STYLE_GRID_PANE)
        );
        optionsPane.setPadding(new Insets(5, 5, 5, 5));
        GridPane.setHgrow(cardNameEdit, Priority.ALWAYS);

        var noteEditor = new TextArea();

        var tabPane = new TabPane(
            newTab(UI_BUNDLE, I18M_FIELDS, false, pane),
            newTab(UI_BUNDLE, I18N_NOTES, false, noteEditor),
            newTab(UI_BUNDLE, I18N_PROPERTIES, false, optionsPane));

        getDialogPane().setContent(tabPane);
        createDefaultButtons(UI_BUNDLE);

        cardContentView.setItems(editableFields);

        cardContentView.getSelectionModel()
            .selectedIndexProperty().addListener(x -> onFieldSelected());

        fieldNameEdit.setOnAction(x -> onFieldNameChanged());

        fieldTypeCombo.setItems(FXCollections.observableArrayList(FieldType.values()));
        fieldTypeCombo.setOnAction(x -> onFieldTypeComboChanged());

        noteEditor.setText(card.note());

        Picture.setupComboBox(pictureList);
        cardNameEdit.setText(card.name());
        pictureList.getSelectionModel().select(card.picture());

        setResultConverter(buttonType -> {
            if (OK.equals(buttonType)) {
                return new Card(
                    card.uuid(),
                    System.currentTimeMillis(),
                    pictureList.getSelectionModel().getSelectedItem(),
                    cardNameEdit.getText(),
                    editableFields.stream()
                        .map(EditableField::toField)
                        .toList(),
                    noteEditor.getText(),
                    card.favorite(),
                    card.active()
                );
            } else {
                return null;
            }
        });
    }

    private ContextMenu createContextMenu() {
        return new ContextMenu(
            menuItem(fxString(UI_BUNDLE, I18N_ADD), SHORTCUT_N, a -> onNewField()),
            new SeparatorMenuItem(),
            menuItem(fxString(UI_BUNDLE, I18N_DELETE), DELETE, a -> onDeleteField()),
            new SeparatorMenuItem(),
            generateMenuItem,
            new SeparatorMenuItem(),
            menuItem(fxString(UI_BUNDLE, I18N_UP), SHORTCUT_U, a -> onFieldUp()),
            menuItem(fxString(UI_BUNDLE, I18N_DOWN), SHORTCUT_D, a -> onFieldDown())
        );
    }

    private List<TableColumn<EditableField, String>> createEditorColumns() {
        var fieldNameColumn = new TableColumn<EditableField, String>();
        fieldNameColumn.setSortable(false);
        fieldNameColumn.setResizable(false);
        fieldNameColumn.setReorderable(false);
        fieldNameColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        fieldNameColumn.setCellValueFactory(p -> p.getValue().nameProperty());

        var fieldValueColumn = new TableColumn<EditableField, String>();
        fieldValueColumn.setSortable(false);
        fieldValueColumn.setResizable(false);
        fieldValueColumn.setReorderable(false);
        fieldValueColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        fieldValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        fieldValueColumn.setCellValueFactory(p -> p.getValue().valueProperty());

        var w = cardContentView.widthProperty().subtract(5);
        fieldNameColumn.prefWidthProperty().bind(w.divide(2));
        fieldValueColumn.prefWidthProperty().bind(w.subtract(fieldNameColumn.widthProperty()));

        return List.of(fieldNameColumn, fieldValueColumn);
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
        var f = new EditableField(FieldType.STRING, fxString(UI_BUNDLE, I18N_NEW_FIELD), "");
        editableFields.add(f);
        cardContentView.getSelectionModel().select(f);
    }

    private void onDeleteField() {
        getSelectedField().ifPresent(sel -> {
            var alert = new Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().filter(x -> x == ButtonType.YES).ifPresent(x -> editableFields.remove(sel));
        });
    }

    private void onGeneratePassword() {
        getSelectedField().ifPresent(sel -> Options.getPasswordOptions(sel.getType()).ifPresent(options -> {
            var password = new Generator().generate(options);
            sel.valueProperty().set(password);
        }));
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
