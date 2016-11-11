package net.explorviz.server.repository.helper;

import java.util.List;
import net.explorviz.server.repository.helper.Signature;

@SuppressWarnings("all")
public class SignatureParser {
  public static Signature parse(final String operationSignatureStr, final boolean javaConstructor) {
    Signature _xblockexpression = null;
    {
      final Signature result = new Signature();
      String restOfOperationSignatureStr = SignatureParser.parseParameterList(operationSignatureStr, result);
      SignatureParser.parseModifiersAndReturnAndName(restOfOperationSignatureStr, javaConstructor, result);
      SignatureParser.parseFQClassnameAndOperationName(javaConstructor, result);
      _xblockexpression = result;
    }
    return _xblockexpression;
  }
  
  private static String parseParameterList(final String operationSignatureStr, final Signature sig) {
    String _xblockexpression = null;
    {
      final int openParenIdx = operationSignatureStr.indexOf("(");
      String _xifexpression = null;
      if ((openParenIdx != (-1))) {
        String _xblockexpression_1 = null;
        {
          int _length = operationSignatureStr.length();
          int _minus = (_length - 1);
          String _substring = operationSignatureStr.substring((openParenIdx + 1), _minus);
          final String[] splitParams = _substring.split(",");
          for (final String splitParam : splitParams) {
            List<String> _paramTypeList = sig.getParamTypeList();
            String _trim = splitParam.trim();
            _paramTypeList.add(_trim);
          }
          _xblockexpression_1 = operationSignatureStr.substring(0, openParenIdx);
        }
        _xifexpression = _xblockexpression_1;
      } else {
        _xifexpression = operationSignatureStr;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  private static void parseModifiersAndReturnAndName(final String restOfOperationSignatureStr, final boolean javaConstructor, final Signature sig) {
    final int nameBeginIdx = restOfOperationSignatureStr.lastIndexOf(" ");
    if ((nameBeginIdx == (-1))) {
      sig.setName(restOfOperationSignatureStr);
    } else {
      final String[] modRetNameArr = restOfOperationSignatureStr.split("\\s");
      int modifierEndLength = 0;
      if (javaConstructor) {
        int _length = modRetNameArr.length;
        int _minus = (_length - 1);
        modifierEndLength = _minus;
      } else {
        int _length_1 = modRetNameArr.length;
        int _minus_1 = (_length_1 - 2);
        String _get = modRetNameArr[_minus_1];
        sig.setReturnType(_get);
        int _length_2 = modRetNameArr.length;
        int _minus_2 = (_length_2 - 2);
        modifierEndLength = _minus_2;
      }
      int i = 0;
      while ((i < modifierEndLength)) {
        {
          List<String> _modifierList = sig.getModifierList();
          String _get_1 = modRetNameArr[i];
          String _trim = _get_1.trim();
          _modifierList.add(_trim);
          i = (i + 1);
        }
      }
      int _length_3 = modRetNameArr.length;
      int _minus_3 = (_length_3 - 1);
      String _get_1 = modRetNameArr[_minus_3];
      sig.setName(_get_1);
    }
  }
  
  private static void parseFQClassnameAndOperationName(final boolean javaConstructor, final Signature result) {
    String _name = result.getName();
    final int opNameIdx = _name.lastIndexOf(".");
    String _xifexpression = null;
    if ((opNameIdx != (-1))) {
      String _name_1 = result.getName();
      _xifexpression = _name_1.substring(0, opNameIdx);
    } else {
      _xifexpression = "";
    }
    result.setFullQualifiedName(_xifexpression);
    if (javaConstructor) {
      String _fullQualifiedName = result.getFullQualifiedName();
      String _fullQualifiedName_1 = result.getFullQualifiedName();
      int _lastIndexOf = _fullQualifiedName_1.lastIndexOf(".");
      int _plus = (_lastIndexOf + 1);
      final String onlyClassName = _fullQualifiedName.substring(_plus);
      result.setOperationName(("new " + onlyClassName));
    } else {
      String _name_2 = result.getName();
      String _substring = _name_2.substring((opNameIdx + 1));
      result.setOperationName(_substring);
    }
  }
}
