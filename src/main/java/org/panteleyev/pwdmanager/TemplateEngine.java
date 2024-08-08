/*
 Copyright © 2021-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class TemplateEngine {
    public enum Template {
        MAIN_CSS("main.css"),
        DIALOG_CSS("dialog.css"),
        ABOUT_DIALOG_CSS("about-dialog.css");

        private final String fileName;

        Template(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

    private static final String TEMPLATE_PATH = "/templates/";
    private static final TemplateEngine ENGINE = new TemplateEngine();

    private TemplateEngine() {
    }

    public static TemplateEngine templateEngine() {
        return ENGINE;
    }

    public byte[] process(Template template, Map<String, ?> model) {
        try (var in = new BufferedReader(new InputStreamReader(
                requireNonNull(getClass().getResourceAsStream(TEMPLATE_PATH + template.getFileName())))
        ); var out = new ByteArrayOutputStream(); var writer = new OutputStreamWriter(out)) {
            while (in.ready()) {
                var string = in.readLine();
                for (var e : model.entrySet()) {
                    string = string.replace("${" + e.getKey() + "}", e.getValue().toString());
                }
                writer.append(string);
                writer.append('\n');
            }
            writer.flush();
            return out.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
