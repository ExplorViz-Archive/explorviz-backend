package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;

/**
 * Helper class for ClazzCommunications
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class ClazzCommunicationHelper {

	/**
	 * Retrieves recursively all clazzCommunications for a component
	 *
	 * @param component
	 * @return
	 */
	public static List<ClazzCommunication> getChildrenComponentClazzCommunications(final Component component) {

		final List<ClazzCommunication> outgoingClazzCommunicationPartialList = new ArrayList<ClazzCommunication>();
		// get children components -> recursive
		for (final Component child : component.getChildren()) {
			if (!child.getChildren().isEmpty()) {
				outgoingClazzCommunicationPartialList.addAll(getChildrenComponentClazzCommunications(child));
			}
		}
		// get clazz communications
		for (final Clazz clazz : component.getClazzes()) {
			for (final ClazzCommunication clazzCommunication : clazz.getOutgoingClazzCommunications()) {
				outgoingClazzCommunicationPartialList.add(clazzCommunication);
			}
		}

		return outgoingClazzCommunicationPartialList;
	}

}
