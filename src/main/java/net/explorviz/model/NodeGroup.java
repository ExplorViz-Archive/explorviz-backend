package net.explorviz.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.DrawNodeEntity;

@SuppressWarnings("serial")
@Type("nodegroup")
public class NodeGroup extends DrawNodeEntity {

	@Relationship("parent")
	private final List<Node> nodes = new ArrayList<Node>();

	@Relationship("parent")
	private System parent;

	private boolean visible = true;
	private boolean opened = true;

	public System getParent() {
		return parent;
	}

	public void setParent(final System parent) {
		this.parent = parent;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(final boolean openedParam) {
		if (openedParam) {
			setAllChildrenVisibility(true);
		} else {
			setAllChildrenVisibility(false);
			if (nodes.size() > 0) {
				final Node firstNode = nodes.get(0);
				firstNode.setVisible(true);
			}
		}

		this.opened = openedParam;
	}

	public void updateName() {
		final List<String> allNames = getAllNames();
		Collections.sort(allNames, new NameComperator());

		if (allNames.size() >= 2) {
			final String first = allNames.get(0);
			final String last = allNames.get(allNames.size() - 1);

			setName(first + " - " + last);
		} else if (allNames.size() == 1) {
			setName(allNames.get(0));
		} else {
			setName("<NO-NAME>");
		}
	}

	private List<String> getAllNames() {
		final List<String> allNames = new ArrayList<String>();
		for (final Node node : nodes) {
			if (node.getName() != null && !node.getName().isEmpty() && !node.getName().startsWith("<")) {
				allNames.add(node.getName());
			} else {
				allNames.add(node.getIpAddress());
			}
		}
		return allNames;
	}

	public void setAllChildrenVisibility(final boolean visiblity) {
		for (final Node node : nodes) {
			node.setVisible(visiblity);
		}
	}

	static class NameComperator implements Comparator<String> {
		@Override
		public int compare(final String o1, final String o2) {
			if (endsInNumber(o1) && endsInNumber(o2)) {
				final double o1Number = getLastNumber(o1);
				final double o2Number = getLastNumber(o2);

				return (int) (o1Number - o2Number);
			} else {
				return o1.compareToIgnoreCase(o2);
			}
		}

		public double getLastNumber(final String arg) {
			int i = arg.length() - 1;
			double result = 0d;
			int index = 0;

			while (i >= 0 && isNumber(arg.charAt(i))) {
				final int currentNumber = Integer.parseInt(arg.substring(i, i + 1));
				result = currentNumber * Math.pow(10, index) + result;
				i = i - 1;
				index = index + 1;
			}

			return result;
		}

		public boolean endsInNumber(final String arg) {
			if (arg != null) {
				return isNumber(arg.charAt(arg.length() - 1));
			} else {
				return false;
			}
		}

		public boolean isNumber(final char c) {
			return Character.isDigit(c);
		}
	}

}