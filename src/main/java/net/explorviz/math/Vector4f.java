package net.explorviz.math;

import net.explorviz.model.helper.BaseEntity;

public class Vector4f extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public float x, y, z, w;

	public Vector4f() {
		x = y = z = w = 0;
	}

	public Vector4f(final Vector4f v) {
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}

	public Vector4f(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f(final Vector3f v, final float w) {
		x = v.x;
		y = v.y;
		z = v.z;
		this.w = w;
	}

	public Vector4f add(final Vector4f v) {
		return new Vector4f(x + v.x, y + v.y, z + v.z, w + v.w);
	}

	public Vector4f sub(final Vector4f v) {
		return new Vector4f(x - v.x, y - v.y, z - v.z, w - v.w);
	}

	public Vector4f negate() {
		return new Vector4f(-x, -y, -z, -w);
	}

	public Vector4f scale(final float c) {
		return new Vector4f(x * c, y * c, z * c, w * c);
	}

	public Vector4f div(final float c) {
		if ((c < 0.000001) && (c > -0.000001)) {
			throw new IllegalArgumentException("c must not be 0");
		}
		return new Vector4f(x / c, y / c, z / c, w / c);
	}

	public double dot(final Vector4f v) {
		return (x * v.x) + (y * v.y) + (z * v.z) + (w * v.w);
	}

	public double length() {
		return Math.sqrt((x * x) + (y * y) + (z * z) + (w * w));
	}

	public Vector4f normalize() {
		final float invlen = (float) (1.0 / Math.sqrt((x * x) + (y * y) + (z * z) + (w * w)));
		return new Vector4f(x * invlen, y * invlen, z * invlen, w * invlen);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}

	public Vector3f convertTo3f() {
		return new Vector3f(x, y, z);
	}

}
