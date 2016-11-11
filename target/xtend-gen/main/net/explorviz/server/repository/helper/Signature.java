package net.explorviz.server.repository.helper;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Signature {
  @Accessors
  private final List<String> modifierList = new ArrayList<String>();
  
  @Accessors
  private String returnType = null;
  
  @Accessors
  private String fullQualifiedName;
  
  @Accessors
  private String name;
  
  @Accessors
  private String operationName;
  
  @Accessors
  private final List<String> paramTypeList = new ArrayList<String>();
  
  @Pure
  public List<String> getModifierList() {
    return this.modifierList;
  }
  
  @Pure
  public String getReturnType() {
    return this.returnType;
  }
  
  public void setReturnType(final String returnType) {
    this.returnType = returnType;
  }
  
  @Pure
  public String getFullQualifiedName() {
    return this.fullQualifiedName;
  }
  
  public void setFullQualifiedName(final String fullQualifiedName) {
    this.fullQualifiedName = fullQualifiedName;
  }
  
  @Pure
  public String getName() {
    return this.name;
  }
  
  public void setName(final String name) {
    this.name = name;
  }
  
  @Pure
  public String getOperationName() {
    return this.operationName;
  }
  
  public void setOperationName(final String operationName) {
    this.operationName = operationName;
  }
  
  @Pure
  public List<String> getParamTypeList() {
    return this.paramTypeList;
  }
}
