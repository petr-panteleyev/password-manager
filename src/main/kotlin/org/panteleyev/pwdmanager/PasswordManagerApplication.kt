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

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import java.util.ResourceBundle

class PasswordManagerApplication : Application() {

    private val rb = ResourceBundle.getBundle(UI_BUNDLE_PATH)

    override fun start(stage: Stage) {
        application = this

        Thread.currentThread().setUncaughtExceptionHandler { _: Thread, e: Throwable ->
            var throwable = e
            throwable.printStackTrace()
            while (throwable.cause != null) {
                throwable = throwable.cause as Throwable
            }
            val alert = Alert(Alert.AlertType.ERROR, throwable.message, ButtonType.OK)
            alert.showAndWait()
        }

        with (stage) {
            title = rb.getString("mainWindow.title")

            scene = Scene(MainWindowController()).apply {
                stylesheets.add(MainWindowController.CSS_PATH)
            }

            height = 542.0
            width = 800.0
            show()
        }
    }

    companion object {
        private val UI_BUNDLE_PATH = "ui"

        internal var application: PasswordManagerApplication? = null
            private set

        internal val bundle: ResourceBundle
            get() = application!!.rb
    }
}

fun main(args: Array<String>) {
    Application.launch(*args)
}
