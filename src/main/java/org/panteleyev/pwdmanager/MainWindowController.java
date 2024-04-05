/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
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
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.textfield.TextFields;
import org.panteleyev.crypto.AES;
import org.panteleyev.freedesktop.Utility;
import org.panteleyev.freedesktop.entry.DesktopEntryBuilder;
import org.panteleyev.freedesktop.entry.DesktopEntryType;
import org.panteleyev.freedesktop.menu.Category;
import org.panteleyev.fx.Controller;
import org.panteleyev.fx.PredicateProperty;
import org.panteleyev.pwdmanager.cells.RecordListCell;
import org.panteleyev.pwdmanager.filters.FieldContentFilter;
import org.panteleyev.pwdmanager.filters.RecordNameFilter;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Note;
import org.panteleyev.pwdmanager.model.RecordType;
import org.panteleyev.pwdmanager.model.WalletRecord;
import org.panteleyev.pwdmanager.settings.SettingsDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;
import static org.panteleyev.freedesktop.entry.DesktopEntryBuilder.localeString;
import static org.panteleyev.fx.ButtonFactory.button;
import static org.panteleyev.fx.FxFactory.newSearchField;
import static org.panteleyev.fx.FxUtils.ELLIPSIS;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.MenuFactory.*;
import static org.panteleyev.pwdmanager.Constants.APP_TITLE;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.ImportUtil.calculateImport;
import static org.panteleyev.pwdmanager.Shortcuts.*;
import static org.panteleyev.pwdmanager.Styles.STYLE_CARD_CONTENT_TITLE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.*;
import static org.panteleyev.pwdmanager.model.Card.*;
import static org.panteleyev.pwdmanager.model.Picture.BIG_IMAGE_SIZE;
import static org.panteleyev.pwdmanager.model.Picture.imageView;

final class MainWindowController extends Controller {
    private static final Logger LOGGER = Logger.getLogger(PasswordManagerApplication.class.getName());

    private final BooleanProperty showDeletedRecords = new SimpleBooleanProperty(false);
    private final PredicateProperty<WalletRecord> defaultFilter = new PredicateProperty<>(WalletRecord::active);

    private final SimpleObjectProperty<File> currentFile = new SimpleObjectProperty<>();
    private String currentPassword;

    private final ObservableList<WalletRecord> recordList = FXCollections.observableArrayList();
    private final SortedList<WalletRecord> sortedList = new SortedList<>(recordList);
    private final FilteredList<WalletRecord> filteredList = sortedList.filtered(defaultFilter);
    private final ListView<WalletRecord> cardListView = new ListView<>(filteredList);

    private final BorderPane leftPane = new BorderPane(cardListView);
    private final TitledPane treeViewPane = new TitledPane("", leftPane);

    private final TextField searchTextField = newSearchField(TextFields::createClearableTextField, this::doSearch);

    private final Label cardContentTitleLabel = new Label();
    private final Button cardEditButton = button(fxString(UI_BUNDLE, I18N_EDIT_BUTTON, ELLIPSIS), _ -> onEditCard());
    private final BorderPane recordViewPane = new BorderPane();

    private final NoteViewer noteViewer = new NoteViewer();
    private final CardViewer cardContentView = new CardViewer();

    MainWindowController(Stage stage) {
        super(stage, settings().getMainCssFilePath());

        sortedList.setComparator(COMPARE_BY_ACTIVE
                .thenComparing(COMPARE_BY_FAVORITE)
                .thenComparing(COMPARE_BY_NAME));

        filteredList.predicateProperty().bind(defaultFilter);

        setupWindow(new BorderPane(createControls(), createMainMenu(), null, null, null));

        settings().loadStageDimensions(this);

        initialize();
    }

