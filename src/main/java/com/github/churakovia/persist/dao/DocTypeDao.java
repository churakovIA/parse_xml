package com.github.churakovia.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import com.github.churakovia.persist.model.DocType;
import java.util.List;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class DocTypeDao {

  public DocType insert(DocType docType) {
    if (docType.getId() == null) {
      int id = insertGeneratedId(docType);
      docType.setId(id);
    } else {
      insertWitId(docType);
    }
    return docType;
  }

  @SqlUpdate("INSERT INTO doc_types (name) VALUES (:name)")
  @GetGeneratedKeys
  abstract int insertGeneratedId(@BindBean DocType docType);

  @SqlUpdate("INSERT INTO doc_types (id, name) VALUES (:id, :name")
  abstract void insertWitId(@BindBean DocType docType);

  @SqlQuery("SELECT * FROM doc_types ORDER BY name")
  public abstract List<DocType> getAll();

}
