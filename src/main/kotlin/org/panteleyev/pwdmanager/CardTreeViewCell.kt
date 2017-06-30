/*
 * Copyright (c) 2017, Petr Panteleyev <petr@panteleyev.org>
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

package org.panteleyev.pwdmanager

import javafx.application.Platform
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import java.util.Timer
import java.util.TimerTask

internal class CardTreeViewCell(mainWindow: MainWindowController) : TreeCell<Record>() {
    internal enum class DnDDropPosition {
        ABOVE,
        BELOW,
        INTO
    }

    private var expansionTimer: Timer? = null

    private fun cancelExpansionTimer() {
        if (expansionTimer != null) {
            expansionTimer!!.cancel()
            expansionTimer = null
        }
    }

    private fun setupAsDropInto() {
        styleClass.add(MainWindowController.CSS_DROP_TARGET)
        dropPosition = DnDDropPosition.INTO

        if (!treeItem.children.isEmpty() && expansionTimer == null) {
            expansionTimer = Timer()
            expansionTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    Platform.runLater { treeItem.setExpanded(!treeItem.isExpanded) }
                }
            }, CELL_EXPANSION_PERIOD, CELL_EXPANSION_PERIOD)
        }
    }

    private fun getSourceTreeItem(event: DragEvent): TreeItem<Record>? {
        val src = event.gestureSource
        return if (src is CardTreeViewCell)
            src.treeItem
        else
            null
    }

    init {
        setOnDragDetected { event: MouseEvent ->
            // Prohibit DND if tree shows search result
            if (mainWindow.searchTextProperty().get().isEmpty()) {
                item?.let { record ->
                    val db = if (record is Category || record is Link)
                        startDragAndDrop(TransferMode.MOVE)
                    else
                        startDragAndDrop(*TransferMode.ANY)
                    db.dragView = snapshot(null, null)
                    val cnt = ClipboardContent()
                    cnt.put(Record.DATA_FORMAT, record.id)
                    db.setContent(cnt)
                    treeView.selectionModel.select(null)
                }
            }
            event.consume()
        }

        setOnDragExited {
            styleClass.removeAll(*MainWindowController.CSS_DND_STYLES)
            cancelExpansionTimer()
            it.consume()
        }

        setOnDragOver { event: DragEvent ->
            getSourceTreeItem(event)?.let { sourceTreeItem ->
                treeItem?.let { targetItem ->
                    if (!sourceTreeItem.children.isEmpty()) {
                        // source is a folder, check that we don't move it down to its subtree
                        var parent: TreeItem<*>? = targetItem.parent
                        while (parent != null) {
                            if (parent === sourceTreeItem) {
                                event.consume()
                                return@setOnDragOver
                            }
                            parent = parent.parent
                        }
                    }

                    item?.let { record ->
                        val sceneCoordinates = localToScene(0.0, 0.0)
                        val height = height
                        val y = event.sceneY - sceneCoordinates.y

                        styleClass.removeAll(*MainWindowController.CSS_DND_STYLES)

                        if (record is Category) {
                            if (y > height * .75) {
                                if (!targetItem.children.isEmpty() && targetItem.isExpanded) {
                                    setupAsDropInto()
                                } else {
                                    styleClass.add(MainWindowController.CSS_DROP_BELOW)
                                    dropPosition = DnDDropPosition.BELOW
                                    cancelExpansionTimer()
                                }
                            } else {
                                if (y < height * .25f) {
                                    styleClass.add(MainWindowController.CSS_DROP_ABOVE)
                                    dropPosition = DnDDropPosition.ABOVE
                                    cancelExpansionTimer()
                                } else {
                                    setupAsDropInto()
                                }
                            }
                        } else {
                            if (y > height * .5) {
                                styleClass.add(MainWindowController.CSS_DROP_BELOW)
                                dropPosition = DnDDropPosition.BELOW
                            } else {
                                styleClass.add(MainWindowController.CSS_DROP_ABOVE)
                                dropPosition = DnDDropPosition.ABOVE
                            }
                        }

                        if (dropPosition != DnDDropPosition.INTO || record is Category) {
                            event.acceptTransferModes(*TransferMode.ANY)
                        }
                    }
                }
            }
            event.consume()
        }

        setOnDragDropped { event: DragEvent ->
            getSourceTreeItem(event)?.let { sourceTreeItem ->
                treeItem?.let { targetItem ->
                    val sourceRecord = sourceTreeItem.value

                    if (targetItem === sourceTreeItem && event.transferMode == TransferMode.MOVE) {
                        treeView.selectionModel.select(targetItem)
                    } else {
                        val newItem = when (event.transferMode) {
                            TransferMode.COPY -> TreeItem(sourceRecord.cloneWithNewId())
                            TransferMode.MOVE -> sourceTreeItem
                            TransferMode.LINK -> TreeItem<Record>(Link(targetId = sourceRecord.id))
                            else -> throw IllegalStateException("Transfer mode is not valid")
                        }

                        if (event.transferMode == TransferMode.MOVE) {
                            sourceTreeItem.parent.children.remove(sourceTreeItem)
                        }

                        if (dropPosition == DnDDropPosition.INTO) {
                            targetItem.children.add(newItem)
                            targetItem.setExpanded(true)
                        } else {
                            val parentItem = targetItem.parent
                            var index = parentItem.children.indexOf(targetItem)
                            if (dropPosition == DnDDropPosition.BELOW) {
                                index++
                            }
                            parentItem.children.add(index, newItem)
                        }

                        treeView.selectionModel.select(newItem)
                        event.isDropCompleted = true
                        mainWindow.writeDocument()
                    }
                }
            }

            event.consume()
        }
    }

    override fun updateItem(item: Record?, empty: Boolean) {
        super.updateItem(item, empty)

        var text: String? = null
        var iv: ImageView? = null

        if (!empty && item != null) {
            iv = ImageView()

            if (item is Link) {
                val targetItem = MainWindowController.findRecordById(item.targetId, treeView.root)
                if (targetItem != null) {
                    text = targetItem.value.name + " - shortcut"
                    iv.image = targetItem.value.picture.image
                }
            } else {
                text = item.name
                iv.image = item.picture.image
            }
        }

        setText(text)
        graphic = iv
    }

    companion object {
        private val CELL_EXPANSION_PERIOD = 2000L

        private var dropPosition: DnDDropPosition? = null
    }
}
