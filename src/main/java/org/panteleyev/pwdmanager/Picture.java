package org.panteleyev.pwdmanager;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;

@SuppressWarnings("unused")
public enum Picture {
    GENERIC,
    PASSWORD,
    INFO,
    IMPORTANT,
    AUTO,
    AIRPLANE,
    TRAIN,
    AMEX,
    MASTERCARD,
    INTERNET,
    INSURANCE,
    GLASSES,
    BANK,
    RAIF,
    EMAIL,
    VISA,
    CREDIT_CARD,
    IPHONE,
    MOBILE,
    WIFI,
    CD,
    COMPUTER,
    FACEBOOK,
    VK,
    SKYPE,
    TWITTER,
    GPLUS,
    MOZILLA,
    PASSPORT,
    SHOP,
    NOTE,
    MEDICINE,
    HOUSE,
    FEMALE,
    MALE,
    EDUCATION,
    STEAM,
    GITHUB,
    BITBUCKET,
    TELEGRAM,
    APPLE,
    YAHOO,
    FOLDER;

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
        comboBox.setItems(FXCollections.observableArrayList(Picture.values()));
    }
}

