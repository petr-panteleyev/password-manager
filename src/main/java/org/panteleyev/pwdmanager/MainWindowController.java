/*
 * Copyright (c) 2016, 2020, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.panteleyev.commons.crypto.AES;
import org.panteleyev.commons.fx.Controller;
import org.panteleyev.pwdmanager.comparators.ByFavorite;
import org.panteleyev.pwdmanager.comparators.ByName;
import org.panteleyev.pwdmanager.filters.FieldContentFilter;
import org.panteleyev.pwdmanager.filters.RecordNameFilter;
import org.panteleyev.pwdmanager.model.Card;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import static org.panteleyev.commons.fx.FXFactory.newButton;
import static org.panteleyev.commons.fx.FXFactory.newCheckMenuItem;
import static org.panteleyev.commons.fx.FXFactory.newMenu;
import static org.panteleyev.commons.fx.FXFactory.newMenuBar;
import static org.panteleyev.commons.fx.FXFactory.newMenuItem;
import static org.panteleyev.commons.fx.FXFactory.newSearchField;
import static org.panteleyev.pwdmanager.PasswordManagerApplication.RB;

class MainWindowController extends Controller implements Styles {
    private static final Logger LOGGER = Logger.getLogger(PasswordManagerApplication.class.getName());

    static final URL CSS_PATH = MainWindowController.class
        .getResource("/org/panteleyev/pwdmanager/PasswordManager.css");

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

    private final TextField searchTextField = newSearchField(this::doSearch);

    private final Label cardContentTitleLabel = new Label();
    private final Button cardEditButton = newButton(RB, "button.edit", a -> onEditCard());
    private final BorderPane recordViewPane = new BorderPane();

    private final NoteViewer noteViewer = new NoteViewer();
    private final CardViewer cardContentView = new CardViewer();

    MainWindowController(Stage stage) {
        super(stage, CSS_PATH.toString());

        sortedList.setComparator(new ByFavorite().thenComparing(new ByName()));

        setupWindow(new BorderPane(createControls(), createMainMenu(), null, null, null));
        getStage().setOnHiding(event -> onWindowClosing());
        Options.loadWindowDimensions(getStage());

        initialize();
    }

    private MenuBar createMainMenu() {
        return newMenuBar(
            newMenu(RB, "menu.file",
                newMenuItem(RB, "menu.file.new",
                    new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN), a -> onNewFile()),
                newMenuItem(RB, "menu.file.open",
                    new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN), a -> onOpenFile()),
                new SeparatorMenuItem(),
                newMenuItem(RB, "menu.file.exit", a -> onExit())
            ),
            // Edit
            newMenu(RB, "menu.edit",
                newMenuItem(RB, "menu.edit.newCard", a -> onNewCard(),
                    currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                newMenuItem(RB, "menu.edit.newNote", a -> onNewNote(),
                    currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
                new SeparatorMenuItem(),
                newMenuItem(RB, "menu.Edit.Filter",
                    new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN), a -> onFilter()),
                new SeparatorMenuItem(),
                newMenuItem(RB, "menu.edit.delete", a -> onDeleteRecord(), currentFile.isNull())),
            // Tools
            newMenu(RB, "menu.tools",
                newMenuItem(RB, "menu.tools.import", a -> onImportFile()),
                newMenuItem(RB, "menu.tools.export", a -> onExportFile(), currentFile.isNull()),
                new SeparatorMenuItem(),
                newMenuItem(RB, "menu.tools.changePassword",
                    a -> onChangePassword(), currentFile.isNull())
            ),
            // Help
            newMenu(RB, "menu.help",
                newMenuItem(RB, "menu.help.about", a -> onAbout()))
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

        cardContentTitleLabel.getStyleClass().add(CARD_CONTENT_TITLE);
        BorderPane.setAlignment(cardContentTitleLabel, Pos.CENTER);
        BorderPane.setAlignment(buttonBar, Pos.CENTER);

        treeViewPane.setMaxHeight(Double.MAX_VALUE);
        treeViewPane.setMaxWidth(Double.MAX_VALUE);

        var split = new SplitPane(treeViewPane, recordViewPane);
        split.setDividerPositions(0.30);

        return split;
    }

    private ContextMenu createContextMenu() {
        var ctxCardPasteMenuItem = newMenuItem(RB, "menu.edit.paste", a -> onCardPaste());
        var ctxFavoriteMenuItem = newCheckMenuItem(RB, "menu.edit.favorite", false,
            a -> onFavorite());

        var menu = new ContextMenu(
            ctxFavoriteMenuItem,
            new SeparatorMenuItem(),
            newMenuItem(RB, "menu.edit.newCard", a -> onNewCard(),
                currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
            newMenuItem(RB, "menu.edit.newNote", a -> onNewNote(),
                currentFile.isNull().or(searchTextField.textProperty().isEmpty().not())),
            new SeparatorMenuItem(),
            newMenuItem(RB, "menu.edit.delete", new KeyCodeCombination(KeyCode.DELETE), a -> onDeleteRecord(),
                currentFile.isNull()),
            new SeparatorMenuItem(),
            newMenuItem(RB, "menu.edit.copy", a -> onCardCopy()),
            ctxCardPasteMenuItem
        );

        menu.setOnShowing(e -> {
            var pasteEnable = false;

            var cb = Clipboard.getSystemClipboard();
            var targetItem = getSelectedItem();

            ctxFavoriteMenuItem.setDisable(targetItem.isEmpty());
            ctxFavoriteMenuItem.setSelected(targetItem.isPresent() && targetItem.get().isFavorite());

            if (cb.hasContent(Card.DATA_FORMAT) && targetItem.isPresent()) {
                var sourceId = (String) cb.getContent(Card.DATA_FORMAT);
                var sourceItem = findRecordById(sourceId);
                pasteEnable = sourceItem.isPresent();
            }

            ctxCardPasteMenuItem.setDisable(!pasteEnable);
        });

        return menu;
    }

    private void initialize() {
        cardListView.getSelectionModel().selectedItemProperty().addListener(x -> onListViewSelected());

        cardEditButton.disableProperty().bind(cardListView.getSelectionModel().selectedItemProperty().isNull());

        leftPane.setTop(searchTextField);
        BorderPane.setMargin(searchTextField, new Insets(0, 0, 10, 0));

        // Cmd parameter overrides stored file but does not overwrite the setting.
        var params = PasswordManagerApplication.getApplication().getParameters();
        var fileName = params.getNamed().get("file");
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
        d.setTitle("Open File");
        d.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showOpenDialog(null);
        if (file != null && file.exists()) {
            new PasswordDialog(file, false).showAndWait().ifPresent(password -> {
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
        d.setTitle("Save File");
        d.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showSaveDialog(null);
        if (file != null) {
            new PasswordDialog(file, false)
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
        cardContentTitleLabel.setText(record.getName());
        cardContentTitleLabel.setGraphic(new ImageView(record.getPicture().getBigImage()));

        if (record.isNote()) {
            noteViewer.setText(record.getNote());
            recordViewPane.setCenter(noteViewer);
        } else {
            if (record.isCard()) {
                recordViewPane.setCenter(cardContentView);

                var wrappers = record.getFields().stream()
                    .filter(f -> !f.getValue().isEmpty())
                    .map(FieldWrapper::new).collect(Collectors.toList());
                cardContentView.setData(FXCollections.observableArrayList(wrappers), record.getNote());
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
        return recordList.stream().filter(x -> x.getUuid().equals(uuid)).findFirst();
    }


    private void processEditedRecord(Card r) {
        var index = getIndexByUUID(r.getUuid());
        if (index != -1) {
            recordList.set(index, r);
        }

        cardListView.scrollTo(r);
        cardListView.getSelectionModel().select(r);

        writeDocument();
    }

    private void onEditCard() {
        getSelectedItem().ifPresent(card -> {
            switch (card.getCardClass()) {
                case CARD:
                    new EditCardDialog(card)
                        .showAndWait().ifPresent(this::processEditedRecord);
                    break;
                case NOTE:
                    new EditNoteDialog(card)
                        .showAndWait().ifPresent(this::processEditedRecord);
                    break;
            }
        });
    }

    private void onAbout() {
        new AboutDialog().showAndWait();
    }

    private void putCardToClipboard(Card record) {
        var cb = Clipboard.getSystemClipboard();
        var content = new ClipboardContent();
        content.put(Card.DATA_FORMAT, record.getUuid());
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
            if (recordList.get(index).getUuid().equals(uuid)) {
                return index;
            }
        }
        return -1;
    }

    private void updateListItem(Card card) {
        var index = getIndexByUUID(card.getUuid());
        if (index != -1) {
            recordList.set(index, card);
        }
    }

    private void onFavorite() {
        getSelectedItem().ifPresent(card -> {
            var newCard = card.setFavorite(!card.isFavorite());
            updateListItem(newCard);
            cardListView.getSelectionModel().select(newCard);
            cardListView.scrollTo(newCard);
            writeDocument();
        });
    }

    private Optional<Card> findRecordById(String uuid) {
        return sortedList.stream().filter(x -> x.getUuid().equals(uuid)).findFirst();
    }

    private void setTitle() {
        if (currentFile.get() == null) {
            getStage().setTitle(AboutDialog.APP_TITLE);
        } else {
            getStage().setTitle(AboutDialog.APP_TITLE + " -- " + currentFile.get().getAbsolutePath());
        }
    }

    private void onNewFile() {
        var d = new FileChooser();
        d.setTitle("New File");
        d.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showSaveDialog(null);
        if (file != null) {
            new PasswordDialog(file, true).showAndWait().ifPresent(password -> {
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
        d.setTitle("Open File");
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
            new PasswordDialog(file, false).showAndWait().ifPresent(password -> {
                currentPassword = password;

                try (InputStream in = new FileInputStream(file)) {
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
                    new Alert(Alert.AlertType.ERROR, "Exception while reading file "
                        + file.getAbsolutePath()).showAndWait();
                    LOGGER.log(Level.SEVERE, "Exception while reading file", ex);
                }
            });
        }
    }

    private Optional<Card> find(Predicate<Card> filter) {
        return recordList.stream().filter(filter).findFirst();
    }

    private void importCards(Collection<Card> toImport) {
        for (var card : toImport) {
            find(c -> card.getUuid().equals(c.getUuid()))
                .ifPresentOrElse(found -> {
                    if (found.getModified() < card.getModified()) {
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
        new PasswordDialog(currentFile.get(), true).showAndWait().ifPresent(password -> {
            currentPassword = password;
            writeDocument();
        });
    }

    private void onWindowClosing() {
        Options.saveWindowDimensions(getStage());
    }
}
