package net.explorviz.math

import java.io.Serializable
import org.eclipse.xtend.lib.annotations.Accessors

class Vector4f implements Serializable {	

	@Accessors float x
	@Accessors float y
	@Accessors float z
	@Accessors float w

	new() {
		x = y = z = w = 0
	}

	new(Vector4f v) {
		x = v.x
		y = v.y
		z = v.z
		w = v.w
	}

	new(float x, float y, float z, float w) {
		this.x = x
		this.y = y
		this.z = z
		this.w = w
	}

	new(Vector3f v, float w) {
		x = v.x
		y = v.y
		z = v.z
		this.w = w
	}

	def Vector4f add(Vector4f v) {
		return new Vector4f(x + v.x, y + v.y, z + v.z, w + v.w)
	}

	def Vector4f sub(Vector4f v) {
		return new Vector4f(x - v.x, y - v.y, z - v.z, w - v.w)
	}

	def Vector4f negate() {
		return new Vector4f(-x, -y, -z, -w)
	}

	def Vector4f scale(float c) {
		return new Vector4f(x * c, y * c, z * c, w * c)
	}

	def Vector4f div(float c) {
		if ((c < 0.000001) && (c > -0.000001)) {
			throw new IllegalArgumentException("c must not be 0")
		}
		return new Vector4f(x / c, y / c, z / c, w / c)
	}

	def double dot(Vector4f v) {
		return (x * v.x) + (y * v.y) + (z * v.z) + (w * v.w)
	}

	def double length() {
		return Math::sqrt((x * x) + (y * y) + (z * z) + (w * w))
	}

	def Vector4f normalize() {
		val float invlen = ((1.0 / Math::sqrt((x * x) + (y * y) + (z * z) + (w * w))) as float)
		return new Vector4f(x * invlen, y * invlen, z * invlen, w * invlen)
	}

	override String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")"
	}

	def Vector3f convertTo3f() {
		return new Vector3f(x, y, z)
	}
}
