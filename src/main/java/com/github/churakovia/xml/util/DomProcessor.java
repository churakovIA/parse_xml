package com.github.churakovia.xml.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomProcessor {

  private static final DocumentBuilderFactory DOCUMENT_FACTORY = DocumentBuilderFactory
      .newInstance();
  private static final DocumentBuilder DOCUMENT_BUILDER;

  static {
    DOCUMENT_FACTORY.setNamespaceAware(true);
    try {
      DOCUMENT_BUILDER = DOCUMENT_FACTORY.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException(e);
    }
  }

  private final Document doc;

  public DomProcessor(InputStream is) {
    try {
      this.doc = DOCUMENT_BUILDER.parse(is);
    } catch (SAXException | IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public Document getDoc() {
    return doc;
  }

  /**
   * Находит в документе все элементы с имененем tagName и аттрибутами map
   *
   * @param tagName - имя тега элемента
   * @param map     - мапа для поиска по аттрибутам. Ключ - имя аттрибута, значение - значение
   *                аттрибута
   * @param <T>     - тип значения аттрибута
   * @return - возвращает список найденных элементов
   */
  public <T> List<Node> getElements(String tagName, Map<String, T> map) {
    return stream(doc.getElementsByTagName(tagName))
        .filter(item -> item.hasAttributes() && allMatch(item.getAttributes(), map))
        .collect(Collectors.toList());
  }

  public <T, C extends Collection<T>> C processAttributes(Stream<Node> nodeStream,
      Function<NamedNodeMap, T> mapper, Supplier<C> collectionFactory) {

    return nodeStream
        .filter(Node::hasAttributes)
        .map(Node::getAttributes)
        .map(mapper)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(collectionFactory));

  }

  static <T> boolean allMatch(NamedNodeMap attributes, Map<String, T> map) {

    return map.entrySet().stream().allMatch(entry -> {
          T entryValue = entry.getValue();
          Node namedItem = attributes.getNamedItem(entry.getKey());
          return entryValue != null && namedItem != null && entryValue.equals(namedItem.getNodeValue());
        }
    );
  }

  static Stream<Node> stream(NodeList nodeList) {
    return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item);
  }

  static Stream<Node> stream(NamedNodeMap namedNodeMap) {
    return IntStream.range(0, namedNodeMap.getLength()).mapToObj(namedNodeMap::item);
  }
}
