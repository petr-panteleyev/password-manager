/*
 Copyright © 2021-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.bundles;

import java.util.ListResourceBundle;

import static org.panteleyev.pwdmanager.bundles.Internationalization.I18M_FIELDS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ACTION;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ADD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_BACKGROUND;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CANCEL_BUTTON;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD_NUMBER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD_PROVIDER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD_TYPE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CAR_BODY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CHANGE_PASSWORD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_COLORS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CONFIRMATION;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CONTROLS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_COPY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CREATE_DESKTOP_ENTRY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DELETE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DELETED;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DELETE_FINALLY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DIALOGS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DIGITS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DOWN;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EDIT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EDIT_BUTTON;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ENGINE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ERROR;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EXIT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EXPIRATION_DATE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EXPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FAVORITE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FIELD_NAME;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FIELD_VALUE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FILE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FILTER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FONTS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FOREGROUND;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_GENERATE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_HELP;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_HELP_ABOUT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ICON;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_IMPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ISSUED;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ISSUER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_LENGTH;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_LOGIN;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_LOWER_CASE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_MENU;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NAME_ON_CARD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NEW_CARD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NEW_FIELD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NEW_FILE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NEW_NOTE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NOTE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NOTES;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NOTHING_TO_IMPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NUMBER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_OPEN;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_OPTIONS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASSPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASSWORD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASSWORDS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASTE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PHONE_NUMBER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PLATE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PROPERTIES;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PURGE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_REGISTRATION;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_REPEAT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_RESTORE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SAVE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SHOW_DELETED;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SKIP;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SURE_TO_DELETE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SURE_TO_DELETE_FIELD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SURE_TO_FINALLY_DELETE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SURE_TO_PURGE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SYMBOLS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SYSTEM;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TEXT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE_1;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TOOLS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TYPE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_UNABLE_TO_READ_FILE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_UP;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_UPDATED;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_UPPER_CASE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_VALID_UNTIL;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_VIEW;

public class UiBundle extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {I18N_ACTION, "Action"},
                {I18N_ADD, "Add"},
                {I18N_BACKGROUND, "Background"},
                {I18N_CANCEL_BUTTON, "Cancel"},
                {I18N_CAR_BODY, "Body"},
                {I18N_CARD, "Card"},
                {I18N_CARD_NUMBER, "Card Number"},
                {I18N_CARD_PROVIDER, "Card Provider"},
                {I18N_CARD_TYPE, "Card Type"},
                {I18N_CHANGE_PASSWORD, "Change Password"},
                {I18N_COLORS, "Colors"},
                {I18N_CONFIRMATION, "Confirmation"},
                {I18N_CONTROLS, "Controls"},
                {I18N_COPY, "Copy"},
                {I18N_DELETE, "Delete"},
                {I18N_DELETE_FINALLY, "Finally delete"},
                {I18N_DELETED, "Deleted"},
                {I18N_DIALOGS, "Dialogs"},
                {I18N_DIGITS, "Digits"},
                {I18N_DOWN, "Down"},
                {I18N_EDIT, "Edit"},
                {I18N_EDIT_BUTTON, "Edit"},
                {I18N_ENGINE, "Engine"},
                {I18N_ERROR, "Error"},
                {I18N_EXIT, "Exit"},
                {I18N_EXPIRATION_DATE, "Expiration Date"},
                {I18N_EXPORT, "Export"},
                {I18N_FAVORITE, "Favorite"},
                {I18N_FIELD_NAME, "Field Name"},
                {I18N_FIELD_VALUE, "Field Value"},
                {I18M_FIELDS, "Fields"},
                {I18N_FILE, "File"},
                {I18N_FILTER, "Filter"},
                {I18N_FONTS, "Fonts"},
                {I18N_FOREGROUND, "Foreground"},
                {I18N_GENERATE, "Generate"},
                {I18N_HELP, "Help"},
                {I18N_HELP_ABOUT, "About"},
                {I18N_ICON, "Icon"},
                {I18N_IMPORT, "Import"},
                {I18N_ISSUED, "Issued"},
                {I18N_ISSUER, "Issuer"},
                {I18N_LENGTH, "Length"},
                {I18N_LOGIN, "Login"},
                {I18N_LOWER_CASE, "Lower Case"},
                {I18N_MENU, "Menu"},
                {I18N_NAME_ON_CARD, "Name on Card"},
                {I18N_NEW_CARD, "New Card"},
                {I18N_NEW_FIELD, "New Field"},
                {I18N_NEW_FILE, "New"},
                {I18N_NEW_NOTE, "New Note"},
                {I18N_NOTE, "Note"},
                {I18N_NOTES, "Notes"},
                {I18N_NOTHING_TO_IMPORT, "Nothing to import"},
                {I18N_NUMBER, "Number"},
                {I18N_OPEN, "Open"},
                {I18N_OPTIONS, "Options"},
                {I18N_PASSPORT, "Passport"},
                {I18N_PASSWORD, "Password"},
                {I18N_PASSWORDS, "Passwords"},
                {I18N_PASTE, "Paste"},
                {I18N_PHONE_NUMBER, "Phone Number"},
                {I18N_PLATE, "Plate"},
                {I18N_PROPERTIES, "Properties"},
                {I18N_PURGE, "Purge"},
                {I18N_REGISTRATION, "Registration"},
                {I18N_REPEAT, "Repeat"},
                {I18N_RESTORE, "Restore"},
                {I18N_SAVE, "Save"},
                {I18N_SHOW_DELETED, "Show Deleted"},
                {I18N_SKIP, "Skip"},
                {I18N_SYMBOLS, "Symbols"},
                {I18N_SYSTEM, "System"},
                {I18N_TEXT, "Text"},
                {I18N_TITLE, "Title"},
                {I18N_TITLE_1, "Title"},
                {I18N_TOOLS, "Tools"},
                {I18N_TYPE, "Type"},
                {I18N_UNABLE_TO_READ_FILE, "Unable to read file"},
                {I18N_UP, "Up"},
                {I18N_UPDATED, "Updated"},
                {I18N_UPPER_CASE, "Upper Case"},
                {I18N_VALID_UNTIL, "Valid Until"},
                {I18N_VIEW, "View"},
                {I18N_CREATE_DESKTOP_ENTRY, "Create Desktop Entry"},
                {I18N_SURE_TO_DELETE, "Are you sure to delete \"%s\"?"},
                {I18N_SURE_TO_FINALLY_DELETE, "Are you sure to finally delete \"%s\"?"},
                {I18N_SURE_TO_PURGE, "Are you sure to purge all deleted items?"},
                {I18N_SURE_TO_DELETE_FIELD, "Are you sure to delete field \"%s\"?"}
        };
    }
}
