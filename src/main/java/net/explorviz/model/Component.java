package net.explorviz.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.Draw3DNodeEntity;

@SuppressWarnings("serial")
@Type("component")
@JsonIgnoreProperties("belongingApplication")
public class Component extends Draw3DNodeEntity {

	private String name;
	private String fullQualifiedName;
	private final boolean synthetic = false;
	private boolean foundation;

	@Relationship("children")
	private final List<Component> children = new ArrayList<Component>();

	@Relationship("clazzes")
	private final List<Clazz> clazzes = new ArrayList<Clazz>();

	@Relationship("parentComponent")
	private Component parentComponent;

	// @Relationship("belongingApplication")
	// Don't parse since cycle results in stackoverflow when accessing
	// latestLandscape
	private Application belongingApplication;

	private boolean opened = false;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getFullQualifiedName() {
		return fullQualifiedName;
	}

	@Override
	public void setFullQualifiedName(final String fullQualifiedName) {
		this.fullQualifiedName = fullQualifiedName;
	}

	public Component getParentComponent() {
		return parentComponent;
	}

	public void setParentComponent(final Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	public Application getBelongingApplication() {
		return belongingApplication;
	}

	public void setBelongingApplication(final Application belongingApplication) {
		this.belongingApplication = belongingApplication;
	}

	public boolean isSynthetic() {
		return synthetic;
	}

	public void setFoundation(final boolean foundation) {
		this.foundation = foundation;
	}

	public boolean isFoundation() {
		return foundation;
	}

	public List<Component> getChildren() {
		return children;
	}

	public List<Clazz> getClazzes() {
		return clazzes;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(final boolean openedParam) {
		if (!openedParam) {
			setAllChildrenUnopened();
		}
		this.opened = openedParam;
	}

	private void setAllChildrenUnopened() {
		for (final Component child : children) {
			child.setOpened(false);
		}
	}

	public void openAllComponents() {
		opened = true;
		for (final Component child : children) {
			child.openAllComponents();
		}
	}

	public void closeAllComponents() {
		opened = false;
		for (final Component child : children) {
			child.closeAllComponents();
		}
	}

}