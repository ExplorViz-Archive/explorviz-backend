package net.explorviz.repository.helper;

import java.util.List
import java.util.ArrayList
import org.eclipse.xtend.lib.annotations.Accessors

class Signature {
	@Accessors val List<String> modifierList = new ArrayList<String>()
	@Accessors String returnType = null
	@Accessors String fullQualifiedName
	@Accessors String name
	@Accessors String operationName
	@Accessors val List<String> paramTypeList = new ArrayList<String>()
}