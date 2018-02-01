package net.explorviz.repository.helper;

import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.Clazz

class RemoteRecordBuffer {
	@Accessors long timestampPutIntoBuffer = java.lang.System.nanoTime()
	
	@Accessors Clazz belongingClazz
}