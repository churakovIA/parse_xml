package com.github.churakovia.xml.upload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.churakovia.persist.DBIProvider;
import com.github.churakovia.persist.dao.DocTypeDao;
import com.github.churakovia.persist.model.DocType;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.Handle;

class PayloadProcessorTest {

  @Test
  void parseDocTypes() throws Exception {
    Set<String> docTypes = PayloadProcessor
        .parseDocTypes(getResource("payload.xml"), Comparator.naturalOrder());

    assertNotNull(docTypes);
    assertEquals(9, docTypes.size());

    System.out.println("\nВиды документов:");
    docTypes.forEach(System.out::println);
  }

  @Test
  void getAttributes() throws Exception {
    Map<String, String> attributes = PayloadProcessor.getAttributes(getResource("payload.xml"));

    assertNotNull(attributes);
    assertEquals(17, attributes.size());

    System.out.println("\nАтрибуты для par step=\"1\" name=\"ГРАЖДАНСТВО\":");
    attributes.forEach((key, value) -> System.out.printf("%s:%s\n", key, value));
  }

  @Test
  void uploadDocTypes() throws Exception {
    Set<String> docTypes = PayloadProcessor
        .parseDocTypes(getResource("payload.xml"), Comparator.naturalOrder());

    initDB();

    DocTypeDao dao = DBIProvider.getDao(DocTypeDao.class);
    DBIProvider.getJDBI()
        .useTransaction((conn, status) -> docTypes.stream().map(DocType::new).forEach(dao::insert));

    List<DocType> actual = dao.getAll();

    assertNotNull(actual);
    assertEquals(9, actual.size());

    System.out.println("\nДанные справочника видов докуметов:");
    actual.forEach(System.out::println);
  }

  private void initDB() throws IOException {
    String sql = CharStreams.toString(new InputStreamReader(
        getResource("db/initDB_hsql.sql"), Charsets.UTF_8));

    try (Handle h = DBIProvider.getJDBI().open()) {
      h.createScript(sql).execute();
    }
  }

  private InputStream getResource(String resourceName) throws IOException {
    return Resources.getResource(resourceName).openStream();
  }
}