    private MenuBar createMainMenu() {
        var pasteMenuItem = menuItem(fxString(UI_BUNDLE, I18N_PASTE), SHORTCUT_V, _ -> onCardPaste());
        var favoriteMenuItem = checkMenuItem(fxString(UI_BUNDLE, I18N_FAVORITE), false, SHORTCUT_I,
                _ -> onFavorite());

        var editMenu = newMenu(fxString(UI_BUNDLE, I18N_EDIT),
                menuItem(fxString(UI_BUNDLE, I18N_NEW_CARD, ELLIPSIS), SHORTCUT_D, _ -> onNewCard(),
                        currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                menuItem(fxString(UI_BUNDLE, I18N_NEW_NOTE, ELLIPSIS), SHORTCUT_T, _ -> onNewNote(),
                        currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_FILTER), SHORTCUT_F, _ -> onFilter()),
                new SeparatorMenuItem(),
                favoriteMenuItem,
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_COPY), SHORTCUT_C,
                        _ -> onCardCopy(), selectedItemProperty().isNull()),
                pasteMenuItem,
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_DELETE), SHIFT_DELETE,
                        _ -> onDeleteRecord(), selectedItemProperty().isNull()),
                menuItem(fxString(UI_BUNDLE, I18N_RESTORE), _ -> onRestoreRecord(), selectedItemProperty().isNull())
        );
        editMenu.setOnShowing(_ -> setupEditMenuItems(favoriteMenuItem, pasteMenuItem));

        var showDeletedItemsMenuItem = checkMenuItem(fxString(UI_BUNDLE, I18N_SHOW_DELETED), false);
        showDeletedRecords.bind(showDeletedItemsMenuItem.selectedProperty());
        showDeletedRecords.addListener((_, _, newValue) -> onShowDeletedItems(newValue));

        var menuBar = menuBar(
                newMenu(fxString(UI_BUNDLE, I18N_FILE),
                        menuItem(fxString(UI_BUNDLE, I18N_NEW_FILE), SHORTCUT_N, _ -> onNewFile()),
                        menuItem(fxString(UI_BUNDLE, I18N_OPEN), SHORTCUT_O, _ -> onOpenFile()),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI_BUNDLE, I18N_EXIT), _ -> onExit())
                ),
                // Edit
                editMenu,
                newMenu(fxString(UI_BUNDLE, I18N_VIEW),
                        showDeletedItemsMenuItem
                ),
                // Tools
                newMenu(fxString(UI_BUNDLE, I18N_TOOLS),
                        menuItem(fxString(UI_BUNDLE, I18N_IMPORT, ELLIPSIS), _ -> onImportFile(), currentFile.isNull()),
                        menuItem(fxString(UI_BUNDLE, I18N_EXPORT, ELLIPSIS), _ -> onExportFile(), currentFile.isNull()),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI_BUNDLE, I18N_PURGE, ELLIPSIS), _ -> onPurge(), currentFile.isNull()),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI_BUNDLE, I18N_CHANGE_PASSWORD, ELLIPSIS),
                                _ -> onChangePassword(), currentFile.isNull()),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI_BUNDLE, I18N_OPTIONS, ELLIPSIS), SHORTCUT_ALT_S, _ -> onOptions()),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI_BUNDLE, I18N_CREATE_DESKTOP_ENTRY), _ -> onCreateDesktopEntry())
                ),
                // Help
                newMenu(fxString(UI_BUNDLE, I18N_HELP),
                        menuItem(fxString(UI_BUNDLE, I18N_HELP_ABOUT, ELLIPSIS), _ -> onAbout()))
        );
        menuBar.getMenus().forEach(menu -> menu.disableProperty().bind(getStage().focusedProperty().not()));

        return menuBar;
    }

    private Control createControls() {
        BorderPane.setAlignment(cardListView, Pos.CENTER);

        cardListView.setCellFactory(_ -> new RecordListCell());
        cardListView.setContextMenu(createContextMenu());

        var buttonBar = new ButtonBar();
        buttonBar.getButtons().setAll(cardEditButton);

        recordViewPane.setTop(cardContentTitleLabel);
        recordViewPane.setBottom(buttonBar);

        BorderPane.setMargin(buttonBar, new Insets(5, 5, 5, 5));

        cardContentTitleLabel.getStyleClass().add(STYLE_CARD_CONTENT_TITLE);
        BorderPane.setAlignment(cardContentTitleLabel, Pos.CENTER);
        BorderPane.setAlignment(buttonBar, Pos.CENTER);

        treeViewPane.setMaxHeight(Double.MAX_VALUE);
        treeViewPane.setMaxWidth(Double.MAX_VALUE);

        var split = new SplitPane(treeViewPane, recordViewPane);
        split.setDividerPositions(0.30);

        return split;
    }

    private ContextMenu createContextMenu() {
        var ctxCardPasteMenuItem = menuItem(fxString(UI_BUNDLE, I18N_PASTE), _ -> onCardPaste());
        var ctxFavoriteMenuItem = checkMenuItem(fxString(UI_BUNDLE, I18N_FAVORITE), false,
                _ -> onFavorite());

        var menu = new ContextMenu(
                ctxFavoriteMenuItem,
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_NEW_CARD, ELLIPSIS), _ -> onNewCard(),
                        currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                menuItem(fxString(UI_BUNDLE, I18N_NEW_NOTE, ELLIPSIS), _ -> onNewNote(),
                        currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_DELETE), _ -> onDeleteRecord(),
                        selectedItemProperty().isNull()),
                menuItem(fxString(UI_BUNDLE, I18N_RESTORE), _ -> onRestoreRecord(),
                        selectedItemProperty().isNull()),
                new SeparatorMenuItem(),
                menuItem(fxString(UI_BUNDLE, I18N_COPY), _ -> onCardCopy()),
                ctxCardPasteMenuItem
        );

        menu.setOnShowing(_ -> setupEditMenuItems(ctxFavoriteMenuItem, ctxCardPasteMenuItem));
        return menu;
    }

    private void setupEditMenuItems(CheckMenuItem favoriteMenuItem, MenuItem pasteMenuItem) {
        var pasteEnable = false;

        var cb = Clipboard.getSystemClipboard();
        var targetItem = getSelectedItem();

        favoriteMenuItem.setDisable(targetItem.isEmpty());
        favoriteMenuItem.setSelected(targetItem.isPresent() && targetItem.get().favorite());

        if (cb.hasContent(Card.DATA_FORMAT) && targetItem.isPresent()) {
            var sourceId = (UUID) cb.getContent(Card.DATA_FORMAT);
            var sourceItem = findRecordById(sourceId);
            pasteEnable = sourceItem.isPresent();
        }

        pasteMenuItem.setDisable(!pasteEnable);
    }

    private void initialize() {
        cardListView.getSelectionModel().selectedItemProperty().addListener(_ -> onListViewSelected());

        cardEditButton.setDisable(true);
        leftPane.setTop(searchTextField);
        BorderPane.setMargin(searchTextField, new Insets(0, 0, 10, 0));

        // Cmd parameter overrides stored file but does not overwrite the setting.
        var fileName = System.getProperty("password.file");
        if (fileName != null && !fileName.isEmpty()) {
            loadDocument(new File(fileName), false);
        } else {
            var currentFilePath = settings().getCurrentFile();
            if (!currentFilePath.isEmpty()) {
                loadDocument(new File(currentFilePath), true);
            }
        }
        Platform.runLater(cardListView::requestFocus);
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
        var d = new FileChooser();
        d.setTitle(fxString(UI_BUNDLE, I18N_OPEN));
        d.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showOpenDialog(getStage());
        if (file == null || !file.exists()) {
            return;
        }

        new PasswordDialog(this, file, false).showAndWait().ifPresent(password -> {
            try (var in = new FileInputStream(file)) {
                var list = new ArrayList<WalletRecord>();
                if (!password.isEmpty()) {
                    try (var cin = AES.aes256().getInputStream(in, password)) {
                        Serializer.deserialize(cin, list);
                    }
                } else {
                    Serializer.deserialize(in, list);
                }

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
        var d = new FileChooser();
        d.setTitle(fxString(UI_BUNDLE, I18N_SAVE));
        d.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showSaveDialog(null);
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
            var alert = new Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait()
                    .filter(x -> x.equals(ButtonType.YES))
                    .ifPresent(_ -> {
                        var newCard = item.setActive(false);
                        updateListItem(newCard);
                        writeDocument();
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
        new CardDialog(RecordType.PASSWORD)
                .showAndWait()
                .ifPresent(this::processNewRecord);
    }

    private void onNewNote() {
        new NoteDialog().showAndWait().ifPresent(this::processNewRecord);
    }

    private void onFilter() {
        searchTextField.requestFocus();
    }

    private void setupRecordViewer(WalletRecord record) {
        cardContentTitleLabel.setText(record.name());
        cardContentTitleLabel.setGraphic(imageView(record.picture().getBigImage(), BIG_IMAGE_SIZE, BIG_IMAGE_SIZE));

        switch (record) {
            case Card card -> {
                recordViewPane.setCenter(cardContentView);
                var wrappers = card.fields().stream()
                        .filter(f -> !f.isEmpty())
                        .map(FieldWrapper::new)
                        .toList();
                cardContentView.setData(FXCollections.observableArrayList(wrappers), record.note());
            }
            case Note note -> {
                noteViewer.setText(note.note());
                recordViewPane.setCenter(noteViewer);
            }
        }
    }

    private void onListViewSelected() {
        var item = cardListView.getSelectionModel().getSelectedItem();
        cardEditButton.setDisable(item == null || !item.active());

        if (item == null) {
            recordViewPane.setCenter(null);
            cardContentTitleLabel.setText(null);
            cardContentTitleLabel.setGraphic(null);
        } else {
            setupRecordViewer(item);
        }
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
                case Card card -> new EditCardDialog(card);
                case Note note -> new EditNoteDialog(note);
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
        var d = new FileChooser();
        d.setTitle(fxString(UI_BUNDLE, I18N_NEW_FILE));
        d.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showSaveDialog(null);
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
        var d = new FileChooser();
        d.setTitle(fxString(UI_BUNDLE, I18N_OPEN));
        d.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );

        var file = d.showOpenDialog(null);
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
            new PasswordDialog(null, file, false).showAndWait().ifPresent(password -> {
                currentPassword = password;

                try (var in = new FileInputStream(file)) {
                    var list = new ArrayList<WalletRecord>();
                    if (!currentPassword.isEmpty()) {
                        try (var cin = AES.aes256().getInputStream(in, password)) {
                            Serializer.deserialize(cin, list);
                        }
                    } else {
                        Serializer.deserialize(in, list);
                    }

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
        var alert = new Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait()
                .filter(x -> x.equals(ButtonType.YES))
                .ifPresent(_ -> {
                    recordList.removeIf(card -> !card.active());
                    writeDocument();
                });
    }

    private void onCreateDesktopEntry() {
        if (!Utility.isLinux()) {
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
}
