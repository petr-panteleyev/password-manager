/*
 Copyright © 2017-2025 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;
import static org.panteleyev.freedesktop.Utility.isLinux;
import static org.panteleyev.freedesktop.entry.DesktopEntryBuilder.localeString;
import static org.panteleyev.fx.FxFactory.searchField;
import static org.panteleyev.fx.FxUtils.ELLIPSIS;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.MenuFactory.checkMenuItem;
import static org.panteleyev.fx.MenuFactory.menu;
import static org.panteleyev.fx.MenuFactory.menuBar;
import static org.panteleyev.fx.MenuFactory.menuItem;
import static org.panteleyev.fx.dialogs.FileChooserBuilder.fileChooser;
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
    private static final long OPEN_DIALOG_DELAY = 300;
    private final BooleanProperty showDeletedRecords = new SimpleBooleanProperty(false);
    private final PredicateProperty<WalletRecord> defaultFilter = new PredicateProperty<>(WalletRecord::active);
    private final SimpleObjectProperty<File> currentFile = new SimpleObjectProperty<>();
    private final ObservableList<WalletRecord> recordList = FXCollections.observableArrayList();
    private final SortedList<WalletRecord> sortedList = new SortedList<>(recordList);
    private final FilteredList<WalletRecord> filteredList = sortedList.filtered(defaultFilter);
    private final ListView<WalletRecord> cardListView = new ListView<>(filteredList);

    private final BorderPane leftPane = new BorderPane(cardListView);
    private final TitledPane treeViewPane = new TitledPane("", leftPane);

    private final TextField searchTextField = searchField(TextFields::createClearableTextField, this::doSearch);

    private final BorderPane recordViewPane = new BorderPane();

    private final CardViewer cardContentView = new CardViewer();
    private String currentPassword;

    // Actions
    private final FxAction editCardAction = FxAction.create(fxString(UI_BUNDLE, I18N_EDIT_BUTTON, ELLIPSIS),
            _ -> onEditCard(), SHORTCUT_E, true);
    private final FxAction deleteCardAction = FxAction.create(fxString(UI_BUNDLE, I18N_DELETE),
            _ -> onDeleteRecord(), SHIFT_DELETE, true);
    private final FxAction restoreCardAction = FxAction.create(fxString(UI_BUNDLE, I18N_RESTORE),
            _ -> onRestoreRecord());
    private final FxAction favoriteAction = FxAction.create(fxString(UI_BUNDLE, I18N_FAVORITE),
            _ -> onFavorite(), SHORTCUT_I, true);
    private final FxAction pasteAction = FxAction.create(fxString(UI_BUNDLE, I18N_PASTE),
            _ -> onCardPaste(), SHORTCUT_V, true);

    public MainWindowController(Stage stage) {
        super(stage, settings().getMainCssFilePath());

        restoreCardAction.setVisible(false);
        favoriteAction.setSelected(false);

        sortedList.setComparator(COMPARE_BY_ACTIVE
                .thenComparing(COMPARE_BY_FAVORITE)
                .thenComparing(COMPARE_BY_NAME));

        filteredList.predicateProperty().bind(defaultFilter);

        setupWindow(new BorderPane(createControls(), createMainMenu(), null, null, null));

        cardListView.getSelectionModel().selectedItemProperty().addListener(_ -> onListViewSelected());

        leftPane.setTop(searchTextField);
        BorderPane.setMargin(searchTextField, new Insets(0, 0, 10, 0));

        recordViewPane.setCenter(cardContentView);

        Platform.runLater(() -> {
            settings().loadStageDimensions(MainWindowController.this);
            cardListView.requestFocus();
        });

        var timer = new Timer(true);
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> openInitialFile());
                    }
                }, OPEN_DIALOG_DELAY
        );
    }

    public static Alert newConfirmationAlert(String message) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(fxString(UI_BUNDLE, I18N_CONFIRMATION));
        alert.setHeaderText(fxString(UI_BUNDLE, I18N_CONFIRMATION));
        return alert;
    }

    private MenuBar createMainMenu() {
        var editMenu = menu(
                fxString(UI_BUNDLE, I18N_EDIT),
                editCardAction.createMenuItem(),
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_NEW_CARD, ELLIPSIS), SHORTCUT_N, _ -> onNewCard(),
                        currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                menuItem(fxString(UI_BUNDLE, I18N_NEW_NOTE, ELLIPSIS), SHORTCUT_T, _ -> onNewNote(),
                        currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_FILTER), SHORTCUT_F, _ -> onFilter()),
                new SeparatorMenuItem(),
                favoriteAction.createCheckMenuItem(),
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_COPY), SHORTCUT_C, _ -> onCardCopy(),
                        selectedItemProperty().isNull()),
                pasteAction.createMenuItem(),
                new SeparatorMenuItem(),
                deleteCardAction.createMenuItem(),
                restoreCardAction.createMenuItem()
        );

        var showDeletedItemsMenuItem = checkMenuItem(fxString(UI_BUNDLE, I18N_SHOW_DELETED), false);
        showDeletedRecords.bind(showDeletedItemsMenuItem.selectedProperty());
        showDeletedRecords.addListener((_, _, newValue) -> onShowDeletedItems(newValue));

        var menuBar = menuBar(
                menu(fxString(UI_BUNDLE, I18N_FILE),
                        menuItem(fxString(UI_BUNDLE, I18N_NEW_FILE), _ -> onNewFile()),
                        menuItem(fxString(UI_BUNDLE, I18N_OPEN), SHORTCUT_O, _ -> onOpenFile()),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI_BUNDLE, I18N_EXIT), _ -> onExit())),
                // Edit
                editMenu,
                menu(fxString(UI_BUNDLE, I18N_VIEW),
                        showDeletedItemsMenuItem),
                // Tools
                menu(fxString(UI_BUNDLE, I18N_TOOLS),
                        menuItem(fxString(UI_BUNDLE, I18N_IMPORT, ELLIPSIS), _ -> onImportFile(), currentFile.isNull()),
                        menuItem(fxString(UI_BUNDLE, I18N_EXPORT, ELLIPSIS), _ -> onExportFile(), currentFile.isNull()),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI_BUNDLE, I18N_PURGE, ELLIPSIS), _ -> onPurge(), currentFile.isNull()),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI_BUNDLE, I18N_CHANGE_PASSWORD, ELLIPSIS), _ -> onChangePassword(),
                                currentFile.isNull()),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI_BUNDLE, I18N_OPTIONS, ELLIPSIS), SHORTCUT_ALT_S, _ -> onOptions()),
                        isLinux() ? new SeparatorMenuItem() : null,
                        isLinux() ? menuItem(fxString(UI_BUNDLE, I18N_CREATE_DESKTOP_ENTRY),
                                _ -> onCreateDesktopEntry()) : null
                ),
                // Help
                menu(fxString(UI_BUNDLE, I18N_HELP),
                        menuItem(fxString(UI_BUNDLE, I18N_HELP_ABOUT, ELLIPSIS), _ -> onAbout()))
        );
        menuBar.getMenus().forEach(menu -> menu.disableProperty().bind(getStage().focusedProperty().not()));

        return menuBar;
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
                menuItem(fxString(UI_BUNDLE, I18N_NEW_CARD, ELLIPSIS), _ -> onNewCard(),
                        currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                menuItem(fxString(UI_BUNDLE, I18N_NEW_NOTE, ELLIPSIS), _ -> onNewNote(),
                        currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                new SeparatorMenuItem(),
                deleteCardAction.createMenuItem(),
                restoreCardAction.createMenuItem(),
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_COPY), _ -> onCardCopy()),
                pasteAction.createMenuItem()
        );
    }

    private void openInitialFile() {
        var fileName = System.getProperty("password.file");
        if (fileName != null && !fileName.isEmpty()) {
            loadDocument(new File(fileName), false);
        } else {
            var currentFilePath = settings().getCurrentFile();
            if (!currentFilePath.isEmpty()) {
                loadDocument(new File(currentFilePath), true);
            }
        }
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

    private ReadOnlyObjectProperty<WalletRecord> selectedItemProperty() {
        return cardListView.getSelectionModel().selectedItemProperty();
    }

    private void onExit() {
        if (currentFile.get() != null) {
            writeDocument();
        }
        getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private void onImportFile() {
        var file = fileChooser(fxString(UI_BUNDLE, I18N_OPEN), List.of(EXTENSION_FILTER))
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
                    new Alert(Alert.AlertType.INFORMATION, fxString(UI_BUNDLE, I18N_NOTHING_TO_IMPORT), ButtonType.OK)
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
        var file = fileChooser(fxString(UI_BUNDLE, I18N_SAVE), List.of(EXTENSION_FILTER))
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
            var messagePattern = fxString(
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
            editCardAction.setDisable(!item.active());
            deleteCardAction.setText(fxString(UI_BUNDLE, item.active() ? I18N_DELETE : I18N_DELETE_FINALLY))
                    .setDisable(false);
            restoreCardAction.setVisible(!item.active());
            favoriteAction.setDisable(false)
                    .setSelected(item.favorite());
        } else {
            editCardAction.setDisable(true);
            deleteCardAction.setDisable(true);
            restoreCardAction.setVisible(false);
            favoriteAction.setDisable(true)
                    .setSelected(false);
        }

        var pasteEnable = false;
        var cb = Clipboard.getSystemClipboard();
        if (item != null && cb.hasContent(Card.DATA_FORMAT)) {
            var sourceId = (UUID) cb.getContent(Card.DATA_FORMAT);
            var sourceItem = findRecordById(sourceId);
            pasteEnable = sourceItem.isPresent();
        }
        pasteAction.setDisable(!pasteEnable);

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
        var file = fileChooser(fxString(UI_BUNDLE, I18N_NEW_FILE), List.of(EXTENSION_FILTER))
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
        var file = fileChooser(fxString(UI_BUNDLE, I18N_OPEN), List.of(EXTENSION_FILTER))
                .showOpenDialog(getStage());
        if (file != null) {
            loadDocument(file, true);
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
            new PasswordDialog(this, file, false).showAndWait().ifPresent(password -> {
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
                    alert.setTitle(fxString(UI_BUNDLE, I18N_ERROR));
                    alert.setHeaderText(fxString(UI_BUNDLE, I18N_UNABLE_TO_READ_FILE, ": ") + path);
                    alert.showAndWait();
                    LOGGER.log(Level.SEVERE, "Exception while reading file " + path, ex);
                }
            });
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
        newConfirmationAlert(fxString(UI_BUNDLE, I18N_SURE_TO_PURGE)).showAndWait()
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
}
