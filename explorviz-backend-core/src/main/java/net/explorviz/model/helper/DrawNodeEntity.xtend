package net.explorviz.model.helper;

import org.eclipse.xtend.lib.annotations.Accessors

abstract class DrawNodeEntity extends BaseEntity {
	@Accessors String name

	@Accessors transient float width
	@Accessors transient float height

	@Accessors transient float positionX
	@Accessors transient float positionY
	@Accessors transient float positionZ

}
