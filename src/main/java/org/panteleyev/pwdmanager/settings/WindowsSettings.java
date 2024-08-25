/*
 Copyright © 2022-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import org.panteleyev.commons.xml.XMLEventReaderWrapper;
import org.panteleyev.commons.xml.XMLStreamWriterWrapper;
import org.panteleyev.fx.Controller;
import org.panteleyev.fx.StagePositionAndSize;
import org.panteleyev.fx.WindowManager;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class WindowsSettings {
    private static final double DEFAULT_WIDTH = 1024.0;
    private static final double DEFAULT_HEIGHT = 768.0;

    private static final QName ROOT_ELEMENT = new QName("windows");
    private static final QName WINDOW_ELEMENT = new QName("window");
    private static final QName CLASS_ATTR = new QName("class");
    private static final QName X_ATTR = new QName("x");
    private static final QName Y_ATTR = new QName("y");
    private static final QName WIDTH_ATTR = new QName("width");
    private static final QName HEIGHT_ATTR = new QName("height");
    private static final QName MAXIMIZED_ATTR = new QName("maximized");

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

        try (var w = XMLStreamWriterWrapper.newInstance(out)) {
            w.document(ROOT_ELEMENT, () -> {
                for (var entry : windowMap.entrySet()) {
                    var positionAndSize = entry.getValue();

                    w.element(WINDOW_ELEMENT, Map.of(
                            CLASS_ATTR, entry.getKey(),
                            X_ATTR, positionAndSize.x(),
                            Y_ATTR, positionAndSize.y(),
                            WIDTH_ATTR, positionAndSize.width(),
                            HEIGHT_ATTR, positionAndSize.height(),
                            MAXIMIZED_ATTR, positionAndSize.maximized()
                    ));
                }
            });
        }
    }

    void load(InputStream in) {
        windowMap.clear();

        try (var reader = XMLEventReaderWrapper.newInstance(in)) {
            while (reader.hasNext()) {
                var event = reader.nextEvent();
                event.ifStartElement(WINDOW_ELEMENT, element -> {
                    var className = element.getAttributeValue(CLASS_ATTR).orElseThrow();
                    windowMap.put(className, new StagePositionAndSize(
                            element.getAttributeValue(X_ATTR, 0.0),
                            element.getAttributeValue(Y_ATTR, 0.0),
                            element.getAttributeValue(WIDTH_ATTR, DEFAULT_WIDTH),
                            element.getAttributeValue(HEIGHT_ATTR, DEFAULT_HEIGHT),
                            element.getAttributeValue(MAXIMIZED_ATTR, false)
                    ));
                });
            }
        }
    }
}
