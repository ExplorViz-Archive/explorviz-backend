package net.explorviz.math;

import net.explorviz.model.helper.BaseEntity;

public class Vector3f extends BaseEntity{
	
	private static final long serialVersionUID = 1L;
	
	public float x, y, z;

	public Vector3f() {
		x = y = z = 0;
	}

	public Vector3f(final Vector3f v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vector3f(final Vector4f v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vector3f(final float val) {
		x = y = z = val;
	}

	public Vector3f(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f add(final Vector3f v) {
		return new Vector3f(x + v.x, y + v.y, z + v.z);
	}

	public Vector3f sub(final Vector3f v) {
		return new Vector3f(x - v.x, y - v.y, z - v.z);
	}

	public Vector3f negate() {
		return new Vector3f(-x, -y, -z);
	}

	public Vector3f scale(final float c) {
		return new Vector3f(x * c, y * c, z * c);
	}

	public Vector3f div(final float c) {
		if ((c < 0.000001) && (c > -0.000001)) {
			throw new IllegalArgumentException("c must not be 0");
		}
		return new Vector3f(x / c, y / c, z / c);
	}

	public float dot(final Vector3f v) {
		return (x * v.x) + (y * v.y) + (z * v.z);
	}

	public Vector3f cross(final Vector3f v) {
		return new Vector3f((y * v.z) - (z * v.y), (z * v.x) - (x * v.z), (x * v.y) - (y * v.x));
	}

	public float length() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}

	public Vector3f scaleToLength(final float L) {
		return new Vector3f((x * L) / length(), (y * L) / length(), (z * L) / length());
	}

	public Vector3f normalize() {
		final float invlen = (float) (1.0 / Math.sqrt((x * x) + (y * y) + (z * z)));
		return new Vector3f(x * invlen, y * invlen, z * invlen);
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Vector3f) {
			final Vector3f otherVector = (Vector3f) other;
			return (checkFloatEquals(x, otherVector.x) && checkFloatEquals(y, otherVector.y)
					&& checkFloatEquals(z, otherVector.z));
		}
		return false;
	}

	private boolean checkFloatEquals(final float first, final float second) {
		return Math.abs(first - second) < 0.0001f;

	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public Vector3f mult(final float c) {
		return new Vector3f(x * c, y * c, z * c);
	}

}
