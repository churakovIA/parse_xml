package com.github.churakovia.xml.upload;

import com.github.churakovia.xml.util.StaxStreamProcessor;
import com.google.common.collect.ImmutableMap;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class PayloadProcessor {


  public static Set<String> parseDocTypes(InputStream is, Comparator<String> comparator)
      throws XMLStreamException {

    Set<String> docTypes = new TreeSet<>(comparator);

    try (StaxStreamProcessor processor = new StaxStreamProcessor(is)) {
      while (processor.startElement("par", ImmutableMap.of("name", "ВИД_ДОК"))) {
        while (processor.startElement("par_list", "par")) {
          docTypes.add(processor.getAttribute("value"));
        }
      }
    }
    return docTypes;
  }

  public static Map<String, String> getAttributes(InputStream is) throws Exception {

    String elementName = "par";
    Map<String, String> filter = ImmutableMap.of("step", "1", "name", "ГРАЖДАНСТВО");

    Map<String, String> attributes = new HashMap<>();

    try (StaxStreamProcessor processor = new StaxStreamProcessor(is)) {
      XMLStreamReader reader = processor.getReader();
      while (processor.startElement(elementName, filter)) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
          attributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
        }
      }
    }
    return attributes;
  }

  public static void uploadDocTypes(InputStream is) {

  }
}
