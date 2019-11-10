package com.github.churakovia.persist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocType {

  private Integer id;
  private @NonNull
  String name;

  public DocType(String name) {
    this.name = name;
  }
}
