// Copyright © 2017-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.textfield.TextFields;
import org.panteleyev.commons.crypto.AES;
import org.panteleyev.freedesktop.Utility;
import org.panteleyev.freedesktop.entry.DesktopEntryBuilder;
import org.panteleyev.freedesktop.entry.DesktopEntryType;
import org.panteleyev.freedesktop.menu.Category;
import org.panteleyev.fx.Controller;
import org.panteleyev.fx.FxAction;
import org.panteleyev.fx.PredicateProperty;
import org.panteleyev.pwdmanager.cells.RecordListCell;
import org.panteleyev.pwdmanager.dialogs.AboutDialog;
import org.panteleyev.pwdmanager.dialogs.CardDialog;
import org.panteleyev.pwdmanager.dialogs.EditCardDialog;
import org.panteleyev.pwdmanager.dialogs.EditNoteDialog;
import org.panteleyev.pwdmanager.dialogs.NoteDialog;
import org.panteleyev.pwdmanager.dialogs.PasswordDialog;
import org.panteleyev.pwdmanager.filters.FieldContentFilter;
import org.panteleyev.pwdmanager.filters.RecordNameFilter;
import org.panteleyev.pwdmanager.imprt.ImportDialog;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Note;
import org.panteleyev.pwdmanager.model.RecordType;
import org.panteleyev.pwdmanager.model.WalletRecord;
import org.panteleyev.pwdmanager.settings.SettingsDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;
import static org.panteleyev.freedesktop.Utility.isLinux;
import static org.panteleyev.freedesktop.entry.DesktopEntryBuilder.localeString;
import static org.panteleyev.fx.FxAction.fxAction;
import static org.panteleyev.fx.factories.FileChooserFactory.fileChooser;
import static org.panteleyev.fx.factories.MenuFactory.checkMenuItem;
import static org.panteleyev.fx.factories.MenuFactory.menu;
import static org.panteleyev.fx.factories.MenuFactory.menuBar;
import static org.panteleyev.fx.factories.MenuFactory.menuItem;
import static org.panteleyev.fx.factories.StringFactory.ELLIPSIS;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.TextFieldFactory.searchField;
import static org.panteleyev.pwdmanager.Constants.APP_TITLE;
import static org.panteleyev.pwdmanager.Constants.EXTENSION_FILTER;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.Shortcuts.SHIFT_DELETE;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_ALT_S;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_C;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_E;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_F;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_I;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_N;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_O;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_T;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_V;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CHANGE_PASSWORD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CONFIRMATION;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_COPY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CREATE_DESKTOP_ENTRY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DELETE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DELETE_FINALLY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EDIT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EDIT_BUTTON;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ERROR;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EXIT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EXPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FAVORITE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FILE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FILTER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_HELP;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_HELP_ABOUT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_IMPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NEW_CARD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NEW_FILE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NEW_NOTE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NOTHING_TO_IMPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_OPEN;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_OPTIONS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASTE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PURGE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_RESTORE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SAVE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SHOW_DELETED;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SURE_TO_DELETE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SURE_TO_FINALLY_DELETE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SURE_TO_PURGE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TOOLS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_UNABLE_TO_READ_FILE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_VIEW;
import static org.panteleyev.pwdmanager.imprt.ImportUtil.calculateImport;
import static org.panteleyev.pwdmanager.model.Card.COMPARE_BY_ACTIVE;
import static org.panteleyev.pwdmanager.model.Card.COMPARE_BY_FAVORITE;
import static org.panteleyev.pwdmanager.model.Card.COMPARE_BY_NAME;

public final class MainWindowController extends Controller {
    private static final Logger LOGGER = Logger.getLogger(MainWindowController.class.getName());
    private final BooleanProperty showDeletedRecords = new SimpleBooleanProperty(false);
    private final PredicateProperty<WalletRecord> defaultFilter = new PredicateProperty<>(WalletRecord::active);
    private final SimpleObjectProperty<File> currentFile = new SimpleObjectProperty<>();
    private final ObservableList<WalletRecord> recordList = FXCollections.observableArrayList();

    private final SortedList<WalletRecord> sortedList = recordSortedList(recordList);
    private final FilteredList<WalletRecord> filteredList = recordFilteredList(sortedList, defaultFilter);
    private final ListView<WalletRecord> cardListView = new ListView<>(filteredList);

