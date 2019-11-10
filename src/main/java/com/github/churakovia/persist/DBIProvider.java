package com.github.churakovia.persist;

import com.google.common.io.Resources;
import java.io.IOException;
import java.sql.DriverManager;
import java.util.Properties;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.logging.SLF4JLog;

public class DBIProvider {

  private static DBI jDBI;

  public static DBI getJDBI() throws IOException {
    if (jDBI == null) {
      initDBI();
      return jDBI;
    } else {
      return jDBI;
    }
  }

  public static <T> T getDao(Class<T> daoClass) throws IOException {
    if (jDBI == null) {
      initDBI();
    }
    return jDBI.onDemand(daoClass);
  }

  private DBIProvider() {
  }

  private static void initDBI() throws IOException {
    Properties properties = new Properties();
    properties.load(Resources.getResource("db/hsqldb.properties").openStream());
    String dbUrl = properties.getProperty("database.url");
    String dbUser = properties.getProperty("database.username");
    String dbPassword = properties.getProperty("database.password");

    jDBI = new DBI(() -> {
      try {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException("hsqldb driver not found", e);
      }
      return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    });
    jDBI.setSQLLog(new SLF4JLog());
  }

}
