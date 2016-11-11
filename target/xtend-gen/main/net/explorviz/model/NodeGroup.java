package net.explorviz.model;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.explorviz.model.Node;
import net.explorviz.model.helper.DrawNodeEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class NodeGroup extends DrawNodeEntity {
  public static class NameComperator implements Comparator<String> {
    @Override
    public int compare(final String o1, final String o2) {
      boolean _and = false;
      boolean _endsInNumber = this.endsInNumber(o1);
      if (!_endsInNumber) {
        _and = false;
      } else {
        boolean _endsInNumber_1 = this.endsInNumber(o2);
        _and = _endsInNumber_1;
      }
      if (_and) {
        final double o1Number = this.getLastNumber(o1);
        final double o2Number = this.getLastNumber(o2);
        return ((int) (o1Number - o2Number));
      } else {
        return o1.compareToIgnoreCase(o2);
      }
    }
    
    public double getLastNumber(final String arg) {
      double _xblockexpression = (double) 0;
      {
        int _length = arg.length();
        int i = (_length - 1);
        double result = 0d;
        int index = 0;
        while (((i >= 0) && this.isNumber(arg.charAt(i)))) {
          {
            String _substring = arg.substring(i, (i + 1));
            final int currentNumber = Integer.parseInt(_substring);
            double _pow = Math.pow(10, index);
            double _multiply = (currentNumber * _pow);
            double _plus = (_multiply + result);
            result = _plus;
            i = (i - 1);
            index = (index + 1);
          }
        }
        _xblockexpression = result;
      }
      return _xblockexpression;
    }
    
    public boolean endsInNumber(final String arg) {
      boolean _xifexpression = false;
      boolean _notEquals = (!Objects.equal(arg, null));
      if (_notEquals) {
        int _length = arg.length();
        int _minus = (_length - 1);
        char _charAt = arg.charAt(_minus);
        _xifexpression = this.isNumber(_charAt);
      } else {
        _xifexpression = false;
      }
      return _xifexpression;
    }
    
    public boolean isNumber(final char c) {
      return Character.isDigit(c);
    }
  }
  
  @Accessors
  private List<Node> nodes = new ArrayList<Node>();
  
  @Accessors
  private net.explorviz.model.System parent;
  
  @Accessors
  private boolean visible = true;
  
  private boolean opened;
  
  public boolean isOpened() {
    return this.opened;
  }
  
  public void setOpened(final boolean openedParam) {
    if (openedParam) {
      this.setAllChildrenVisibility(true);
    } else {
      this.setAllChildrenVisibility(false);
      int _size = this.nodes.size();
      boolean _greaterThan = (_size > 0);
      if (_greaterThan) {
        final Node firstNode = this.nodes.get(0);
        firstNode.setVisible(true);
      }
    }
    this.opened = openedParam;
  }
  
  public void updateName() {
    final List<String> names = this.getAllNames();
    NodeGroup.NameComperator _nameComperator = new NodeGroup.NameComperator();
    Collections.<String>sort(names, _nameComperator);
    int _size = names.size();
    boolean _greaterEqualsThan = (_size >= 2);
    if (_greaterEqualsThan) {
      final String first = names.get(0);
      int _size_1 = names.size();
      int _minus = (_size_1 - 1);
      final String last = names.get(_minus);
      this.setName(((first + " - ") + last));
    } else {
      int _size_2 = names.size();
      boolean _equals = (_size_2 == 1);
      if (_equals) {
        String _get = names.get(0);
        this.setName(_get);
      } else {
        this.setName("<NO-NAME>");
      }
    }
  }
  
  private List<String> getAllNames() {
    ArrayList<String> _xblockexpression = null;
    {
      final ArrayList<String> result = new ArrayList<String>();
      for (final Node node : this.nodes) {
        boolean _and = false;
        boolean _and_1 = false;
        String _name = node.getName();
        boolean _notEquals = (!Objects.equal(_name, null));
        if (!_notEquals) {
          _and_1 = false;
        } else {
          String _name_1 = node.getName();
          boolean _isEmpty = _name_1.isEmpty();
          boolean _not = (!_isEmpty);
          _and_1 = _not;
        }
        if (!_and_1) {
          _and = false;
        } else {
          String _name_2 = node.getName();
          boolean _startsWith = _name_2.startsWith("<");
          boolean _not_1 = (!_startsWith);
          _and = _not_1;
        }
        if (_and) {
          String _name_3 = node.getName();
          result.add(_name_3);
        } else {
          String _ipAddress = node.getIpAddress();
          result.add(_ipAddress);
        }
      }
      _xblockexpression = result;
    }
    return _xblockexpression;
  }
  
  public void setAllChildrenVisibility(final boolean visiblity) {
    for (final Node node : this.nodes) {
      node.setVisible(visiblity);
    }
  }
  
  @Pure
  public List<Node> getNodes() {
    return this.nodes;
  }
  
  public void setNodes(final List<Node> nodes) {
    this.nodes = nodes;
  }
  
  @Pure
  public net.explorviz.model.System getParent() {
    return this.parent;
  }
  
  public void setParent(final net.explorviz.model.System parent) {
    this.parent = parent;
  }
  
  @Pure
  public boolean isVisible() {
    return this.visible;
  }
  
  public void setVisible(final boolean visible) {
    this.visible = visible;
  }
}
