/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.model;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import org.panteleyev.pwdmanager.cells.PictureListCell;
import java.util.Arrays;
import java.util.Comparator;

@SuppressWarnings("unused")
public enum Picture {
    AIRPLANE,
    AMAZON,
    AMEX,
    APPLE,
    AUTO,
    BANK,
    BITBUCKET,
    CD,
    CHESS,
    CHROME,
    COMPUTER,
    CREDIT_CARD,
    EDUCATION,
    EMAIL,
    FACEBOOK,
    FEMALE,
    FLICKR,
    FOLDER,
    GENERIC,
    GLASSES,
    GITHUB,
    GPLUS,
    HOUSE,
    ICQ,
    IMPORTANT,
    INFO,
    INSURANCE,
    INTERNET,
    IPHONE,
    JAVA,
    LINKEDIN,
    MALE,
    MASTERCARD,
    MEDICINE,
    MOBILE,
    MOZILLA,
    NOTE,
    NVIDIA,
    ODNOKLASSNIKI,
    ORIGIN,
    PASSPORT,
    PHONE,
    REDDIT,
    RUSSIA,
    SBERBANK,
    SHOP,
    SKYPE,
    STEAM,
    TELEGRAM,
    TRAIN,
    TWITTER,
    US,
    VISA,
    VK,
    WALLET,
    WIFI,
    WOW,
    YAHOO,
    YANDEX;

    private final Image image;
    private final Image bigImage;

    Picture() {
        var res = name().toLowerCase() + ".png";
        var bigRes = name().toLowerCase() + "-48.png";

        image = new Image(getClass().getResourceAsStream("/org/panteleyev/pwdmanager/res/" + res));
        bigImage = new Image(getClass().getResourceAsStream("/org/panteleyev/pwdmanager/res/" + bigRes));
    }

    public Image getImage() {
        return image;
    }

    public Image getBigImage() {
        return bigImage;
    }

    public static void setupComboBox(ComboBox<Picture> comboBox) {
        comboBox.setCellFactory(p -> new PictureListCell());
        comboBox.setButtonCell(new PictureListCell());
        comboBox.setItems(FXCollections.observableArrayList(
            Arrays.stream(Picture.values()).sorted(Comparator.comparing(Enum::name)).toList())
        );
    }

    public static Picture of(String stringValue) {
        if (stringValue == null || stringValue.isBlank()) {
            return GENERIC;
        }

        try {
            return valueOf(stringValue.toUpperCase());
        } catch (Exception ex) {
            return GENERIC;
        }
    }
}
