package net.explorviz.landscape.repository.helper;

/**
 * Parses a class signature for the full-qualified-name and operation name.
 */
public final class SignatureParser {

  private SignatureParser() {
    // Utility Class
  }

  public static Signature parse(final String operationSignatureStr, final boolean javaConstructor) {
    final Signature result = new Signature();

    final String restOfOperationSignatureStr = parseParameterList(operationSignatureStr, result);
    parseModifiersAndReturnAndName(restOfOperationSignatureStr, javaConstructor, result);
    parseFqClassnameAndOperationName(javaConstructor, result);

    return result;
  }

  /**
   * Parses the list of parameters.
   *
   * @param operationSignatureStr
   * @param sig
   * @return
   */
  private static String parseParameterList(final String operationSignatureStr,
      final Signature sig) {
    final int openParenIdx = operationSignatureStr.indexOf('(');
    if (openParenIdx == -1) {
      return operationSignatureStr;
    } else {
      final String[] splitParams =
          operationSignatureStr.substring(openParenIdx + 1, operationSignatureStr.length() - 1)
              .split(",");
      for (final String splitParam : splitParams) {
        sig.getParamTypeList().add(splitParam.trim());
      }
      return operationSignatureStr.substring(0, openParenIdx);
    }
  }

  /**
   * Parses modifies and return names.
   *
   * @param restOfOperationSignatureStr
   * @param javaConstructor
   * @param sig
   */
  private static void parseModifiersAndReturnAndName(final String restOfOperationSignatureStr,
      final boolean javaConstructor, final Signature sig) {

    final int nameBeginIdx = restOfOperationSignatureStr.lastIndexOf(' ');

    if (nameBeginIdx == -1) {
      sig.setName(restOfOperationSignatureStr);
    } else {
      final String[] modRetNameArr = restOfOperationSignatureStr.split("\\s");
      int modifierEndLength = 0;

      if (javaConstructor) {
        modifierEndLength = modRetNameArr.length - 1;
      } else {
        sig.setReturnType(modRetNameArr[modRetNameArr.length - 2]);
        modifierEndLength = modRetNameArr.length - 2;
      }
      int i = 0;
      while (i < modifierEndLength) {
        sig.getModifierList().add(modRetNameArr[i].trim());
        i = i + 1;
      }
      sig.setName(modRetNameArr[modRetNameArr.length - 1]);
    }
  }

  /**
   * Parses the full-qualified class- and operation name.
   *
   * @param javaConstructor - TODOa
   * @param result - Todoa
   * @return
   */
  private static String parseFqClassnameAndOperationName(final boolean javaConstructor,
      final Signature result) {
    final int opNameIdx = result.getName().lastIndexOf('.');
    if (opNameIdx == -1) {
      result.setFullQualifiedName("");
    } else {
      result.setFullQualifiedName(result.getName().substring(0, opNameIdx));
    }

    if (javaConstructor) {
      final String onlyClassName = result.getFullQualifiedName()
          .substring(result.getFullQualifiedName().lastIndexOf('.') + 1);
      result.setOperationName("new " + onlyClassName);
      return result.getOperationName();
    } else {
      result.setOperationName(result.getName().substring(opNameIdx + 1));
      return result.getOperationName();
    }
  }
}
