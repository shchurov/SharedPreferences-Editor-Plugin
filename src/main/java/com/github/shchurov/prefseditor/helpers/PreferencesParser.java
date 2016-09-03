package com.github.shchurov.prefseditor.helpers;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreferencesParser {

    public Map<String, Object> parse(String filePath) throws ParseException {
        Document document = buildDocument(filePath);
        return buildPreferencesMap(document.getDocumentElement());
    }

    private Document buildDocument(String filePath) throws ParseException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(new File(filePath));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new ParseException(e);
        }
    }

    private Map<String, Object> buildPreferencesMap(Element rootElement) throws ParseException {
        Map<String, Object> map = new HashMap<>();
        NodeList nodes = rootElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) node;
            String name = element.getAttribute("name");
            Object value = extractValue(element);
            map.put(name, value);
        }
        return map;
    }

    private Object extractValue(Element element) throws ParseException {
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
                throw new ParseException("Unknown type: " + type);
        }
    }

    private String extractStringValue(Node node) {
        return node.getChildNodes().item(0).getNodeValue();
    }

    private Set<String> extractStringSetValue(Node rootNode) {
        Set<String> set = new HashSet<>();
        NodeList nodes = rootNode.getChildNodes();
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

    public static class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }

        ParseException(Throwable cause) {
            super(cause);
        }
    }

}
