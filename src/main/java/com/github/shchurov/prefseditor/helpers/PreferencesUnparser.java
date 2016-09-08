package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.exceptions.UnparsePreferencesException;
import com.github.shchurov.prefseditor.model.Preference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.Set;

public class PreferencesUnparser {

    public void unparse(List<Preference> preferences, String filePath) throws UnparsePreferencesException {
        Document document = createDocument();
        putPreferences(preferences, document);
        saveDocument(document, filePath);
    }

    private Document createDocument() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new UnparsePreferencesException(e);
        }
    }

    private void putPreferences(List<Preference> preferences, Document document) {
        Element rootElement = document.createElement("map");
        document.appendChild(rootElement);
        for (Preference p : preferences) {
            String tag = getTag(p.getType());
            Element element = document.createElement(tag);
            element.setAttribute("name", p.getKey());
            putValue(p, element, document);
            rootElement.appendChild(element);
        }
    }

    private String getTag(Preference.Type type) {
        switch (type) {
            case BOOLEAN:
                return "boolean";
            case FLOAT:
                return "float";
            case INTEGER:
                return "int";
            case LONG:
                return "long";
            case STRING:
                return "string";
            case STRING_SET:
                return "set";
            default:
                throw new UnparsePreferencesException(new IllegalStateException(type.toString()));
        }
    }

    @SuppressWarnings("unchecked")
    private void putValue(Preference preference, Element element, Document document) {
        switch (preference.getType()) {
            case STRING:
                putStringValue(preference.getValue().toString(), element, document);
                break;
            case STRING_SET:
                putStringSetValue((Set<String>) preference.getValue(), element, document);
                break;
            case BOOLEAN:
            case FLOAT:
            case INTEGER:
            case LONG:
                element.setAttribute("value", preference.getValue().toString());
                break;
        }
    }

    private void putStringValue(String value, Element element, Document document) {
        element.appendChild(document.createTextNode(value));
    }

    private void putStringSetValue(Set<String> value, Element parentElement, Document document) {
        for (String s : value) {
            Element element = document.createElement("string");
            element.appendChild(document.createTextNode(s));
            parentElement.appendChild(element);
        }
    }

    private void saveDocument(Document document, String filePath) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Source src = new DOMSource(document);
            Result dst = new StreamResult(new File(filePath));
            transformer.transform(src, dst);
        } catch (TransformerException e) {
            throw new UnparsePreferencesException(e);
        }
    }

}
