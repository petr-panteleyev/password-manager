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

import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

enum DnDDropPosition {
    ABOVE,
    BELOW,
    INTO
}

class CardTreeViewCell extends TreeCell<Record> {
    private static final long CELL_EXPANSION_PERIOD = 2000L;

    private static DnDDropPosition dropPosition = null;
    private static TreeItem<Record> sourceItem = null;

    private Timer expansionTimer = null;

    private void cancelExpansionTimer() {
        if (expansionTimer != null) {
            expansionTimer.cancel();
            expansionTimer = null;
        }
    }

    private void setupAsDropInto() {
        getStyleClass().add(MainWindowController.CSS_DROP_TARGET);
        dropPosition = DnDDropPosition.INTO;

        if (!getTreeItem().getChildren().isEmpty() && expansionTimer == null) {
            expansionTimer = new Timer();
            expansionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> getTreeItem().setExpanded(!getTreeItem().isExpanded()));
                }
            }, CELL_EXPANSION_PERIOD, CELL_EXPANSION_PERIOD);
        }
    }

    public CardTreeViewCell(MainWindowController mainWindow) {
        setOnDragDetected((MouseEvent event) -> {
            Record record = getItem();
            if (record != null) {
                Dragboard db = (record instanceof Category || record instanceof Link)?
                    startDragAndDrop(TransferMode.MOVE) : startDragAndDrop(TransferMode.ANY);
                db.setDragView(snapshot(null, null));
                ClipboardContent cnt = new ClipboardContent();
                cnt.put(Record.DATA_FORMAT, record.getId());
                db.setContent(cnt);
                sourceItem = getTreeItem();
                getTreeView().getSelectionModel().select(null);
            }
            event.consume();
        });

        setOnDragExited((DragEvent event) -> {
            getStyleClass().removeAll(MainWindowController.CSS_DND_STYLES);
            cancelExpansionTimer();
            event.consume();
        });

        setOnDragOver((DragEvent event) -> {
            TreeItem<Record> targetItem = getTreeItem();

            if (targetItem != null && !sourceItem.getChildren().isEmpty()) {
                // source is a folder, check that we don't move it down to its subtree
                TreeItem parent = targetItem.getParent();
                while (parent != null) {
                    if (parent == sourceItem) {
                        event.consume();
                        return;
                    }
                    parent = parent.getParent();
                }
            }

            Record record = getItem();
            if (record != null && targetItem != null) {
                Dragboard db = event.getDragboard();
                if (db.hasContent(Record.DATA_FORMAT)) {
                    Point2D sceneCoordinates = localToScene(0d, 0d);
                    double height = getHeight();
                    double y = event.getSceneY() - (sceneCoordinates.getY());

                    getStyleClass().removeAll(MainWindowController.CSS_DND_STYLES);

                    if (record instanceof Category) {
                        if (y > (height * .75d)) {
                            if (!targetItem.getChildren().isEmpty() && targetItem.isExpanded()) {
                                setupAsDropInto();
                            } else {
                                getStyleClass().add(MainWindowController.CSS_DROP_BELOW);
                                dropPosition = DnDDropPosition.BELOW;
                                cancelExpansionTimer();
                            }
                        } else {
                            if (y < (height * .25f)) {
                                getStyleClass().add(MainWindowController.CSS_DROP_ABOVE);
                                dropPosition = DnDDropPosition.ABOVE;
                                cancelExpansionTimer();
                            } else {
                                setupAsDropInto();
                            }
                        }
                    } else {
                        if (y > (height * .5d)) {
                            getStyleClass().add(MainWindowController.CSS_DROP_BELOW);
                            dropPosition = DnDDropPosition.BELOW;
                        } else {
                            getStyleClass().add(MainWindowController.CSS_DROP_ABOVE);
                            dropPosition = DnDDropPosition.ABOVE;
                        }
                    }

                    if (dropPosition != DnDDropPosition.INTO || record instanceof Category) {
                        event.acceptTransferModes(TransferMode.ANY);
                    }
                }
            }
            event.consume();
        });

        setOnDragDropped((DragEvent event) -> {
            TreeItem<Record> targetItem = getTreeItem();
            if (targetItem != null && sourceItem != null) {
                Record sourceRecord = sourceItem.getValue();
                Record targetRecord = targetItem.getValue();

                if (sourceRecord != null && targetRecord != null) {
                    if (targetItem == sourceItem && event.getTransferMode() == TransferMode.MOVE) {
                        getTreeView().getSelectionModel().select(targetItem);
                    } else {
                        TreeItem<Record> newItem = null;
                        switch (event.getTransferMode()) {
                            case COPY:
                                newItem = new TreeItem<>(sourceRecord.cloneWithNewId());
                                break;
                            case MOVE:
                                newItem = sourceItem;
                                break;
                            case LINK:
                                newItem = new TreeItem<>(new Link(sourceRecord.getId()));
                                break;
                        }

                        Objects.requireNonNull(newItem);

                        if (event.getTransferMode() == TransferMode.MOVE) {
                            sourceItem.getParent().getChildren().remove(sourceItem);
                        }

                        if (dropPosition == DnDDropPosition.INTO) {
                            targetItem.getChildren().add(newItem);
                            targetItem.setExpanded(true);
                        } else {
                            TreeItem<Record> parentItem = targetItem.getParent();
                            int index = parentItem.getChildren().indexOf(targetItem);
                            if (dropPosition == DnDDropPosition.BELOW) {
                                index++;
                            }
                            parentItem.getChildren().add(index, newItem);
                        }

                        getTreeView().getSelectionModel().select(newItem);
                        event.setDropCompleted(true);
                        mainWindow.writeDocument();
                    }
                }
            }
            event.consume();
        });
    }

    @Override
    protected void updateItem(Record item, boolean empty) {
        super.updateItem(item, empty);

        String text = null;
        ImageView iv = null;

        if (!empty && item != null) {
            iv = new ImageView();

            if (item instanceof Link) {
                Optional<TreeItem<Record>> targetItem = MainWindowController.findRecordById(((Link)item).getTargetId(), getTreeView().getRoot());
                if (targetItem.isPresent()) {
                    text = targetItem.get().getValue().getName() + " - shortcut";
                    iv.setImage(targetItem.get().getValue().getPicture().getImage());
                }
            } else {
                text = item.getName();
                iv.setImage(item.getPicture().getImage());
                if (item instanceof Category) {
                    ((Category)item).expandedProperty().bind(getTreeItem().expandedProperty());
                }
            }
        }

        setText(text);
        setGraphic(iv);
    }
}
