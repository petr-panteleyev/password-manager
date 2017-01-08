/*
 * Copyright (c) 2016, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.panteleyev.crypto.AES;

public class MainWindowController implements Initializable {
    private static final String PREF_CURRENT_FILE = "currentFile";

    public static final String CSS_DROP_TARGET = "dropTarget";
    public static final String CSS_DROP_BELOW = "dropBelow";
    public static final String CSS_DROP_ABOVE = "dropAbove";

    public static final String[] CSS_DND_STYLES = { CSS_DROP_TARGET, CSS_DROP_BELOW, CSS_DROP_ABOVE };

    // TreeView clipboard
    private boolean cut = false;

    private MenuItem    cardPasteMenuItem;
    private MenuItem    cardPasteLinkMenuItem;

    @FXML private TextField         searchTextField;
    @FXML private TreeView<Record>  cardTreeView;
    @FXML private TitledPane        treeViewPane;
    @FXML private Label             cardContentTitleLabel;
    @FXML private Button            cardEditButton;
    @FXML private BorderPane        recordViewPane;

    // Menu items
    @FXML private MenuBar           menuBar;
    @FXML private MenuItem          newCardMenuItem;
    @FXML private MenuItem          newCategoryMenuItem;
    @FXML private MenuItem          newNoteMenuItem;
    @FXML private MenuItem          changePasswordMenuItem;
    @FXML private MenuItem          importMenuItem;
    @FXML private MenuItem          exportMenuItem;
    @FXML private MenuItem          deleteMenuItem;

    private final NoteViewer        noteViewer = new NoteViewer();
    private final CardViewer        cardContentView = new CardViewer();

    private Record rootRecord;

    private final SimpleObjectProperty<File> currentFile = new SimpleObjectProperty<>();
    private String currentPassword;

    private final Preferences preferences = Preferences.userNodeForPackage(MainWindowController.class);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cardTreeView.setContextMenu(createTreeViewContextMenu());
        cardTreeView.setCellFactory((TreeView<Record> p) -> new CardTreeViewCell(this));
        cardTreeView.setShowRoot(false);
        cardTreeView.getSelectionModel().selectedItemProperty().addListener(x -> {
            onTreeViewSelected();
        });

        menuBar.setUseSystemMenuBar(true);

        cardEditButton.disableProperty().bind(cardTreeView.getSelectionModel().selectedItemProperty().isNull());

        currentFile.addListener((ObservableValue<? extends File> observable, File oldValue, File newValue) -> {
            treeViewPane.setText(newValue != null? newValue.getName() : "");
        });

        String currentFilePath = preferences.get(PREF_CURRENT_FILE, null);
        if (currentFilePath != null) {
            loadDocument(new File(currentFilePath));
        }

        Platform.runLater(() -> cardTreeView.requestFocus());

        importMenuItem.disableProperty().bind(currentFile.isNull());
        exportMenuItem.disableProperty().bind(currentFile.isNull());
        newCardMenuItem.disableProperty().bind(currentFile.isNull());
        newCategoryMenuItem.disableProperty().bind(currentFile.isNull());
        newNoteMenuItem.disableProperty().bind(currentFile.isNull());
        changePasswordMenuItem.disableProperty().bind(currentFile.isNull());
        deleteMenuItem.disableProperty().bind(currentFile.isNull());
    }

    private Optional<TreeItem<Record>> getSelectedItem() {
        return Optional.ofNullable(cardTreeView.getSelectionModel().getSelectedItem());
    }

    private void loadDocument(File file) {
        if (!file.exists()) {
            currentFile.set(null);
            preferences.put(PREF_CURRENT_FILE, "");
        } else {
            PasswordDialog pwdDialog = new PasswordDialog(file);

            pwdDialog.showAndWait().ifPresent(password -> {
                currentPassword = password;

                try (InputStream in = new FileInputStream(file)) {
                    TreeItem<Record> root;

                    if (!currentPassword.isEmpty()) {
                        try (InputStream cin = AES.aes256().getInputStream(in, password)) {
                            root = Serializer.deserialize(cin);
                        }
                    } else {
                        root = Serializer.deserialize(in);
                    }

                    cardTreeView.setRoot(root);
                    currentFile.set(file);
                    preferences.put(PREF_CURRENT_FILE, currentFile.get().getAbsolutePath());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    public void writeDocument() {
        Objects.requireNonNull(currentFile);
        writeDocument(currentFile.get());
    }

    private void writeDocument(File file) {
        try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
            if (!currentPassword.isEmpty()) {
                try (OutputStream cout = AES.aes256().getOutputStream(bOut, currentPassword)) {
                    Serializer.serialize(cout, cardTreeView.getRoot());
                }
            } else {
                Serializer.serialize(bOut, cardTreeView.getRoot());
            }

            try (OutputStream fOut = new FileOutputStream(file)) {
                fOut.write(bOut.toByteArray());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void onExit() {
        if (currentFile.get() != null) {
            writeDocument();
        }
        System.exit(0);
    }

    public void onNewFile() throws Exception {
        FileChooser d = new FileChooser();
        d.setTitle("New File");
        d.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        File file = d.showSaveDialog(null);
        if (file != null) {
            PasswordDialog pwdDialog = new PasswordDialog(file, true);
            pwdDialog.showAndWait().ifPresent(password -> {
                currentPassword = password;

                rootRecord = new Category("root", RecordType.EMPTY, Picture.FOLDER);

                TreeItem<Record> root = new TreeItem<>(rootRecord);
                cardTreeView.setRoot(root);
                root.setExpanded(true);

                writeDocument(file);
                currentFile.set(file);
                preferences.put(PREF_CURRENT_FILE, currentFile.get().getAbsolutePath());
            });
        }
    }

    public void onOpenFile() throws Exception {
        FileChooser d = new FileChooser();
        d.setTitle("Open File");
        d.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        File file = d.showOpenDialog(null);
        if (file != null) {
            loadDocument(file);
        }
    }

    private void processNewRecord(Record record) {
        TreeItem<Record> selected = cardTreeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            selected = cardTreeView.getRoot();
        }

        Record parent = selected.getValue();
        if (!(parent instanceof Category)) {
            selected = cardTreeView.getRoot();
        }

        selected.getChildren().add(new TreeItem<>(record));
        selected.setExpanded(true);

        writeDocument();
    }

    public void onDeleteRecord() {
        getSelectedItem().ifPresent((TreeItem<Record> item) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().filter(x -> x == ButtonType.YES).ifPresent(x -> {
                String id = item.getValue().getId();

                TreeItem<Record> parent = item.getParent();
                parent.getChildren().remove(item);

                deleteBrokenLinks(id, cardTreeView.getRoot());

                writeDocument();
            });
        });
    }

    public void onNewCard() {
        TreeItem<Record> selected = cardTreeView.getSelectionModel().getSelectedItem();
        RecordType defaultType = (selected != null && selected.getValue() instanceof Category)?
            selected.getValue().getType() : RecordType.PASSWORD;

        CardDialog d = new CardDialog(defaultType, null);
        Optional<Card> result = d.showAndWait();
        if (result.isPresent()) {
            processNewRecord(result.get());
        }
    }

    public void onNewCategory() {
        CategoryDialog d = new CategoryDialog(null);
        Optional<Category> result = d.showAndWait();
        if (result.isPresent()) {
            processNewRecord(result.get());
        }
    }

    public void onNewNote() {
        new NoteDialog().showAndWait().ifPresent(this::processNewRecord);
    }

    private void setupRecordViewer(TreeItem<Record> item) {
        Record record = item.getValue();

        if (record instanceof Link) {
            Optional<TreeItem<Record>> target = findRecordById(((Link)record).getTargetId(), cardTreeView.getRoot());
            target.ifPresent(this::setupRecordViewer);
        } else {
            cardContentTitleLabel.setText(item.getValue().getName());
            cardContentTitleLabel.setGraphic(new ImageView(item.getValue().getPicture().getBigImage()));

            if (record instanceof Note) {
                noteViewer.setText(((Note)record).getText());
                recordViewPane.setCenter(noteViewer);
            } else {
                if (record instanceof Card) {
                    recordViewPane.setCenter(cardContentView);

                    List<FieldWrapper> wrappers = ((Card)record).getFields().stream()
                        .filter(f -> !f.getValue().isEmpty())
                        .map(FieldWrapper::new).collect(Collectors.toList());
                    cardContentView.setData(FXCollections.observableArrayList(wrappers), ((Card)record).getNote());
                } else {

                }
            }
        }
    }

    public void onTreeViewSelected() {
        TreeItem<Record> item = cardTreeView.getSelectionModel().getSelectedItem();

        if (item == null || item.getValue() instanceof Category) {
            recordViewPane.setCenter(null);
            cardContentTitleLabel.setText(null);
            cardContentTitleLabel.setGraphic(null);
        } else {
            setupRecordViewer(item);
        }
    }

    private void processEditedRecord(TreeItem<Record> item, Record r) {
        item.setValue(r);
        onTreeViewSelected();
        writeDocument();
    }

    public void onEditCard() throws Exception {
        TreeItem<Record> item = cardTreeView.getSelectionModel().getSelectedItem();

        if (item != null) {
            if (item.getValue() instanceof Card) {
                EditCardDialog d = new EditCardDialog((Card)item.getValue());

                d.showAndWait().ifPresent(result -> {
                    processEditedRecord(item, result);
                });
            } else {
                if (item.getValue() instanceof Note) {
                    EditNoteDialog d = new EditNoteDialog((Note)item.getValue());

                    d.showAndWait().ifPresent(result -> {
                        processEditedRecord(item, result);
                    });
                } else {
                    if (item.getValue() instanceof Category) {
                        CategoryDialog d = new CategoryDialog((Category)item.getValue());
                        d.showAndWait().ifPresent(result -> {
                            processEditedRecord(item, result);
                        });
                    }
                }

            }
        }
    }

    private ContextMenu createTreeViewContextMenu() {
        ContextMenu menu = new ContextMenu();
        menu.setOnShowing(e -> onCardTreeContextMenuShowing());

        MenuItem m1 = new MenuItem("New Card...");
        m1.setOnAction(a -> onNewCard());

        MenuItem m2 = new MenuItem("New Category...");
        m2.setOnAction(a -> onNewCategory());

        MenuItem m3 = new MenuItem("Delete...");
        m3.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        m3.setOnAction(a -> onDeleteRecord());

        MenuItem m4 = new MenuItem("New Note...");
        m4.setOnAction(a -> onNewNote());

        MenuItem m5 = new MenuItem("Cut");
        m5.setOnAction(a -> onCardCut());

        MenuItem m6 = new MenuItem("Copy");
        m6.setOnAction(a -> onCardCopy());

        cardPasteMenuItem = new MenuItem("Paste");
        cardPasteMenuItem.setOnAction(a -> onCardPaste());

        cardPasteLinkMenuItem = new MenuItem("Paste Link");
        cardPasteLinkMenuItem.setOnAction(a -> onCardPasteLink());

        menu.getItems().addAll(
            m2,
            new SeparatorMenuItem(),
            m1,
            m4,
            new SeparatorMenuItem(),
            m3,
            new SeparatorMenuItem(),
            m5,
            m6,
            cardPasteMenuItem,
            cardPasteLinkMenuItem
        );

        m1.disableProperty().bind(currentFile.isNull());
        m2.disableProperty().bind(currentFile.isNull());
        m3.disableProperty().bind(currentFile.isNull());
        m4.disableProperty().bind(currentFile.isNull());

        return menu;
    }

    public void onChangePassword() {
        PasswordDialog pwdDialog = new PasswordDialog(currentFile.get(), true);
        pwdDialog.showAndWait().ifPresent(password -> {
            currentPassword = password;
            writeDocument(currentFile.get());
        });
    }

    public void onAbout() {
        new AboutDialog().showAndWait();
    }

    public void onImport() {
        FileChooser d = new FileChooser();
        d.setTitle("Open eWallet Text File");
        d.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("eWallet Text Files", "*.txt")
        );
        File file = d.showOpenDialog(null);
        if (file != null) {
            TreeItem<Record> root = ImportExport.eWalletImport(file);
            if (root != null) {
                cardTreeView.setRoot(root);
                writeDocument();
            }
        }
    }

    public void onExport() {
        FileChooser d = new FileChooser();
        d.setTitle("Password Manager XML");
        d.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Password Manager XML Files", "*.xml")
        );
        File file = d.showSaveDialog(null);
        if (file != null) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                Serializer.serialize(out, cardTreeView.getRoot());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void onCardTreeContextMenuShowing() {
        boolean pasteEnable = false;

        Clipboard cb = Clipboard.getSystemClipboard();
        Optional<TreeItem<Record>> targetItem = getSelectedItem();

        if (cb.hasContent(Record.DATA_FORMAT) && targetItem.isPresent()) {
            String sourceId = (String)cb.getContent(Record.DATA_FORMAT);
            Optional<TreeItem<Record>> sourceItem = findRecordById(sourceId, cardTreeView.getRoot());

            if (sourceItem.isPresent()) {
                pasteEnable = checkForParentCategory(sourceItem.get(), targetItem.get());
            }
        }

        cardPasteMenuItem.setDisable(!pasteEnable);
        cardPasteLinkMenuItem.setDisable(!pasteEnable || cut);
    }

    private boolean checkForParentCategory(TreeItem item, TreeItem target) {
        // Leaf node, can be moved anywhere
        if (item.getChildren().isEmpty()) {
            return true;
        }

        TreeItem parent = target.getParent();
        while (parent != null) {
            if (parent == item) {
                return false;
            }
            parent = parent.getParent();
        }

        return true;
    }

    private void putCardToClipboard(Record record) {
        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.put(Record.DATA_FORMAT, record.getId());
        cb.setContent(content);
    }

    public void onCardCut() {
        getSelectedItem().ifPresent(item -> {
            putCardToClipboard(item.getValue());
            cut = true;
        });
    }

    public void onCardCopy() {
        getSelectedItem().ifPresent(item -> {
            putCardToClipboard(item.getValue());
            cut = false;
        });
    }

    private void genericPaste(Function<TreeItem<Record>, TreeItem<Record>> getNewItem) {
        getSelectedItem().ifPresent(targetItem -> {
            Clipboard cb = Clipboard.getSystemClipboard();
            String sourceId = (String)cb.getContent(Record.DATA_FORMAT);

            findRecordById(sourceId, cardTreeView.getRoot()).ifPresent(sourceItem -> {
                TreeItem<Record> newItem = getNewItem.apply(sourceItem);

                if (targetItem.getValue() instanceof Category) {
                    if (sourceItem != targetItem) {
                        if (cut) {
                            sourceItem.getParent().getChildren().remove(sourceItem);
                        }
                        targetItem.getChildren().add(newItem);
                        targetItem.setExpanded(true);
                    }
                } else {
                    if (cut) {
                        sourceItem.getParent().getChildren().remove(sourceItem);
                    }
                    TreeItem parentItem = targetItem.getParent();
                    int index = parentItem.getChildren().indexOf(targetItem);
                    parentItem.getChildren().add(index + 1, newItem);
                }

            });
        });
    }


    public void onCardPaste() {
        genericPaste(sourceItem -> cut ? sourceItem : new TreeItem<>(sourceItem.getValue().cloneWithNewId()));
    }

    public void onCardPasteLink() {
        genericPaste(sourceItem -> new TreeItem<>(new Link(sourceItem.getValue().getId())));
    }

    static Optional<TreeItem<Record>> findRecordById(String id, TreeItem<Record> item) {
        if (id == null) {
            return Optional.empty();
        }

        if (item.getValue().getId().equals(id)) {
            return Optional.of(item);
        } else {
            for (TreeItem<Record> child : item.getChildren()) {
                Optional<TreeItem<Record>> found = findRecordById(id, child);
                if (found.isPresent()) {
                    return found;
                }
            }
            return Optional.empty();
        }
    }

    private static void deleteBrokenLinks(String id, TreeItem<Record> root) {
        root.getChildren().stream()
            .filter(x -> !x.getChildren().isEmpty()).forEach(x -> deleteBrokenLinks(id, x));

        List<TreeItem> toDelete = root.getChildren().stream()
            .filter(x -> x.getValue() instanceof Link)
            .filter(x -> ((Link)x.getValue()).getTargetId().equals(id))
            .collect(Collectors.toList());

        toDelete.forEach(x -> {
            root.getChildren().remove(x);
        });
    }
}

final class FieldWrapper extends Field {
    private boolean show;

    FieldWrapper(Field that) {
        super(that);
    }

    void toggleShow() {
        show = !show;
    }

    boolean getShow() {
        return show;
    }
}

final class FieldValueCellImpl extends TableCell<FieldWrapper,FieldWrapper> {
    @Override
    protected void updateItem(FieldWrapper item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            textProperty().set(null);
        } else {
            switch (item.getType()) {
                case HIDDEN:
                    if (item.getShow() || item.getValue().isEmpty()) {
                        textProperty().set(item.getValue());
                    } else {
                        textProperty().set("***");
                    }
                    break;
                default:
                    textProperty().set(item.getValue());
                    break;
            }
        }
    }
}