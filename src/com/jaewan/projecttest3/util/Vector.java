package com.jaewan.projecttest3.util;

public final class Vector
{
	public float x;

	public float y;

	public float z;

	public Vector(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector()
	{
		this(0, 0, 0);
	}

	public float magnitude()
	{
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}

	public float magnitudeSquared()
	{
		return ((x * x) + (y * y) + (z * z));
	}

	public void add(Vector vec)
	{
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}
	
	public void sub(Vector vec)
	{
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
	}

	public void scale(float s)
	{
		x *= s;
		y *= s;
		z *= s;
	}

	public float dot(Vector vec)
	{
		return (x * vec.x + y * vec.y + z * vec.z);
	}
	
	public static Vector cross(Vector u, Vector v)
	{
		return new Vector(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z, u.x
				* v.y - u.y * v.x);
	}

	public void normalize()
	{
		float mag = magnitude();
		if (mag == 0)
			return;
		x /= mag;
		y /= mag;
		z /= mag;
	}

	public Vector plus(Vector vec)
	{
		return new Vector(x + vec.x, y + vec.y, z + vec.z);
	}

	public Vector minus(Vector vec)
	{
		return new Vector(x - vec.x, y - vec.y, z - vec.z);
	}

	public Vector scaled(float s)
	{
		return new Vector(x * s, y * s, z * s);
	}

	public Vector normalized()
	{
		float mag = magnitude();
		if (mag == 0)
			return new Vector();
		return new Vector(x / mag, y / mag, z / mag);
	}

	public static Vector axisRotation(Vector vec, Vector axis, float angle)
	{
		Vector nAxis = axis.normalized();

		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);

		return new Vector(nAxis.x
				* (nAxis.x * vec.x + nAxis.y * vec.y + nAxis.z * vec.z)
				* (1.f - c) + vec.x * c + (-nAxis.z * vec.y + nAxis.y * vec.z)
				* s, nAxis.y
				* (nAxis.x * vec.x + nAxis.y * vec.y + nAxis.z * vec.z)
				* (1.f - c) + vec.y * c + (nAxis.z * vec.x - nAxis.x * vec.z)
				* s, nAxis.z
				* (nAxis.x * vec.x + nAxis.y * vec.y + nAxis.z * vec.z)
				* (1.f - c) + vec.z * c + (-nAxis.y * vec.x + nAxis.x * vec.y)
				* s);
	}
	
	public Vector invertedZ()
	{
		return new Vector(x, y, -z);
	}
}