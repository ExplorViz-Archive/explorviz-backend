package net.explorviz.landscape.model.helper;

import java.util.Comparator;
import net.explorviz.landscape.model.landscape.NodeGroup;

/**
 * Helper class for {@link NodeGroup}.
 */
public class NameComperator implements Comparator<String> {
  @Override
  public int compare(final String o1, final String o2) {
    if (this.endsInNumber(o1) && this.endsInNumber(o2)) {
      final double o1Number = this.getLastNumber(o1);
      final double o2Number = this.getLastNumber(o2);

      return (int) (o1Number - o2Number);
    } else {
      return o1.compareToIgnoreCase(o2);
    }
  }

  public double getLastNumber(final String arg) {
    int i = arg.length() - 1;
    double result = 0d;
    int index = 0;

    while (i >= 0 && this.isNumber(arg.charAt(i))) {
      final int currentNumber = Integer.parseInt(arg.substring(i, i + 1));
      result = currentNumber * Math.pow(10, index) + result;
      i = i - 1;
      index = index + 1;
    }

    return result;
  }

  public boolean endsInNumber(final String arg) {
    if (arg == null) {
      return false;
    } else {
      return this.isNumber(arg.charAt(arg.length() - 1));
    }
  }

  public boolean isNumber(final char c) {
    return Character.isDigit(c);
  }
}
