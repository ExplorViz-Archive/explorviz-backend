package net.explorviz.repository.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature for analyzed records.
 */
public class Signature {

  private final List<String> modifierList = new ArrayList<String>();
  private String returnType = null;
  private String fullQualifiedName;
  private String name;
  private String operationName;
  private final List<String> paramTypeList = new ArrayList<String>();

  public String getFullQualifiedName() {
    return fullQualifiedName;
  }

  public void setFullQualifiedName(final String fullQualifiedName) {
    this.fullQualifiedName = fullQualifiedName;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(final String operationName) {
    this.operationName = operationName;
  }

  public List<String> getModifierList() {
    return modifierList;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(final String returnType) {
    this.returnType = returnType;
  }

  public List<String> getParamTypeList() {
    return paramTypeList;
  }
}
