/*
 Copyright © 2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import org.panteleyev.fx.Controller;
import org.panteleyev.fx.StagePositionAndSize;
import org.panteleyev.fx.WindowManager;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.panteleyev.pwdmanager.XMLUtils.appendElement;
import static org.panteleyev.pwdmanager.XMLUtils.createDocument;
import static org.panteleyev.pwdmanager.XMLUtils.getAttribute;
import static org.panteleyev.pwdmanager.XMLUtils.readDocument;
import static org.panteleyev.pwdmanager.XMLUtils.writeDocument;

final class WindowsSettings {
    private static final double DEFAULT_WIDTH = 1024.0;
    private static final double DEFAULT_HEIGHT = 768.0;

    private static final String ROOT_ELEMENT = "windows";
    private static final String WINDOW_ELEMENT = "window";
    private static final String CLASS_ATTR = "class";
    private static final String X_ATTR = "x";
    private static final String Y_ATTR = "y";
    private static final String WIDTH_ATTR = "width";
    private static final String HEIGHT_ATTR = "height";
    private static final String MAXIMIZED_ATTR = "maximized";

    private final Map<String, StagePositionAndSize> windowMap = new ConcurrentHashMap<>();

    void storeWindowDimensions(Controller controller) {
        windowMap.put(controller.getClass().getSimpleName(), controller.getStagePositionAndSize());
    }

    void restoreWindowDimensions(Controller controller) {
        controller.setStagePositionAndSize(
                windowMap.get(controller.getClass().getSimpleName())
        );
    }

    void save(OutputStream out) {
        WindowManager.newInstance().getControllerStream().forEach(this::storeWindowDimensions);

        var root = createDocument(ROOT_ELEMENT);
        for (var entry : windowMap.entrySet()) {
            var w = appendElement(root, WINDOW_ELEMENT);
            w.setAttribute(CLASS_ATTR, entry.getKey());
            w.setAttribute(X_ATTR, Double.toString(entry.getValue().x()));
            w.setAttribute(Y_ATTR, Double.toString(entry.getValue().y()));
            w.setAttribute(WIDTH_ATTR, Double.toString(entry.getValue().width()));
            w.setAttribute(HEIGHT_ATTR, Double.toString(entry.getValue().height()));
            w.setAttribute(MAXIMIZED_ATTR, Boolean.toString(entry.getValue().maximized()));
        }

        writeDocument(root.getOwnerDocument(), out);
    }

    void load(InputStream in) {
        windowMap.clear();
        var root = readDocument(in);
        var windowNodes = root.getElementsByTagName(WINDOW_ELEMENT);
        for (int i = 0; i < windowNodes.getLength(); i++) {
            if (windowNodes.item(i) instanceof Element windowElement) {
                var className = windowElement.getAttribute(CLASS_ATTR);
                var x = getAttribute(windowElement, X_ATTR, 0.0);
                var y = getAttribute(windowElement, Y_ATTR, 0.0);
                var width = getAttribute(windowElement, WIDTH_ATTR, DEFAULT_WIDTH);
                var height = getAttribute(windowElement, HEIGHT_ATTR, DEFAULT_HEIGHT);
                var maximized = getAttribute(windowElement, MAXIMIZED_ATTR, false);
                windowMap.put(className, new StagePositionAndSize(
                        x, y, width, height, maximized
                ));
            }
        }
    }
}
