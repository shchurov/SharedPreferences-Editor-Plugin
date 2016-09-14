package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.exceptions.ParsePreferencesException;
import com.github.shchurov.prefseditor.model.Preference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PreferencesParser {

    public List<Preference> parse(String filePath) throws ParsePreferencesException {
        try {
            Document document = buildDocument(filePath);
            return extractPreferences(document);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new ParsePreferencesException(e);
        }
    }

    private Document buildDocument(String filePath) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(new File(filePath));
    }

    private List<Preference> extractPreferences(Document document) {
        List<Preference> preferences = new ArrayList<>();
        NodeList nodes = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) node;
            String key = element.getAttribute("name");
            Object value = extractValue(element);
            preferences.add(new Preference(key, value));
        }
        return preferences;
    }

    private Object extractValue(Element element) {
        String type = element.getTagName();
        if ("string".equals(type)) {
            return extractStringValue(element);
        } else if ("set".equals(type)) {
            return extractStringSetValue(element);
        }
        String value = element.getAttribute("value");
        switch (type) {
            case "boolean":
                return Boolean.parseBoolean(value);
            case "int":
                return Integer.parseInt(value);
            case "long":
                return Long.parseLong(value);
            case "float":
                return Float.parseFloat(value);
            default:
                throw new ParsePreferencesException(new IllegalArgumentException(type));
        }
    }

    private String extractStringValue(Node node) {
        return node.getChildNodes().item(0).getNodeValue();
    }

    private Set<String> extractStringSetValue(Node parentNode) {
        Set<String> set = new TreeSet<>();
        NodeList nodes = parentNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String value = extractStringValue(nodes.item(i));
            set.add(value);
        }
        return set;
    }

}