    private final BorderPane leftPane = new BorderPane(cardListView);
    private final TitledPane treeViewPane = new TitledPane("", leftPane);

    private final TextField searchTextField = searchField(TextFields::createClearableTextField, this::doSearch);

    private final BorderPane recordViewPane = new BorderPane();

    private final CardViewer cardContentView = new CardViewer();
    private String currentPassword;

    // Actions
    private final FxAction newCardAction = fxAction(string(UI_BUNDLE, I18N_NEW_CARD, ELLIPSIS))
            .onAction(_ -> onNewCard())
            .accelerator(SHORTCUT_N);
    private final FxAction newNoteAction = fxAction(string(UI_BUNDLE, I18N_NEW_NOTE, ELLIPSIS))
            .onAction(_ -> onNewNote())
            .accelerator(SHORTCUT_T);
    private final FxAction editCardAction = fxAction(string(UI_BUNDLE, I18N_EDIT_BUTTON, ELLIPSIS))
            .onAction(_ -> onEditCard())
            .accelerator(SHORTCUT_E)
            .disable(true);
    private final FxAction deleteCardAction = fxAction(string(UI_BUNDLE, I18N_DELETE))
            .onAction(_ -> onDeleteRecord())
            .accelerator(SHIFT_DELETE)
            .disable(true);
    private final FxAction restoreCardAction = fxAction(string(UI_BUNDLE, I18N_RESTORE))
            .onAction(_ -> onRestoreRecord())
            .visible(false);
    private final FxAction favoriteAction = fxAction(string(UI_BUNDLE, I18N_FAVORITE))
            .onAction(_ -> onFavorite())
            .accelerator(SHORTCUT_I)
            .disable(true)
            .selected(false);
    private final FxAction pasteAction = fxAction(string(UI_BUNDLE, I18N_PASTE))
            .onAction(_ -> onCardPaste())
            .accelerator(SHORTCUT_V)
            .disable(true);

    public MainWindowController(Stage stage, StartupParameters params) {
        super(stage, settings().getMainCssFilePath());

        setupActions();
        setupWindow(new BorderPane(createControls(), createMainMenu(), null, null, null));

        cardListView.getSelectionModel().selectedItemProperty().addListener(_ -> onListViewSelected());

        leftPane.setTop(searchTextField);
        BorderPane.setMargin(searchTextField, new Insets(0, 0, 10, 0));

        recordViewPane.setCenter(cardContentView);

        Platform.runLater(() -> {
            settings().loadStageDimensions(MainWindowController.this);
            cardListView.requestFocus();
        });

        if (params != null) {
            loadDocument(params.initialFile(), params.password(), params.saveFileName());
        }
    }

    @Override
    public String getTitle() {
        return APP_TITLE;
    }

