package com.github.churakovia.xml.upload;

import com.github.churakovia.xml.util.StaxStreamProcessor;
import com.github.churakovia.xml.util.XPathProcessor;
import com.google.common.collect.ImmutableMap;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

  public static Set<String> parseDocTypesByXPath(InputStream is) {
    XPathProcessor processor = XPathProcessor.createDOM(is);
    XPathExpression expression = XPathProcessor
        .getExpression("/order/services/serv/pars/par[@name='ВИД_ДОК']/par_list/@value");
    NodeList nodes = processor.evaluate(expression, XPathConstants.NODESET);

    return IntStream.range(0, nodes.getLength()).mapToObj(i -> nodes.item(i).getNodeValue())
        .collect(Collectors.toCollection(TreeSet::new));
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

  public static Map<String, String> getAttributesByXPath(InputStream is) {
    XPathProcessor processor = XPathProcessor.createDOM(is);
    XPathExpression expression = XPathProcessor
        .getExpression("/order/services/serv/pars/par[@step='1'][@name='ВИД_ДОК']/@*");
    NodeList nodes = processor.evaluate(expression, XPathConstants.NODESET);

    return IntStream.range(0, nodes.getLength()).mapToObj(nodes::item)
        .collect(Collectors.toMap(Node::getNodeName, Node::getNodeValue));
  }
}
