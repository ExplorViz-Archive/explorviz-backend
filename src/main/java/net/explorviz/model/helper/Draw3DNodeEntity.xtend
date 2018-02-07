package net.explorviz.model.helper;

import org.eclipse.xtend.lib.annotations.Accessors

abstract class Draw3DNodeEntity extends BaseEntity {
	@Accessors var String name
	@Accessors var String fullQualifiedName

	@Accessors transient float width
	@Accessors transient float height
	@Accessors transient float depth

	@Accessors transient float positionX
	@Accessors transient float positionY
	@Accessors transient float positionZ

	@Accessors var boolean highlighted = false
}
