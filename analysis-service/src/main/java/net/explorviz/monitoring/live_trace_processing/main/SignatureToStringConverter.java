package net.explorviz.monitoring.live_trace_processing.main;

import java.lang.reflect.Modifier;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

public class SignatureToStringConverter {
  public static final String signatureToLongString(final Signature sig) {
    if (sig instanceof MethodSignature) {
      final MethodSignature signature = (MethodSignature) sig;
      final StringBuilder sb = new StringBuilder(256);
      // modifiers
      final String modString = Modifier.toString(signature.getModifiers());
      sb.append(modString);
      if (modString.length() > 0) {
        sb.append(' ');
      }
      // return
      addType(sb, signature.getReturnType());
      sb.append(' ');
      // component
      sb.append(signature.getDeclaringTypeName());
      sb.append('.');
      // name
      sb.append(signature.getName());
      // parameters
      sb.append('(');
      addTypeList(sb, signature.getParameterTypes());
      sb.append(')');
      // throws
      // this.addTypeList(sb, signature.getExceptionTypes());
      return sb.toString();
    } else if (sig instanceof ConstructorSignature) {
      final ConstructorSignature signature = (ConstructorSignature) sig;
      final StringBuilder sb = new StringBuilder(256);
      // modifiers
      final String modString = Modifier.toString(signature.getModifiers());
      sb.append(modString);
      if (modString.length() > 0) {
        sb.append(' ');
      }
      // component
      sb.append(signature.getDeclaringTypeName());
      sb.append('.');
      // name
      sb.append(signature.getName());
      // parameters
      sb.append('(');
      addTypeList(sb, signature.getParameterTypes());
      sb.append(')');
      // throws
      // this.addTypeList(sb, signature.getExceptionTypes());
      return sb.toString();
    } else {
      return sig.toLongString();
    }
  }

  private static final StringBuilder addTypeList(final StringBuilder sb, final Class<?>[] clazzes) {
    if (null != clazzes) {
      boolean first = true;
      for (final Class<?> clazz : clazzes) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        addType(sb, clazz);
      }
    }
    return sb;
  }

  private static final StringBuilder addType(final StringBuilder sb, final Class<?> clazz) {
    if (null == clazz) {
      sb.append("ANONYMOUS");
    } else if (clazz.isArray()) {
      final Class<?> componentType = clazz.getComponentType();
      addType(sb, componentType);
      sb.append("[]");
    } else {
      sb.append(clazz.getName());
    }
    return sb;
  }
}
