/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.RecordType;
import static java.util.Objects.requireNonNull;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.fx.FxUtils.COLON;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TYPE;

final class CardDialog extends RecordDialog {
    private final RecordType defaultType;

    CardDialog(RecordType defaultType) {
        this.defaultType = requireNonNull(defaultType);
        initialize();
    }

    private void initialize() {
        setTitle(fxString(UI_BUNDLE, I18N_CARD));
        createDefaultButtons(UI_BUNDLE);

        initLists();
        setTypeLabelText(fxString(UI_BUNDLE, I18N_TYPE, COLON));

        getNameEdit().setText("");
        getTypeList().getSelectionModel().select(defaultType);
        getPictureList().getSelectionModel().select(RecordType.PASSWORD.getPicture());

        setResultConverter(buttonType -> {
            if (OK.equals(buttonType)) {
                var type = getTypeList().getSelectionModel().getSelectedItem();
                return new Card(
                    getNameEdit().getText(),
                    getPictureList().getSelectionModel().getSelectedItem(),
                    type.getFieldSet()
                );
            } else {
                return null;
            }
        });

        Platform.runLater(this::setupValidator);
        Platform.runLater(getNameEdit()::requestFocus);
    }
}
