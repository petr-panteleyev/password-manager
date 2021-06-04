/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.pwdmanager.cells.CardTypeListCell;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Picture;
import org.panteleyev.pwdmanager.model.RecordType;
import java.util.List;
import static org.panteleyev.fx.FxUtils.COLON;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.Options.options;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ICON;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TYPE;

abstract class RecordDialog extends BaseDialog<Card> {
    private final ValidationSupport validation = new ValidationSupport();

    private final TextField nameEdit = new TextField();
    private final ComboBox<RecordType> typeList = new ComboBox<>();
    private final ComboBox<Picture> pictureList = new ComboBox<>();
    private final Label typeLabel = label(fxString(UI_BUNDLE, I18N_TYPE, COLON));

    RecordDialog() {
        super(options().getDialogCssFileUrl());

        nameEdit.setPrefColumnCount(25);

        typeList.setOnAction(event -> onCardTypeSelected());

        getDialogPane().setContent(gridPane(
            List.of(
                gridRow(label(fxString(UI_BUNDLE, I18N_TITLE, COLON)), nameEdit),
                gridRow(typeLabel, typeList),
                gridRow(label(fxString(UI_BUNDLE, I18N_ICON, COLON)), pictureList)
            ), b -> b.withStyle(STYLE_GRID_PANE)
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
        typeList.setCellFactory(p -> new CardTypeListCell());
        typeList.setButtonCell(new CardTypeListCell());
        Picture.setupComboBox(pictureList);
    }

    void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String value) ->
            ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }

    private void onCardTypeSelected() {
        pictureList.getSelectionModel().select(typeList.getSelectionModel().getSelectedItem().getPicture());
    }
}
