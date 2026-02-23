// Copyright © 2017-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.dialogs;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.pwdmanager.cells.CardTypeListCell;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Picture;
import org.panteleyev.pwdmanager.model.RecordType;

import java.util.List;

import static org.panteleyev.fx.factories.LabelFactory.label;
import static org.panteleyev.fx.factories.StringFactory.COLON;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.grid.GridPaneFactory.gridPane;
import static org.panteleyev.fx.factories.grid.GridRow.gridRow;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ICON;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TYPE;

abstract class RecordDialog extends BaseDialog<Card> {
    private final ValidationSupport validation = new ValidationSupport();

    private final TextField nameEdit = new TextField();
    private final ComboBox<RecordType> typeList = new ComboBox<>();
    private final ComboBox<Picture> pictureList = new ComboBox<>();
    private final Label typeLabel = label(string(UI_BUNDLE, I18N_TYPE, COLON));

    RecordDialog(Controller owner) {
        super(owner, settings().getDialogCssFileUrl());

        nameEdit.setPrefColumnCount(25);

        typeList.setOnAction(_ -> onCardTypeSelected());

        getDialogPane().setContent(gridPane(
                List.of(
                        gridRow(label(string(UI_BUNDLE, I18N_TITLE, COLON)), nameEdit),
                        gridRow(typeLabel, typeList),
                        gridRow(label(string(UI_BUNDLE, I18N_ICON, COLON)), pictureList)
                ), null, List.of(STYLE_GRID_PANE)
        ));
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
        typeList.setCellFactory(_ -> new CardTypeListCell());
        typeList.setButtonCell(new CardTypeListCell());
        Picture.setupComboBox(pictureList);
    }

    void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String _) ->
                ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }

    private void onCardTypeSelected() {
        pictureList.getSelectionModel().select(typeList.getSelectionModel().getSelectedItem().getPicture());
    }
}
