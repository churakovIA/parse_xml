package com.github.churakovia.xml.util;

import java.io.InputStream;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class StaxStreamProcessor implements AutoCloseable {

  private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

  private final XMLStreamReader reader;

  public StaxStreamProcessor(InputStream is) throws XMLStreamException {
    reader = FACTORY.createXMLStreamReader(is);
  }

  public boolean startElement(String element, String parent) throws XMLStreamException {
    while (reader.hasNext()) {
      int event = reader.next();
      if (parent != null && isElementEnd(event, parent)) {
        return false;
      }
      if (isElementStart(event, element)) {
        return true;
      }
    }
    return false;
  }

  private boolean isElementStart(int event, String el) {
    return event == XMLEvent.START_ELEMENT && el.equals(reader.getLocalName());
  }

  private boolean isElementEnd(int event, String el) {
    return event == XMLEvent.END_ELEMENT && el.equals(reader.getLocalName());
  }

  public XMLStreamReader getReader() {
    return reader;
  }

  public String getAttribute(String name) {
    return reader.getAttributeValue(null, name);
  }

  public boolean startElement(String value, Map<String, String> attr) throws XMLStreamException {
    while (reader.hasNext()) {
      int event = reader.next();
      if (event == XMLEvent.START_ELEMENT && value.equals(getValue(event))
          && attr.entrySet().stream()
          .allMatch(e -> e.getValue().equals(getAttribute(e.getKey())))) {
        return true;
      }
    }
    return false;
  }

  public String getValue(int event) {
    return (event == XMLEvent.CHARACTERS) ? reader.getText() : reader.getLocalName();
  }

  @Override
  public void close() {
    if (reader != null) {
      try {
        reader.close();
      } catch (XMLStreamException e) {
        // empty
      }
    }
  }
}
