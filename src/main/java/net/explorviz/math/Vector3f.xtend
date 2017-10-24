package net.explorviz.math

import java.io.Serializable
import org.eclipse.xtend.lib.annotations.Accessors

class Vector3f implements Serializable {
	
	@Accessors float x
	@Accessors float y
	@Accessors float z

	new() {
		x = y = z = 0
	}

	new(Vector3f v) {
		x = v.x
		y = v.y
		z = v.z
	}

	new(Vector4f v) {
		x = v.x
		y = v.y
		z = v.z
	}

	new(float ^val) {
		x = y = z = ^val
	}

	new(float x, float y, float z) {
		this.x = x
		this.y = y
		this.z = z
	}

	def Vector3f add(Vector3f v) {
		return new Vector3f(x + v.x, y + v.y, z + v.z)
	}

	def Vector3f sub(Vector3f v) {
		return new Vector3f(x - v.x, y - v.y, z - v.z)
	}

	def Vector3f negate() {
		return new Vector3f(-x, -y, -z)
	}

	def Vector3f scale(float c) {
		return new Vector3f(x * c, y * c, z * c)
	}

	def Vector3f div(float c) {
		if ((c < 0.000001) && (c > -0.000001)) {
			throw new IllegalArgumentException("c must not be 0")
		}
		return new Vector3f(x / c, y / c, z / c)
	}

	def float dot(Vector3f v) {
		return (x * v.x) + (y * v.y) + (z * v.z)
	}

	def Vector3f cross(Vector3f v) {
		return new Vector3f((y * v.z) - (z * v.y), (z * v.x) - (x * v.z), (x * v.y) - (y * v.x))
	}

	def float length() {
		return (Math::sqrt((x * x) + (y * y) + (z * z)) as float)
	}

	def Vector3f scaleToLength(float L) {
		return new Vector3f((x * L) / length(), (y * L) / length(), (z * L) / length())
	}

	def Vector3f normalize() {
		val float invlen = ((1.0 / Math::sqrt((x * x) + (y * y) + (z * z))) as float)
		return new Vector3f(x * invlen, y * invlen, z * invlen)
	}

	override boolean equals(Object other) {
		if (other instanceof Vector3f) {
			val Vector3f otherVector = other
			return (checkFloatEquals(x, otherVector.x) && checkFloatEquals(y, otherVector.y) &&
				checkFloatEquals(z, otherVector.z))
		}
		return false
	}

	def private boolean checkFloatEquals(float first, float second) {
		return Math::abs(first - second) < 0.0001f
	}

	override String toString() {
		return "(" + x + ", " + y + ", " + z + ")"
	}

	def Vector3f mult(float c) {
		return new Vector3f(x * c, y * c, z * c)
	}
}
