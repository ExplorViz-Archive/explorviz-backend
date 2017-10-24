package net.explorviz.server.main

import java.io.File

class FileSystemHelper {
	def static String getExplorVizDirectory() {
		val String homefolder = System.getProperty("user.home")
		val String filePath = homefolder + '/.explorviz'
		new File(filePath).mkdir()
		return filePath
	}
}
