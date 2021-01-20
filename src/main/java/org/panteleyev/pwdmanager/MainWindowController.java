/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.panteleyev.crypto.AES;
import org.panteleyev.fx.Controller;
import org.panteleyev.pwdmanager.filters.FieldContentFilter;
import org.panteleyev.pwdmanager.filters.RecordNameFilter;
import org.panteleyev.pwdmanager.model.Card;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import static org.panteleyev.fx.ButtonFactory.button;
import static org.panteleyev.fx.FxFactory.newSearchField;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.MenuFactory.checkMenuItem;
import static org.panteleyev.fx.MenuFactory.menuBar;
import static org.panteleyev.fx.MenuFactory.menuItem;
import static org.panteleyev.fx.MenuFactory.newMenu;
import static org.panteleyev.pwdmanager.Constants.APP_TITLE;
import static org.panteleyev.pwdmanager.Constants.COMPARE_CARDS_BY_FAVORITE;
import static org.panteleyev.pwdmanager.Constants.COMPARE_CARDS_BY_NAME;
import static org.panteleyev.pwdmanager.Constants.RB;
import static org.panteleyev.pwdmanager.Constants.SHIFT_DELETE;
import static org.panteleyev.pwdmanager.Constants.SHORTCUT_C;
import static org.panteleyev.pwdmanager.Constants.SHORTCUT_D;
import static org.panteleyev.pwdmanager.Constants.SHORTCUT_F;
import static org.panteleyev.pwdmanager.Constants.SHORTCUT_I;
import static org.panteleyev.pwdmanager.Constants.SHORTCUT_N;
import static org.panteleyev.pwdmanager.Constants.SHORTCUT_O;
import static org.panteleyev.pwdmanager.Constants.SHORTCUT_T;
import static org.panteleyev.pwdmanager.Constants.SHORTCUT_V;
import static org.panteleyev.pwdmanager.Constants.STYLE_CARD_CONTENT_TITLE;
import static org.panteleyev.pwdmanager.Options.options;

class MainWindowController extends Controller {
    private static final Logger LOGGER = Logger.getLogger(PasswordManagerApplication.class.getName());

    // Preferences
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(MainWindowController.class);
    private static final String PREF_CURRENT_FILE = "current_file";

    private final SimpleObjectProperty<File> currentFile = new SimpleObjectProperty<>();
    private String currentPassword;

    private final ObservableList<Card> recordList = FXCollections.observableArrayList();
    private final SortedList<Card> sortedList = new SortedList<>(recordList);
    private final ListView<Card> cardListView = new ListView<>(sortedList);

    private final BorderPane leftPane = new BorderPane(cardListView);
    private final TitledPane treeViewPane = new TitledPane("", leftPane);

    private final TextField searchTextField = newSearchField(TextFields::createClearableTextField, this::doSearch);

    private final Label cardContentTitleLabel = new Label();
    private final Button cardEditButton = button(fxString(RB, "button.edit"), a -> onEditCard());
    private final BorderPane recordViewPane = new BorderPane();

    private final NoteViewer noteViewer = new NoteViewer();
    private final CardViewer cardContentView = new CardViewer();

    MainWindowController(Stage stage) {
        super(stage, options().getMainCssFilePath());

        sortedList.setComparator(COMPARE_CARDS_BY_FAVORITE.thenComparing(COMPARE_CARDS_BY_NAME));

        setupWindow(new BorderPane(createControls(), createMainMenu(), null, null, null));
        getStage().setOnHiding(event -> onWindowClosing());

        Options.loadWindowDimensions(getStage());
        Options.loadPasswordOptions();

        initialize();
    }

