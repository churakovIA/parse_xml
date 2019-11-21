package com.github.churakovia.xml.util;

import static com.github.churakovia.xml.util.DomProcessor.allMatch;
import static com.github.churakovia.xml.util.DomProcessor.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomProcessorTest {

  DomProcessor processor;

  @BeforeEach
  void init() throws Exception {
    try (InputStream is = Resources.getResource("payload.xml").openStream()) {
      processor = new DomProcessor(is);
    }
  }

  @Test
  void buildDom() throws Exception {

    //given

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document;
    try (InputStream is = Resources.getResource("payload.xml").openStream()) {
      document = builder.parse(is);
    }

    //verify

    assertNotNull(document);
    assertEquals(1, document.getChildNodes().getLength());
    assertEquals("order", document.getDocumentElement().getTagName());
    assertEquals(5, document.getChildNodes().item(0).getChildNodes().getLength());

  }

  @Test
  void getElements() {

    //given

    List<Node> elements = processor
        .getElements("par", ImmutableMap.of("step", "1", "name", "ВИД_ДОК"));

    //verify

    assertNotNull(elements);
    assertEquals(1, elements.size());

  }

  @Test
  void getDocTypes() {

    //given

    NodeList elements = ((Element) processor
        .getElements("par", ImmutableMap.of("step", "1", "name", "ВИД_ДОК")).get(0))
        .getElementsByTagName("par_list");

    Function<NamedNodeMap, String> mapper = map -> {
      Node value = map.getNamedItem("value");
      return value != null ? value.getNodeValue() : null;
    };

    Set<String> docTypes = processor.processAttributes(stream(elements), mapper, TreeSet::new);

    //verify

    assertNotNull(docTypes);
    assertEquals(9, docTypes.size());

    System.out.println("\nВиды документов (DOM):\n");
    docTypes.forEach(System.out::println);
  }

  @Test
  void getAttributes() {

    List<Node> elements = processor
        .getElements("par", ImmutableMap.of("step", "1", "name", "ГРАЖДАНСТВО"));
    Map<String, String> attributes = processor
        .processAttributes(elements.stream(), DomProcessor::stream, ArrayList::new).stream()
        .flatMap(Function.identity())
        .collect(Collectors.toMap(Node::getNodeName, Node::getNodeValue));

    //verify

    assertNotNull(attributes);
    assertEquals(17, attributes.size());

    System.out.println("\nАтрибуты для par step=\"1\" name=\"ГРАЖДАНСТВО\" (DOM):\n");
    attributes.forEach((key, value) -> System.out.printf("%s:%s\n", key, value));
  }

  @Test
  void allMatchTest() {
    NamedNodeMap attributes = processor.getDoc().getElementsByTagName("par").item(0)
        .getAttributes();

    assertTrue(allMatch(attributes, ImmutableMap.of("step", "1", "name", "ФИО ПЛАТЕЛЬЩИКА")));
    assertFalse(allMatch(attributes, ImmutableMap.of("step", "1", "name", "ВИД_ДОК")));
    assertFalse(allMatch(attributes, ImmutableMap.of("xxx", "")));
    assertFalse(allMatch(attributes, new HashMap() {{
      put("name", null);
    }}));
    assertTrue(allMatch(attributes, Collections.emptyMap()));
  }

}
