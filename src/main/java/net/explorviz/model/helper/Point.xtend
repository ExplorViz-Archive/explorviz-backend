package net.explorviz.model.helper;

import org.eclipse.xtend.lib.annotations.Accessors


class Point extends BaseEntity{
	@Accessors float x
	@Accessors float y

	private val DELTA = 0.01f

	def boolean equals(Point other) {
		Math.abs(other.x - x) <= DELTA && Math.abs(other.y - y) <= DELTA
	}

	def Point sub(Point other) {
		val point = new Point()
		point.x = x - other.x
		point.y = y - other.y

		point
	}

	def Point add(Point other) {
		val point = new Point()
		point.x = x + other.x
		point.y = y + other.y

		point
	}
}
