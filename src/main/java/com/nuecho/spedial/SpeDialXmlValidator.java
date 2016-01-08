/*
 * Copyright (c) 2015 Nu Echo Inc.
 * 
 * This is free software. For terms and warranty disclaimer, see license.md
 */

package com.nuecho.spedial;

import static java.lang.String.*;

import java.io.*;
import java.util.*;

import javax.xml.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.xml.sax.*;

public final class SpeDialXmlValidator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar spdxml-validator.jar XML_FILE XSD_FILE");
            return;
        }

        String xmlPath = args[0];
        String xsdPath = args[1];

        Source xmlSource = null;
        File xmlFile = new File(xmlPath);

        if (!xmlFile.exists()) {
            System.err.println(format("Couldn't find XML file : '%s'", xmlPath));
            return;
        }

        if (!xmlFile.isFile()) {
            System.err.println(format("XML location is not a valid file : '%s'", xmlPath));
            return;
        }

        try {
            xmlSource = new StreamSource(xmlFile);
        } catch (Exception exception) {
            System.err.println(format("Couldn't open XML file : '%s'", xmlPath));
            return;
        }

        File schemaFile = null;
        schemaFile = new File(xsdPath);
        if (!schemaFile.exists()) {
            System.err.println(format("Couldn't find XSD file : '%s'", xsdPath));
            return;
        } 

        if (!schemaFile.isFile()) {
            System.err.println(format("XSD location is not a valid file : '%s'", xsdPath));
            return;
        }

        final List<SAXParseException> exceptions = new ArrayList<SAXParseException>();
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    exceptions.add(exception);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    exceptions.add(exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    exceptions.add(exception);
                }
            });

            try {
                validator.validate(xmlSource);
            } catch (SAXException exception) {
                System.err.println(xmlSource.getSystemId() + " is NOT valid");
                System.err.println("Reason: " + exception.getLocalizedMessage());
                return;
            } catch (IOException exception) {
                System.err.println(exception.getLocalizedMessage());
                return;
            }
        } catch (SAXException exception) {
            System.err.println("Invalid XSD File Structure");
            return;
        }

        for (SAXParseException exception : exceptions) {
            System.err.println("Line Number " + exception.getLineNumber() + " " + exception.getLocalizedMessage());
        }

        System.out.println(exceptions.size() + " error(s) found");
    }
}
