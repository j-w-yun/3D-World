package com.jaewan.projecttest3.props;

import com.jaewan.projecttest3.engine.Chunk;
import com.jaewan.projecttest3.util.Camera;
import com.jaewan.projecttest3.util.Helper;
import com.jaewan.projecttest3.util.Vector;

public class Player
{
	private static final float CAMERA_HEIGHT = 1.5f;
	
	private static final float MOVE_SPEED = 0.07f * 2;
	
	private static final float GRAVITY = -0.005f;
	
	private static final float INITIAL_JUMP_VELOCITY = 0.11f * 2;
	
	private float ground = 128;
	
	private float height = 5;
	
	private float velocity = 0;
	
	private Camera camera = new Camera();
	
	{
		camera.setPositionY(height + CAMERA_HEIGHT);
	}
	
	private Vector deltaPosition;
	
	public void jump()
	{
		ground = 0;
		height += 0.0001f;
		velocity = INITIAL_JUMP_VELOCITY;
	}
	
	public void collision(Chunk chunk)
	{
		Vector position = camera.getPosition();
		if(position.x < 0)
			camera.setPositionX(0);
		else if(position.x > 128)
			camera.setPositionX(128);
		
		if(position.z < 0)
			camera.setPositionZ(0);
		else if(position.z > 128)
			camera.setPositionZ(128);
		
		// Right and left
		if (deltaPosition.x > 0)
		{
			if ((int) Math.round(position.x) < 128 && (int) Math.round(position.x) > position.x
					&& ((position.z - 0.25f >= 0 && chunk.getBlockType(
							new Block((int) Math.round(position.x), (int) (height), (int) (position.z - 0.25f))) != 0)
					|| (position.z + 0.25f < 128 && chunk.getBlockType(
							new Block((int) Math.round(position.x), (int) (height), (int) (position.z + 0.25f))) != 0)
					|| (height + 1 < 128 && position.z - 0.25f >= 0
							&& chunk.getBlockType(new Block((int) Math.round(position.x), (int) (height + 1),
									(int) (position.z - 0.25f))) != 0)
					|| (height + 1 < 128 && position.z + 0.25f < 128
							&& chunk.getBlockType(new Block((int) Math.round(position.x), (int) (height + 1),
									(int) (position.z + 0.25f))) != 0)))
			{
				camera.setPositionX((int) Math.round(position.x) - 0.5f);
			}
		}
		else
		{
			if ((int) Math.round(position.x) - 1 >= 0 && (int) Math.round(position.x) < position.x
					&& ((position.z - 0.25f >= 0 && chunk.getBlockType(new Block((int) Math.round(position.x) - 1,
							(int) (height), (int) (position.z - 0.25f))) != 0)
					|| (position.z + 0.25f < 128 && chunk.getBlockType(new Block((int) Math.round(position.x) - 1,
							(int) (height), (int) (position.z + 0.25f))) != 0)
					|| (height + 1 < 128 && position.z - 0.25f >= 0
							&& chunk.getBlockType(new Block((int) Math.round(position.x) - 1, (int) (height + 1),
									(int) (position.z - 0.25f))) != 0)
					|| (height + 1 < 128 && position.z + 0.25f < 128
							&& chunk.getBlockType(new Block((int) Math.round(position.x) - 1, (int) (height + 1),
									(int) (position.z + 0.25f))) != 0)))
			{
				camera.setPositionX((int) Math.round(position.x) + 0.5f);
			}
		}
	
		// Forward and backward
		if (deltaPosition.z > 0)
		{
			if ((int) Math.round(position.z) < 128 && (int) Math.round(position.z) > position.z
					&& ((position.x - 0.25f >= 0 && chunk.getBlockType(
							new Block((int) (position.x - 0.25f), (int) (height), (int) Math.round(position.z))) != 0)
					|| (position.x + 0.25f < 128 && chunk.getBlockType(
							new Block((int) (position.x + 0.25f), (int) (height), (int) Math.round(position.z))) != 0)
					|| (height + 1 < 128 && position.x - 0.25f >= 0
							&& chunk.getBlockType(new Block((int) (position.x - 0.25f), (int) (height + 1),
									(int) Math.round(position.z))) != 0)
					|| (height + 1 < 128 && position.x + 0.25f < 128
							&& chunk.getBlockType(new Block((int) (position.x + 0.25f), (int) (height + 1),
									(int) Math.round(position.z))) != 0)))
			{
				camera.setPositionZ((int) Math.ceil(position.z) - 0.5f);
			}
		} else
		{
			if ((int) Math.round(position.z) - 1 >= 0 && (int) Math.round(position.z) < position.z
					&& ((position.x - 0.25f >= 0 && chunk.getBlockType(new Block((int) (position.x - 0.25f),
							(int) (height), (int) Math.round(position.z) - 1)) != 0)
					|| (position.x + 0.25f < 128 && chunk.getBlockType(new Block((int) (position.x + 0.25f),
							(int) (height), (int) Math.round(position.z) - 1)) != 0)
					|| (height + 1 < 128 && position.x - 0.25f >= 0
							&& chunk.getBlockType(new Block((int) (position.x - 0.25f), (int) (height + 1),
									(int) Math.round(position.z) - 1)) != 0)
					|| (height + 1 < 128 && position.x + 0.25f < 128
							&& chunk.getBlockType(new Block((int) (position.x + 0.25f), (int) (height + 1),
									(int) Math.round(position.z) - 1)) != 0)))
			{
				camera.setPositionZ((int) Math.round(position.z) + 0.5f);
			}
		}
	
		// Falling
		if (deltaPosition.y <= 0)
		{
			int drop = (int) height;
	
			// Cast down a line until it reaches a solid block, which is the
			// ground.
			while (drop >= 1 && !(((int) position.x < 128 && (int) position.z < 128
					&& chunk.getBlockType(new Block((int) (position.x), drop - 1, (int) (position.z))) != 0)
					|| ((int) position.z < 128 && position.x + 0.25f < 128
							&& chunk.getBlockType(
									new Block((int) (position.x + 0.25f), drop - 1, (int) (position.z))) != 0)
					|| ((int) position.x < 128 && position.z + 0.25f < 128
							&& chunk.getBlockType(
									new Block((int) (position.x), drop - 1, (int) (position.z + 0.25f))) != 0)
					|| (position.x + 0.25f < 128 && position.z + 0.25f < 128
							&& chunk.getBlockType(
									new Block((int) (position.x + 0.25f), drop - 1, (int) (position.z + 0.25f))) != 0)
					|| ((int) position.z < 128 && position.x - 0.25f >= 0
							&& chunk.getBlockType(
									new Block((int) (position.x - 0.25f), drop - 1, (int) (position.z))) != 0)
					|| ((int) position.x < 128 && position.z - 0.25f >= 0
							&& chunk.getBlockType(
									new Block((int) (position.x), drop - 1, (int) (position.z - 0.25f))) != 0)
					|| (position.z - 0.25f >= 0 && position.x - 0.25f >= 0
							&& chunk.getBlockType(
									new Block((int) (position.x - 0.25f), drop - 1, (int) (position.z - 0.25f))) != 0)
					|| (position.x + 0.25f < 128 && position.z - 0.25f >= 0
							&& chunk.getBlockType(
									new Block((int) (position.x + 0.25f), drop - 1, (int) (position.z - 0.25f))) != 0)
					|| (position.x - 0.25f >= 0 && position.z + 0.25f < 128 && chunk.getBlockType(
							new Block((int) (position.x - 0.25f), drop - 1, (int) (position.z + 0.25f))) != 0)))
			{
				drop--;
			}
	
			ground = drop;
		}
		else
		{
			// Hitting your head when jumping
			if ((int) Math.round(position.y) < 128
					&& (((int) position.x < 128 && (int) position.z < 128
							&& chunk.getBlockType(new Block((int) (position.x), (int) Math.round(position.y),
									(int) (position.z))) != 0)
					|| ((int) position.z < 128 && position.x + 0.25f < 128
							&& chunk.getBlockType(new Block((int) (position.x + 0.25f), (int) Math.round(position.y),
									(int) (position.z))) != 0)
					|| ((int) position.x < 128 && position.z + 0.25f < 128
							&& chunk.getBlockType(new Block((int) (position.x), (int) Math.round(position.y),
									(int) (position.z + 0.25f))) != 0)
					|| (position.x + 0.25f < 128 && position.z + 0.25f < 128
							&& chunk.getBlockType(new Block((int) (position.x + 0.25f), (int) Math.round(position.y),
									(int) (position.z + 0.25f))) != 0)
					|| ((int) position.z < 128 && position.x - 0.25f >= 0
							&& chunk.getBlockType(new Block((int) (position.x - 0.25f), (int) Math.round(position.y),
									(int) (position.z))) != 0)
					|| ((int) position.x < 128 && position.z - 0.25f >= 0
							&& chunk.getBlockType(new Block((int) (position.x), (int) Math.round(position.y),
									(int) (position.z - 0.25f))) != 0)
					|| (position.z - 0.25f >= 0 && position.x - 0.25f >= 0
							&& chunk.getBlockType(new Block((int) (position.x - 0.25f), (int) Math.round(position.y),
									(int) (position.z - 0.25f))) != 0)
					|| (position.x + 0.25f < 128 && position.z - 0.25f >= 0
							&& chunk.getBlockType(new Block((int) (position.x + 0.25f), (int) Math.round(position.y),
									(int) (position.z - 0.25f))) != 0)
					|| (position.x - 0.25f >= 0 && position.z + 0.25f < 128
							&& chunk.getBlockType(new Block((int) (position.x - 0.25f), (int) Math.round(position.y),
									(int) (position.z + 0.25f))) != 0)))
			{
				// Reposition and stop upward velocity
				height = (int) Math.ceil(position.y) - CAMERA_HEIGHT - 0.5f;
				velocity = 0;
			}
		}
	}
	
	public void move(Helper input, float multiplier)
	{
		Vector previousPosition = camera.getPosition();
		// Movement
		if (input.forward)
		{
			camera.moveForward(MOVE_SPEED * multiplier);
		}
		if (input.backward)
		{
			camera.moveForward(-MOVE_SPEED * multiplier);
		}
		if (input.left)
		{
			camera.strafeRight(-MOVE_SPEED * multiplier);
		}
		if (input.right)
		{
			camera.strafeRight(MOVE_SPEED * multiplier);
		}

		if (height != ground)
		{
			height += velocity * multiplier;
			velocity += GRAVITY * multiplier;

			if (height < ground)
			{
				height = ground;
				velocity = 0;
			}
			else if (height + CAMERA_HEIGHT > 128)
			{
				height = 128 - CAMERA_HEIGHT;
				velocity = 0;
			}

			camera.setPositionY(height + CAMERA_HEIGHT);
		}

		// Calculate the delta position
		deltaPosition = camera.getPosition().minus(previousPosition);

		// Orient the camera
		camera.pitch(input.lookDeltaY);
		camera.yaw(input.lookDeltaX);
	}
	
	public Camera getCamera()
	{
		return camera;
	}
}
