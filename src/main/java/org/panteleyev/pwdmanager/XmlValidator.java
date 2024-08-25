/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.commons.xml.XMLEventReaderWrapper;

import javax.xml.XMLConstants;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;

public final class XmlValidator {
    private static final String SCHEMA_URL = "/xsd/password-manager.xsd";
    private static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    public static void validate(InputStream inputStream) {
        try (var wrapper = XMLEventReaderWrapper.newInstance(inputStream)) {
            var schema = SCHEMA_FACTORY.newSchema(XmlValidator.class.getResource(SCHEMA_URL));
            var validator = schema.newValidator();
            validator.validate(new StAXSource(wrapper.getReader()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