    public static Alert newConfirmationAlert(String message) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(string(UI_BUNDLE, I18N_CONFIRMATION));
        alert.setHeaderText(string(UI_BUNDLE, I18N_CONFIRMATION));
        return alert;
    }

    private MenuBar createMainMenu() {
        // Edit
        var filterMenuItem = menuItem(string(UI_BUNDLE, I18N_FILTER), _ -> onFilter());
        filterMenuItem.setAccelerator(SHORTCUT_F);
        var copyMenuItem = menuItem(string(UI_BUNDLE, I18N_COPY), _ -> onCardCopy());
        copyMenuItem.setAccelerator(SHORTCUT_C);

        var editMenu = menu(
                string(UI_BUNDLE, I18N_EDIT),
                editCardAction.createMenuItem(),
                new SeparatorMenuItem(),
                newCardAction.createMenuItem(),
                newNoteAction.createMenuItem(),
                new SeparatorMenuItem(),
                filterMenuItem,
                new SeparatorMenuItem(),
                favoriteAction.createCheckMenuItem(),
                new SeparatorMenuItem(),
                copyMenuItem,
                pasteAction.createMenuItem(),
                new SeparatorMenuItem(),
                deleteCardAction.createMenuItem(),
                restoreCardAction.createMenuItem()
        );

        var showDeletedItemsMenuItem = checkMenuItem(string(UI_BUNDLE, I18N_SHOW_DELETED));
        showDeletedItemsMenuItem.setSelected(false);
        showDeletedRecords.bind(showDeletedItemsMenuItem.selectedProperty());
        showDeletedRecords.addListener((_, _, newValue) -> onShowDeletedItems(newValue));

        // File
        var newFileMenuItem = menuItem(string(UI_BUNDLE, I18N_NEW_FILE), _ -> onNewFile());
        var openFileMenuItem = menuItem(string(UI_BUNDLE, I18N_OPEN), _ -> onOpenFile());
        openFileMenuItem.setAccelerator(SHORTCUT_O);
        var exitMenuItem = menuItem(string(UI_BUNDLE, I18N_EXIT), _ -> onExit());
        // Tools
        var importMenuItem = menuItem(string(UI_BUNDLE, I18N_IMPORT, ELLIPSIS), _ -> onImportFile());
        importMenuItem.disableProperty().bind(currentFile.isNull());
        var exportMenuItem = menuItem(string(UI_BUNDLE, I18N_EXPORT, ELLIPSIS), _ -> onExportFile());
        exportMenuItem.disableProperty().bind(currentFile.isNull());
        var purgeMenuItem = menuItem(string(UI_BUNDLE, I18N_PURGE, ELLIPSIS), _ -> onPurge());
        purgeMenuItem.disableProperty().bind(currentFile.isNull());
        var changePasswordMenuItem = menuItem(string(UI_BUNDLE, I18N_CHANGE_PASSWORD, ELLIPSIS),
                _ -> onChangePassword());
        changePasswordMenuItem.disableProperty().bind(currentFile.isNull());
        var optionsMenuItem = menuItem(string(UI_BUNDLE, I18N_OPTIONS, ELLIPSIS), _ -> onOptions());
        optionsMenuItem.setAccelerator(SHORTCUT_ALT_S);
        var desktopEntryMenuItem = menuItem(string(UI_BUNDLE, I18N_CREATE_DESKTOP_ENTRY),
                _ -> onCreateDesktopEntry());
        // Help
        var aboutMenuItem = menuItem(string(UI_BUNDLE, I18N_HELP_ABOUT, ELLIPSIS), _ -> onAbout());

        var menuBar = menuBar(
                menu(string(UI_BUNDLE, I18N_FILE),
                        newFileMenuItem,
                        openFileMenuItem,
                        new SeparatorMenuItem(),
                        exitMenuItem),
                // Edit
                editMenu,
                menu(string(UI_BUNDLE, I18N_VIEW), showDeletedItemsMenuItem),
                // Tools
                menu(string(UI_BUNDLE, I18N_TOOLS),
                        importMenuItem,
                        exportMenuItem,
                        new SeparatorMenuItem(),
                        purgeMenuItem,
                        new SeparatorMenuItem(),
                        changePasswordMenuItem,
                        new SeparatorMenuItem(),
                        optionsMenuItem,
                        isLinux() ? new SeparatorMenuItem() : null,
                        isLinux() ? desktopEntryMenuItem : null
                ),
                // Help
                menu(string(UI_BUNDLE, I18N_HELP), aboutMenuItem)
        );
        menuBar.getMenus().forEach(menu -> menu.disableProperty().bind(getStage().focusedProperty().not()));

        return menuBar;
    }

    private void setupActions() {
        var binding = currentFile.isNull().or(searchTextField.textProperty().isEmpty().not());
        newCardAction.disableProperty().bind(binding);
        newNoteAction.disableProperty().bind(binding);
    }

    private Control createControls() {
        BorderPane.setAlignment(cardListView, Pos.CENTER);

        cardListView.setCellFactory(_ -> new RecordListCell());
        cardListView.setContextMenu(createContextMenu());

        treeViewPane.setMaxHeight(Double.MAX_VALUE);
        treeViewPane.setMaxWidth(Double.MAX_VALUE);

        var split = new SplitPane(treeViewPane, recordViewPane);
        split.setDividerPositions(0.30);

        return split;
    }

    private ContextMenu createContextMenu() {
        return new ContextMenu(
                editCardAction.createMenuItem(),
                new SeparatorMenuItem(),
                favoriteAction.createCheckMenuItem(),
                new SeparatorMenuItem(),
                newCardAction.createMenuItem(),
                newNoteAction.createMenuItem(),
                new SeparatorMenuItem(),
                deleteCardAction.createMenuItem(),
                restoreCardAction.createMenuItem(),
                new SeparatorMenuItem(),
                menuItem(string(UI_BUNDLE, I18N_COPY), _ -> onCardCopy()),
                pasteAction.createMenuItem()
        );
    }

    private void doSearch(String newValue) {
        if (newValue.isEmpty()) {
            filteredList.predicateProperty().bind(defaultFilter);
        } else {
            filteredList.predicateProperty().bind(
                    PredicateProperty.and(List.of(
                            defaultFilter,
                            new PredicateProperty<>(new RecordNameFilter(newValue).or(new FieldContentFilter(newValue)))
                    ))
            );
        }
    }

    private Optional<WalletRecord> getSelectedItem() {
        return Optional.ofNullable(cardListView.getSelectionModel().getSelectedItem());
    }

    private void onExit() {
        if (currentFile.get() != null) {
            writeDocument();
        }
        getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private void onImportFile() {
        var file = fileChooser(string(UI_BUNDLE, I18N_OPEN), List.of(EXTENSION_FILTER))
                .showOpenDialog(getStage());
        if (file == null || !file.exists()) {
            return;
        }

        new PasswordDialog(this, file, false).showAndWait().ifPresent(password -> {
            try (var fileInputStream = new FileInputStream(file);
                 var decypheredInputStream = decypheredInputStream(fileInputStream, password)
            ) {
                var list = Serializer.deserialize(decypheredInputStream);

                var importRecords = calculateImport(recordList, list);
                if (importRecords.isEmpty()) {
                    new Alert(Alert.AlertType.INFORMATION, string(UI_BUNDLE, I18N_NOTHING_TO_IMPORT), ButtonType.OK)
                            .showAndWait();
                } else {
                    new ImportDialog(this, importRecords).showAndWait().ifPresent(records -> {
                        for (var r : records) {
                            if (r.existingCard() != null) {
                                recordList.removeAll(r.existingCard());
                            }
                            recordList.add(r.cardToImport());
                        }
                        if (!records.isEmpty()) {
                            writeDocument();
                        }
                    });
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void onExportFile() {
        var file = fileChooser(string(UI_BUNDLE, I18N_SAVE), List.of(EXTENSION_FILTER))
                .showSaveDialog(getStage());
        if (file != null) {
            new PasswordDialog(this, file, false)
                    .showAndWait()
                    .ifPresent(password -> writeDocument(file, password));
        }
    }

    private void processNewRecord(WalletRecord newRecord) {
        recordList.add(newRecord);
        cardListView.getSelectionModel().select(newRecord);
        cardListView.scrollTo(newRecord);
        writeDocument();
    }

    private void onDeleteRecord() {
        getSelectedItem().ifPresent((WalletRecord item) -> {
            var messagePattern = string(
                    UI_BUNDLE, item.active() ?
                            I18N_SURE_TO_DELETE : I18N_SURE_TO_FINALLY_DELETE
            );

            newConfirmationAlert(String.format(messagePattern, item.name())).showAndWait()
                    .filter(x -> x.equals(ButtonType.YES))
                    .ifPresent(_ -> {
                        if (item.active()) {
                            var newCard = item.setActive(false);
                            updateListItem(newCard);
                            writeDocument();
                        } else {
                            recordList.remove(item);
                            writeDocument();
                        }
                    });
        });
    }

    private void onRestoreRecord() {
        getSelectedItem().ifPresent(card -> {
            var newCard = card.setActive(true);
            updateListItem(newCard);
            writeDocument();
        });
    }

    private void onNewCard() {
        new CardDialog(this, RecordType.PASSWORD)
                .showAndWait()
                .ifPresent(this::processNewRecord);
    }

    private void onNewNote() {
        new NoteDialog(this).showAndWait().ifPresent(this::processNewRecord);
    }

    private void onFilter() {
        searchTextField.requestFocus();
    }

    private void setupRecordViewer(WalletRecord record) {
        switch (record) {
            case Card card -> {
                var wrappers = card.fields().stream()
                        .filter(f -> !f.isEmpty())
                        .map(FieldWrapper::new)
                        .toList();
                cardContentView.setData(card, FXCollections.observableArrayList(wrappers));
            }
            case Note note -> cardContentView.setData(note);
            case null -> cardContentView.clearData();
        }
    }

    private void onListViewSelected() {
        var item = cardListView.getSelectionModel().getSelectedItem();

        if (item != null) {
            editCardAction.disable(!item.active());
            deleteCardAction.text(string(UI_BUNDLE, item.active() ? I18N_DELETE : I18N_DELETE_FINALLY))
                    .disable(false);
            restoreCardAction.visible(!item.active());
            favoriteAction.disable(false).selected(item.favorite());
        } else {
            editCardAction.disable(true);
            deleteCardAction.disable(true);
            restoreCardAction.visible(false);
            favoriteAction.disable(true).selected(false);
        }

        var pasteEnable = false;
        var cb = Clipboard.getSystemClipboard();
        if (item != null && cb.hasContent(Card.DATA_FORMAT)) {
            var sourceId = (UUID) cb.getContent(Card.DATA_FORMAT);
            var sourceItem = findRecordById(sourceId);
            pasteEnable = sourceItem.isPresent();
        }
        pasteAction.disable(!pasteEnable);

        setupRecordViewer(item);
    }

    private Optional<WalletRecord> findByUuid(UUID uuid) {
        return recordList.stream().filter(x -> x.uuid().equals(uuid)).findFirst();
    }

    private void processEditedRecord(WalletRecord r) {
        var index = getIndexByUUID(r.uuid());
        if (index != -1) {
            recordList.set(index, r);
        }

        cardListView.scrollTo(r);
        cardListView.getSelectionModel().select(r);

        writeDocument();
    }

    private void onEditCard() {
        getSelectedItem().ifPresent(item -> {
            var dialog = switch (item) {
                case Card card -> new EditCardDialog(this, card);
                case Note note -> new EditNoteDialog(this, note);
            };
            dialog.showAndWait().ifPresent(this::processEditedRecord);
        });
    }

    private void onAbout() {
        new AboutDialog(this).showAndWait();
    }

    private void putCardToClipboard(WalletRecord record) {
        var cb = Clipboard.getSystemClipboard();
        var content = new ClipboardContent();
        content.put(Card.DATA_FORMAT, record.uuid());
        cb.setContent(content);
    }

    private void onCardCopy() {
        getSelectedItem().ifPresent(this::putCardToClipboard);
    }

    private void onCardPaste() {
        var cb = Clipboard.getSystemClipboard();
        var sourceId = (UUID) cb.getContent(Card.DATA_FORMAT);

        findByUuid(sourceId).ifPresent(sourceRecord -> {
            var newRecord = sourceRecord.copyWithNewUuid();
            recordList.add(newRecord);
            cardListView.getSelectionModel().select(newRecord);
            cardListView.scrollTo(newRecord);
            writeDocument();
        });
    }

    private int getIndexByUUID(UUID uuid) {
        for (int index = 0; index < recordList.size(); index++) {
            if (recordList.get(index).uuid().equals(uuid)) {
                return index;
            }
        }
        return -1;
    }

    private void updateListItem(WalletRecord card) {
        var index = getIndexByUUID(card.uuid());
        if (index != -1) {
            recordList.set(index, card);
        }
    }

    private void onFavorite() {
        getSelectedItem().ifPresent(card -> {
            var newCard = card.setFavorite(!card.favorite());
            updateListItem(newCard);
            cardListView.getSelectionModel().select(newCard);
            cardListView.scrollTo(newCard);
            writeDocument();
        });
    }

    private Optional<WalletRecord> findRecordById(UUID uuid) {
        return sortedList.stream().filter(x -> x.uuid().equals(uuid)).findFirst();
    }

    private void setTitle() {
        if (currentFile.get() == null) {
            getStage().setTitle(APP_TITLE);
        } else {
            getStage().setTitle(APP_TITLE + " -- " + currentFile.get().getAbsolutePath());
        }
    }

    private void onNewFile() {
        var file = fileChooser(string(UI_BUNDLE, I18N_NEW_FILE), List.of(EXTENSION_FILTER))
                .showSaveDialog(getStage());
        if (file != null) {
            new PasswordDialog(this, file, true).showAndWait().ifPresent(password -> {
                currentPassword = password;
                recordList.clear();

                currentFile.set(file);
                settings().setCurrentFile(currentFile.get().getAbsolutePath());

                setTitle();
                writeDocument();
            });
        }
    }

    private void onOpenFile() {
        var file = fileChooser(string(UI_BUNDLE, I18N_OPEN), List.of(EXTENSION_FILTER))
                .showOpenDialog(getStage());
        if (file != null) {
            loadDocument(file, true);
        }
    }

    private void loadDocument(File file, String password, boolean changeSettings) {
        currentPassword = password;

        try (var fileInputStream = new FileInputStream(file);
             var decypheredInputStream = decypheredInputStream(fileInputStream, password)
        ) {
            var list = Serializer.deserialize(decypheredInputStream);
            recordList.setAll(list);

            currentFile.set(file);
            if (changeSettings) {
                settings().setCurrentFile(currentFile.get().getAbsolutePath());
            }

            setTitle();
        } catch (Exception ex) {
            var path = file.getAbsolutePath();
            var alert = new Alert(Alert.AlertType.ERROR, ex.toString());
            alert.setTitle(string(UI_BUNDLE, I18N_ERROR));
            alert.setHeaderText(string(UI_BUNDLE, I18N_UNABLE_TO_READ_FILE, ": ") + path);
            alert.showAndWait();
            LOGGER.log(Level.SEVERE, "Exception while reading file " + path, ex);
        }
    }

    private void loadDocument(File file, boolean changeSettings) {
        if (!file.exists()) {
            currentFile.set(null);
            if (changeSettings) {
                settings().setCurrentFile("");
            }
            setTitle();
        } else {
            new PasswordDialog(this, file, false).showAndWait()
                    .ifPresent(password -> loadDocument(file, password, changeSettings));
        }
    }

    private void writeDocument() {
        requireNonNull(currentFile.get());
        writeDocument(currentFile.get(), currentPassword);
    }

    private void writeDocument(File file, String password) {
        try (var bOut = new ByteArrayOutputStream()) {
            if (!password.isEmpty()) {
                try (var cOut = AES.aes256().getOutputStream(bOut, password)) {
                    Serializer.serialize(cOut, recordList);
                }
            } else {
                Serializer.serialize(bOut, recordList);
            }

            try (var fOut = new FileOutputStream(file)) {
                fOut.write(bOut.toByteArray());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void onChangePassword() {
        new PasswordDialog(this, currentFile.get(), true).showAndWait().ifPresent(password -> {
            currentPassword = password;
            writeDocument();
        });
    }

    private void onOptions() {
        new SettingsDialog(this).showAndWait();
    }

    @Override
    protected void onWindowHiding() {
        super.onWindowHiding();
        settings().saveWindowsSettings();
    }

    private void onShowDeletedItems(boolean show) {
        defaultFilter.set(show ? _ -> true : WalletRecord::active);
    }

    private void onPurge() {
        newConfirmationAlert(string(UI_BUNDLE, I18N_SURE_TO_PURGE)).showAndWait()
                .filter(x -> x.equals(ButtonType.YES))
                .ifPresent(_ -> {
                    recordList.removeIf(card -> !card.active());
                    writeDocument();
                });
    }

    private void onCreateDesktopEntry() {
        if (!isLinux()) {
            return;
        }
        Utility.getExecutablePath().ifPresent(command -> {
            var execFile = new File(command);
            var rootDir = execFile.getParentFile().getParentFile().getAbsolutePath();

            var desktopEntry = new DesktopEntryBuilder(DesktopEntryType.APPLICATION)
                    .version(DesktopEntryBuilder.VERSION_1_5)
                    .name("Password Manager")
                    .name(localeString("Менеджер паролей", "ru_RU"))
                    .categories(List.of(Category.UTILITY, Category.JAVA))
                    .comment("Application to store passwords and other sensitive information")
                    .comment(localeString("Хранение паролей и другой секретной информации", "ru_RU"))
                    .exec("\"" + command + "\"")
                    .icon(rootDir + "/lib/Password Manager.png")
                    .build();
            desktopEntry.write("password-manager");
        });
    }

    private InputStream decypheredInputStream(InputStream inputStream, String password) throws IOException {
        try (var outputStream = new ByteArrayOutputStream()) {
            if (!password.isEmpty()) {
                try (var cin = AES.aes256().getInputStream(inputStream, password)) {
                    cin.transferTo(outputStream);
                }
            } else {
                inputStream.transferTo(outputStream);
            }
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    //

    private static SortedList<WalletRecord> recordSortedList(ObservableList<WalletRecord> list) {
        var sortedList = new SortedList<>(list);
        sortedList.setComparator(COMPARE_BY_ACTIVE
                .thenComparing(COMPARE_BY_FAVORITE)
                .thenComparing(COMPARE_BY_NAME));
        return sortedList;
    }

    private static FilteredList<WalletRecord> recordFilteredList(ObservableList<WalletRecord> list,
            PredicateProperty<WalletRecord> predicate)
    {
        var filteredList = list.filtered(predicate);
        filteredList.predicateProperty().bind(predicate);
        return filteredList;
    }
}