    private MenuBar createMainMenu() {
        var pasteMenuItem = menuItem(fxString(RB, "menu.edit.paste"), SHORTCUT_V, a -> onCardPaste());
        var favoriteMenuItem = checkMenuItem(fxString(RB, "menu.edit.favorite"), false, SHORTCUT_I,
            a -> onFavorite());

        var editMenu = newMenu(fxString(RB, "menu.edit"),
            menuItem(fxString(RB, "menu.edit.newCard"), SHORTCUT_D, a -> onNewCard(),
                currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
            menuItem(fxString(RB, "menu.edit.newNote"), SHORTCUT_T, a -> onNewNote(),
                currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
            new SeparatorMenuItem(),
            menuItem(fxString(RB, "menu.Edit.Filter"), SHORTCUT_F, a -> onFilter()),
            new SeparatorMenuItem(),
            favoriteMenuItem,
            new SeparatorMenuItem(),
            menuItem(fxString(RB, "menu.edit.copy"), SHORTCUT_C, a -> onCardCopy()),
            pasteMenuItem,
            new SeparatorMenuItem(),
            menuItem(fxString(RB, "menu.edit.delete"), SHIFT_DELETE, a -> onDeleteRecord(), currentFile.isNull())
        );
        editMenu.setOnShowing(e -> setupEditMenuItems(favoriteMenuItem, pasteMenuItem));

        return menuBar(
            newMenu(fxString(RB, "menu.file"),
                menuItem(fxString(RB, "menu.file.new"), SHORTCUT_N, a -> onNewFile()),
                menuItem(fxString(RB, "menu.file.open"), SHORTCUT_O, a -> onOpenFile()),
                new SeparatorMenuItem(),
                menuItem(fxString(RB, "menu.file.exit"), a -> onExit())
            ),
            // Edit
            editMenu,
            // Tools
            newMenu(fxString(RB, "menu.tools"),
                menuItem(fxString(RB, "menu.tools.import"), a -> onImportFile()),
                menuItem(fxString(RB, "menu.tools.export"), a -> onExportFile(), currentFile.isNull()),
                new SeparatorMenuItem(),
                menuItem(fxString(RB, "menu.tools.changePassword"),
                    a -> onChangePassword(), currentFile.isNull()),
                new SeparatorMenuItem(),
                menuItem(fxString(RB, "Options", "..."), a -> onOptions())
            ),
            // Help
            newMenu(fxString(RB, "menu.help"),
                menuItem(fxString(RB, "menu.help.about"), a -> onAbout()))
        );
    }

    private Control createControls() {
        BorderPane.setAlignment(cardListView, Pos.CENTER);

        cardListView.setCellFactory(x -> new RecordListCell());
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
        var ctxCardPasteMenuItem = menuItem(fxString(RB, "menu.edit.paste"), a -> onCardPaste());
        var ctxFavoriteMenuItem = checkMenuItem(fxString(RB, "menu.edit.favorite"), false,
            a -> onFavorite());

        var menu = new ContextMenu(
            ctxFavoriteMenuItem,
            new SeparatorMenuItem(),
            menuItem(fxString(RB, "menu.edit.newCard"), a -> onNewCard(),
                currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
            menuItem(fxString(RB, "menu.edit.newNote"), a -> onNewNote(),
                currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
            new SeparatorMenuItem(),
            menuItem(fxString(RB, "menu.edit.delete"), a -> onDeleteRecord(),
                currentFile.isNull()),
            new SeparatorMenuItem(),
            menuItem(fxString(RB, "menu.edit.copy"), a -> onCardCopy()),
            ctxCardPasteMenuItem
        );

        menu.setOnShowing(e -> setupEditMenuItems(ctxFavoriteMenuItem, ctxCardPasteMenuItem));
        return menu;
    }

    private void setupEditMenuItems(CheckMenuItem favoriteMenuItem, MenuItem pasteMenuItem) {
        var pasteEnable = false;

        var cb = Clipboard.getSystemClipboard();
        var targetItem = getSelectedItem();

        favoriteMenuItem.setDisable(targetItem.isEmpty());
        favoriteMenuItem.setSelected(targetItem.isPresent() && targetItem.get().favorite());

        if (cb.hasContent(Card.DATA_FORMAT) && targetItem.isPresent()) {
            var sourceId = (String) cb.getContent(Card.DATA_FORMAT);
            var sourceItem = findRecordById(sourceId);
            pasteEnable = sourceItem.isPresent();
        }

        pasteMenuItem.setDisable(!pasteEnable);
    }

    private void initialize() {
        cardListView.getSelectionModel().selectedItemProperty().addListener(x -> onListViewSelected());

        cardEditButton.disableProperty().bind(cardListView.getSelectionModel().selectedItemProperty().isNull());

        leftPane.setTop(searchTextField);
        BorderPane.setMargin(searchTextField, new Insets(0, 0, 10, 0));

        // Cmd parameter overrides stored file but does not overwrite the setting.
        var fileName = System.getProperty("password.file");
        if (fileName != null && !fileName.isEmpty()) {
            loadDocument(new File(fileName), false);
        } else {
            var currentFilePath = PREFERENCES.get(PREF_CURRENT_FILE, null);
            if (currentFilePath != null) {
                loadDocument(new File(currentFilePath), true);
            }
        }
        Platform.runLater(cardListView::requestFocus);
    }

    private void doSearch(String newValue) {
        if (newValue.isEmpty()) {
            cardListView.setItems(sortedList);
        } else {
            cardListView.setItems(sortedList.filtered(
                new RecordNameFilter(newValue).or(new FieldContentFilter(newValue))));
        }
    }

    private Optional<Card> getSelectedItem() {
        return Optional.ofNullable(cardListView.getSelectionModel().getSelectedItem());
    }

    private void onExit() {
        if (currentFile.get() != null) {
            writeDocument();
        }
        System.exit(0);
    }

    private void onImportFile() {
        var d = new FileChooser();
        d.setTitle(fxString(RB, "Open File"));
        d.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showOpenDialog(null);
        if (file != null && file.exists()) {
            new PasswordDialog(this, file, false).showAndWait().ifPresent(password -> {
                try (var in = new FileInputStream(file)) {
                    var list = new ArrayList<Card>();
                    if (!password.isEmpty()) {
                        try (var cin = AES.aes256().getInputStream(in, password)) {
                            Serializer.deserialize(cin, list);
                        }
                    } else {
                        Serializer.deserialize(in, list);
                    }

                    importCards(list);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    private void onExportFile() {
        var d = new FileChooser();
        d.setTitle(fxString(RB, "Save File"));
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

    private void processNewRecord(Card newRecord) {
        recordList.add(newRecord);
        cardListView.getSelectionModel().select(newRecord);
        cardListView.scrollTo(newRecord);
        writeDocument();
    }

    private void onDeleteRecord() {
        getSelectedItem().ifPresent((Card item) -> {
            var alert = new Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().filter(x -> x == ButtonType.YES).ifPresent(x -> recordList.remove(item));
        });
    }

    private void onNewCard() {
        new CardDialog(RecordType.PASSWORD, null)
            .showAndWait()
            .ifPresent(this::processNewRecord);
    }

    private void onNewNote() {
        new NoteDialog().showAndWait().ifPresent(this::processNewRecord);
    }

    private void onFilter() {
        searchTextField.requestFocus();
    }

    private void setupRecordViewer(Card record) {
        cardContentTitleLabel.setText(record.name());
        cardContentTitleLabel.setGraphic(new ImageView(record.picture().getBigImage()));

        if (record.isNote()) {
            noteViewer.setText(record.note());
            recordViewPane.setCenter(noteViewer);
        } else {
            if (record.isCard()) {
                recordViewPane.setCenter(cardContentView);

                var wrappers = record.fields().stream()
                    .filter(f -> !f.value().isEmpty())
                    .map(FieldWrapper::new).collect(Collectors.toList());
                cardContentView.setData(FXCollections.observableArrayList(wrappers), record.note());
            }
        }
    }

    private void onListViewSelected() {
        var item = cardListView.getSelectionModel().getSelectedItem();

        if (item == null) {
            recordViewPane.setCenter(null);
            cardContentTitleLabel.setText(null);
            cardContentTitleLabel.setGraphic(null);
        } else {
            setupRecordViewer(item);
        }
    }

    private Optional<Card> findByUuid(String uuid) {
        return recordList.stream().filter(x -> x.uuid().equals(uuid)).findFirst();
    }


    private void processEditedRecord(Card r) {
        var index = getIndexByUUID(r.uuid());
        if (index != -1) {
            recordList.set(index, r);
        }

        cardListView.scrollTo(r);
        cardListView.getSelectionModel().select(r);

        writeDocument();
    }

    private void onEditCard() {
        getSelectedItem().ifPresent(card -> {
            switch (card.cardClass()) {
                case CARD -> new EditCardDialog(card).showAndWait().ifPresent(this::processEditedRecord);
                case NOTE -> new EditNoteDialog(card).showAndWait().ifPresent(this::processEditedRecord);
            }
        });
    }

    private void onAbout() {
        new AboutDialog(this).showAndWait();
    }

    private void putCardToClipboard(Card record) {
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
        var sourceId = (String) cb.getContent(Card.DATA_FORMAT);

        findByUuid(sourceId).ifPresent(sourceRecord -> {
            var newRecord = sourceRecord.copyWithNewUuid();
            recordList.add(newRecord);
            cardListView.getSelectionModel().select(newRecord);
            cardListView.scrollTo(newRecord);
            writeDocument();
        });
    }

    private int getIndexByUUID(String uuid) {
        for (int index = 0; index < recordList.size(); index++) {
            if (recordList.get(index).uuid().equals(uuid)) {
                return index;
            }
        }
        return -1;
    }

    private void updateListItem(Card card) {
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

    private Optional<Card> findRecordById(String uuid) {
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
        d.setTitle(fxString(RB, "New File"));
        d.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showSaveDialog(null);
        if (file != null) {
            new PasswordDialog(this, file, true).showAndWait().ifPresent(password -> {
                currentPassword = password;
                recordList.clear();

                currentFile.set(file);
                PREFERENCES.put(PREF_CURRENT_FILE, currentFile.get().getAbsolutePath());

                setTitle();
                writeDocument();
            });
        }
    }

    private void onOpenFile() {
        var d = new FileChooser();
        d.setTitle(fxString(RB, "Open File"));
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
                PREFERENCES.put(PREF_CURRENT_FILE, "");
            }
            setTitle();
        } else {
            new PasswordDialog(null, file, false).showAndWait().ifPresent(password -> {
                currentPassword = password;

                try (var in = new FileInputStream(file)) {
                    var list = new ArrayList<Card>();
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
                        PREFERENCES.put(PREF_CURRENT_FILE, currentFile.get().getAbsolutePath());
                    }

                    setTitle();
                } catch (Exception ex) {
                    var path = file.getAbsolutePath();
                    var alert = new Alert(Alert.AlertType.ERROR, ex.toString());
                    alert.setTitle(fxString(RB, "Error"));
                    alert.setHeaderText(fxString(RB, "Unable to read file", ": ") + path);
                    alert.showAndWait();
                    LOGGER.log(Level.SEVERE, "Exception while reading file " + path, ex);
                }
            });
        }
    }

    private Optional<Card> find(Predicate<Card> filter) {
        return recordList.stream().filter(filter).findFirst();
    }

    private void importCards(Collection<Card> toImport) {
        for (var card : toImport) {
            find(c -> card.uuid().equals(c.uuid()))
                .ifPresentOrElse(found -> {
                    if (found.modified() < card.modified()) {
                        recordList.removeAll(found);
                        recordList.add(card);
                    }
                }, () -> recordList.add(card));
        }
    }

    private void writeDocument() {
        Objects.requireNonNull(currentFile);
        writeDocument(currentFile.get(), currentPassword);
    }

    private void writeDocument(File file, String password) {
        try (var bOut = new ByteArrayOutputStream()) {
            if (!password.isEmpty()) {
                try (var cout = AES.aes256().getOutputStream(bOut, password)) {
                    Serializer.serialize(cout, recordList);
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
        new OptionsDialog(this).showAndWait();
    }

    private void onWindowClosing() {
        Options.saveWindowDimensions(getStage());
        Options.saveOptions();
    }
}
