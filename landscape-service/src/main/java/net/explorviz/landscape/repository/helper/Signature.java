package net.explorviz.landscape.repository.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature for analyzed records.
 */
public class Signature {

  private final List<String> modifierList = new ArrayList<>();
  private String returnType;
  private String fullQualifiedName;
  private String name;
  private String operationName;
  private final List<String> paramTypeList = new ArrayList<>();

  public String getFullQualifiedName() {
    return this.fullQualifiedName;
  }

  public void setFullQualifiedName(final String fullQualifiedName) {
    this.fullQualifiedName = fullQualifiedName;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getOperationName() {
    return this.operationName;
  }

  public void setOperationName(final String operationName) {
    this.operationName = operationName;
  }

  public List<String> getModifierList() {
    return this.modifierList;
  }

  public String getReturnType() {
    return this.returnType;
  }

  public void setReturnType(final String returnType) {
    this.returnType = returnType;
  }

  public List<String> getParamTypeList() {
    return this.paramTypeList;
  }
}
