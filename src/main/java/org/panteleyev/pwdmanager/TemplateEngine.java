/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Map;

public final class TemplateEngine {
    public enum Template {
        MAIN_CSS("main.css"),
        DIALOG_CSS("dialog.css");

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

    public void process(Template template, Map<String, ?> model, Writer out) {
        try (var in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(TEMPLATE_PATH + template.getFileName())))) {
            while (in.ready()) {
                var string = in.readLine();
                for (var e : model.entrySet()) {
                    string = string.replace("${" + e.getKey() + "}", e.getValue().toString());
                }
                out.append(string);
                out.append('\n');
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
