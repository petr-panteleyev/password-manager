/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
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
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CONTROLS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_COPY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DELETE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DELETED;
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

@SuppressWarnings("unused")
public class UiBundle_ru_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
            {I18N_ACTION, "Действие"},
            {I18N_ADD, "Добавить"},
            {I18N_BACKGROUND, "Фон"},
            {I18N_CANCEL_BUTTON, "Отмена"},
            {I18N_CAR_BODY, "Кузов"},
            {I18N_CARD, "Карточка"},
            {I18N_CARD_NUMBER, "Номер карты"},
            {I18N_CARD_PROVIDER, "Кто выпустил"},
            {I18N_CARD_TYPE, "Тип карты"},
            {I18N_CHANGE_PASSWORD, "Изменить пароль"},
            {I18N_COLORS, "Цвета"},
            {I18N_CONTROLS, "Элементы управления"},
            {I18N_COPY, "Копировать"},
            {I18N_DELETE, "Удалить"},
            {I18N_DELETED, "Удаленное"},
            {I18N_DIALOGS, "Диалоги"},
            {I18N_DIGITS, "Цифры"},
            {I18N_DOWN, "Вниз"},
            {I18N_EDIT, "Правка"},
            {I18N_EDIT_BUTTON, "Изменить"},
            {I18N_ENGINE, "Двигатель"},
            {I18N_ERROR, "Ошибка"},
            {I18N_EXIT, "Выйти"},
            {I18N_EXPIRATION_DATE, "Срок окончания"},
            {I18N_EXPORT, "Экспорт"},
            {I18N_FAVORITE, "Избранное"},
            {I18N_FIELD_NAME, "Название поля"},
            {I18N_FIELD_VALUE, "Значение поля"},
            {I18M_FIELDS, "Поля"},
            {I18N_FILE, "Файл"},
            {I18N_FILTER, "Фильтр"},
            {I18N_FONTS, "Шрифты"},
            {I18N_FOREGROUND, "Цвет"},
            {I18N_GENERATE, "Сгенерировать"},
            {I18N_HELP, "Справка"},
            {I18N_HELP_ABOUT, "О программе"},
            {I18N_ICON, "Значок"},
            {I18N_IMPORT, "Импорт"},
            {I18N_ISSUED, "Когда выдан"},
            {I18N_ISSUER, "Кем выдан"},
            {I18N_LENGTH, "Длина"},
            {I18N_LOGIN, "Логин"},
            {I18N_LOWER_CASE, "Маленькие буквы"},
            {I18N_MENU, "Меню"},
            {I18N_NAME_ON_CARD, "Имя на карте"},
            {I18N_NEW_CARD, "Создать карточку"},
            {I18N_NEW_FIELD, "Новое поле"},
            {I18N_NEW_FILE, "Создать"},
            {I18N_NEW_NOTE, "Создать заметку"},
            {I18N_NOTE, "Заметка"},
            {I18N_NOTES, "Заметки"},
            {I18N_NOTHING_TO_IMPORT, "Нет записей для импорта"},
            {I18N_NUMBER, "Номер"},
            {I18N_OPEN, "Открыть"},
            {I18N_OPTIONS, "Настройки"},
            {I18N_PASSPORT, "Паспорт"},
            {I18N_PASSWORD, "Пароль"},
            {I18N_PASSWORDS, "Пароли"},
            {I18N_PASTE, "Вставить"},
            {I18N_PHONE_NUMBER, "Номер телефона"},
            {I18N_PLATE, "Номер"},
            {I18N_PROPERTIES, "Свойства"},
            {I18N_PURGE, "Очистить"},
            {I18N_REGISTRATION, "Регистрация"},
            {I18N_REPEAT, "Повторить"},
            {I18N_RESTORE, "Восстановить"},
            {I18N_SAVE, "Сохранить"},
            {I18N_SHOW_DELETED, "Показывать удаленные"},
            {I18N_SKIP, "Пропустить"},
            {I18N_SYMBOLS, "Символы"},
            {I18N_SYSTEM, "Система"},
            {I18N_TEXT, "Текст"},
            {I18N_TITLE, "Заголовок"},
            {I18N_TITLE_1, "Название"},
            {I18N_TOOLS, "Инструменты"},
            {I18N_TYPE, "Тип"},
            {I18N_UNABLE_TO_READ_FILE, "Невозможно прочитать файл"},
            {I18N_UP, "Вверх"},
            {I18N_UPDATED, "Обновлено"},
            {I18N_UPPER_CASE, "Большие буквы"},
            {I18N_VALID_UNTIL, "Годен до"},
            {I18N_VIEW, "Вид"},
        };
    }
}
