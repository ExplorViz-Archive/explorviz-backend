package net.explorviz.layout.application;

import java.util.Comparator;

import net.explorviz.model.helper.Draw3DNodeEntity;

public class ComponentAndClassComparator implements Comparator<Draw3DNodeEntity> {

	@Override
	public int compare(final Draw3DNodeEntity o1, final Draw3DNodeEntity o2) {
		final float result = o1.getWidth() - o2.getWidth();

		if ((-0.00001f < result) && (result < 0.00001f)) {
			return o1.getName().compareTo(o2.getName());
		}

		if (result < 0) {
			return 1;
		} else {
			return -1;
		}
	}

}
