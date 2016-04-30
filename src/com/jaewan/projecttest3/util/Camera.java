package com.jaewan.projecttest3.util;

import static org.lwjgl.util.glu.GLU.gluLookAt;

public class Camera
{
	public static final float DEG_TO_RAD = (float) Math.PI / 180.0f;
	
	public static final Vector sky = new Vector(0, 1, 0);
	
	public Vector position = new Vector(64, 0, -64);
	
	public Vector right = new Vector(1, 0, 0);
	
	public Vector sight = new Vector(0, 0, -1);
	
	public float rotationX = 0;
	
	public void updateMatrix()
	{
		Vector lookAt = position.plus(sight);
		
		gluLookAt((float) position.x,
				(float) position.y,
				(float) position.z,
				(float) lookAt.x,
				(float) lookAt.y,
				(float) lookAt.z,
				(float) sky.x,
				(float) sky.y,
				(float) sky.z);
	}
	
	public void move(Vector vector)
	{
		position.add(vector.invertedZ());
	}
	
	public void moveForward(float distance)
	{
		position.add(new Vector(sight.x, 0, sight.z).normalized().scaled(distance));
	}
	
	public void strafeRight(float distance)
	{
		position.add(right.scaled(distance));
	}
	
	public void pitch(float angle)
	{
		if(rotationX + angle < -89.0f || rotationX + angle > 89.0f)
		{
			return;
		}
		
		rotationX += angle;
		
		sight = Vector.axisRotation(sight, right, angle * DEG_TO_RAD);
	}
	
	public void yaw(float angle)
	{
		sight = Vector.axisRotation(sight, sky, -angle * DEG_TO_RAD);
		right = Vector.cross(sight, sky).normalized();
	}
	
	public void setPositionX(float x)
	{
		position.x = x;
	}
	
	public void setPositionY(float y)
	{
		position.y = y;
	}
	
	public void setPositionZ(float z)
	{
		position.z = -z;
	}
	
	public Vector getPosition()
	{
		return position.invertedZ();
	}
	
	public Vector getSight()
	{
		return sight.invertedZ();
	}
}
