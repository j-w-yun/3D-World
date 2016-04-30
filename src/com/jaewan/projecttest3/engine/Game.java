package com.jaewan.projecttest3.engine;

import com.jaewan.projecttest3.props.Block;
import com.jaewan.projecttest3.props.Player;
import com.jaewan.projecttest3.util.Camera;
import com.jaewan.projecttest3.util.Helper;
import com.jaewan.projecttest3.util.Vector;

public class Game
{
	private static final float ARM = 6;
	
	public Renderer listener = null;
	
	private Player player = new Player();
	
	private Chunk chunk = null;
	
	private Block selectedBlock = null;
	
	private Block newBlock = null;
	
	public Game(Renderer listener)
	{
		this.listener = listener;
		chunk = new Chunk();
		listener.gameStateChunkChanged(chunk);
	}
	
	public void update(Helper input, float deltaTime)
	{
		float multiplier = deltaTime / (100.f / 6.f);
		player.move(input, multiplier);
		player.collision(chunk);
		if(input.jump)
			player.jump();
		calculateSelectedBlock(chunk);
		if(selectedBlock != null && newBlock != null)
		{
			if(input.breakBlock)
			{
				chunk.setBlockType(selectedBlock, (byte) 0);
				listener.gameStateChunkChanged(chunk);
			}
			else if(input.placeBlock)
			{
				chunk.setBlockType(newBlock, (byte) 1);
				listener.gameStateChunkChanged(chunk);
			}
		}
	}
	
	public void calculateSelectedBlock(Chunk chunk)
	{
		Vector position = player.getCamera().getPosition();
		Vector sight = player.getCamera().getSight();

		Vector ray; // Vector cast out from the players position to find a block
		Vector step; // step to increment ray by

		selectedBlock = null;
		newBlock = null;
		
		float frontBackDistSquared = Float.MAX_VALUE;
		if (sight.z != 0)
		{
			// Calculate ray and step depending on look direction
			if (sight.z > 0)
				ray = position.plus(sight.scaled((float) (Math.ceil(position.z) - position.z) / sight.z));
			else
				ray = position.plus(sight.scaled((float) (Math.floor(position.z) - position.z) / sight.z));
			step = sight.scaled(Math.abs(1.f / sight.z));

			// Do the first step already if z == 128 to prevent an
			// ArrayIndexOutOfBoundsException
			if (ray.z == 128)
				ray.add(step);

			while (ray.x >= 0 && ray.x < 128 && ray.y >= 0 && ray.y < 128 && ray.z >= 0 && ray.z < 128)
			{
				// Give up if we've extended the ray longer than the Player's
				// arm length
				float distSquared = ray.minus(position).magnitudeSquared();
				if (distSquared > ARM * ARM)
					break;

				if (sight.z > 0)
				{
					if (chunk.getBlockType(new Block((int) ray.x, (int) ray.y, (int) ray.z)) != 0)
					{
						selectedBlock = new Block((int) ray.x, (int) ray.y, (int) ray.z);
						if (selectedBlock.z - 1 >= 0)
						{
							newBlock = new Block(selectedBlock.x, selectedBlock.y, selectedBlock.z - 1);
							if (chunk.getBlockType(newBlock) != 0)
								newBlock = null;
						}

						frontBackDistSquared = distSquared;
						break;
					}
				} else
				{
					if (ray.z - 1 >= 0 && chunk.getBlockType(new Block((int) ray.x, (int) ray.y, (int) ray.z - 1)) != 0)
					{
						selectedBlock = new Block((int) ray.x, (int) ray.y, (int) ray.z - 1);
						if (selectedBlock.z + 1 < 128)
						{
							newBlock = new Block(selectedBlock.x, selectedBlock.y, selectedBlock.z + 1);
							if (chunk.getBlockType(newBlock) != 0)
								newBlock = null;
						}

						frontBackDistSquared = distSquared;
						break;
					}
				}
				ray.add(step);
			}
		}

		// YZ plane (left and right faces)
		float leftRightDistSquared = Float.MAX_VALUE;
		if (sight.x != 0)
		{
			if (sight.x > 0)
				ray = position.plus(sight.scaled((float) (Math.ceil(position.x) - position.x) / sight.x));
			else
				ray = position.plus(sight.scaled((float) (Math.floor(position.x) - position.x) / sight.x));
			step = sight.scaled(Math.abs(1.f / sight.x));

			if (ray.x == 128)
				ray.add(step);

			while (ray.x >= 0 && ray.x < 128 && ray.y >= 0 && ray.y < 128 && ray.z >= 0 && ray.z < 128)
			{
				float distSquared = ray.minus(position).magnitudeSquared();
				if (distSquared > ARM * ARM || distSquared > frontBackDistSquared)
					break;

				if (sight.x > 0)
				{
					if (chunk.getBlockType(new Block((int) ray.x, (int) ray.y, (int) ray.z)) != 0)
					{
						selectedBlock = new Block((int) ray.x, (int) ray.y, (int) ray.z);
						if (selectedBlock.x - 1 >= 0)
						{
							newBlock = new Block(selectedBlock.x - 1, selectedBlock.y, selectedBlock.z);
							if (chunk.getBlockType(newBlock) != 0)
								newBlock = null;
						}

						leftRightDistSquared = distSquared;
						break;
					}
				} else
				{
					if (ray.x - 1 >= 0 && chunk.getBlockType(new Block((int) ray.x - 1, (int) ray.y, (int) ray.z)) != 0)
					{
						selectedBlock = new Block((int) ray.x - 1, (int) ray.y, (int) ray.z);
						if (selectedBlock.x + 1 < 128)
						{
							newBlock = new Block(selectedBlock.x + 1, selectedBlock.y, selectedBlock.z);
							if (chunk.getBlockType(newBlock) != 0)
								newBlock = null;
						}

						leftRightDistSquared = distSquared;
						break;
					}
				}
				ray.add(step);
			}
		}

		// XZ plane (bottom and top faces)
//		float bottomTopDistSquared = Float.MAX_VALUE;
		if (sight.y != 0)
		{
			if (sight.y > 0)
				ray = position.plus(sight.scaled((float) (Math.ceil(position.y) - position.y) / sight.y));
			else
				ray = position.plus(sight.scaled((float) (Math.floor(position.y) - position.y) / sight.y));
			step = sight.scaled(Math.abs(1.f / sight.y));

			if (ray.y == 128)
				ray.add(step);

			while (ray.x >= 0 && ray.x < 128 && ray.y >= 0 && ray.y < 128 && ray.z >= 0 && ray.z < 128)
			{
				float distSquared = ray.minus(position).magnitudeSquared();
				if (distSquared > ARM * ARM || distSquared > frontBackDistSquared
						|| distSquared > leftRightDistSquared)
					break;

				if (sight.y > 0)
				{
					if (chunk.getBlockType(new Block((int) ray.x, (int) ray.y, (int) ray.z)) != 0)
					{
						selectedBlock = new Block((int) ray.x, (int) ray.y, (int) ray.z);
						if (selectedBlock.y - 1 >= 0)
						{
							newBlock = new Block(selectedBlock.x, selectedBlock.y - 1, selectedBlock.z);
							if (chunk.getBlockType(newBlock) != 0)
								newBlock = null;
						}

//						bottomTopDistSquared = distSquared;
						break;
					}
				} else
				{
					if (ray.y - 1 >= 0 && chunk.getBlockType(new Block((int) ray.x, (int) ray.y - 1, (int) ray.z)) != 0)
					{
						selectedBlock = new Block((int) ray.x, (int) ray.y - 1, (int) ray.z);
						if (selectedBlock.y + 1 < 128)
						{
							newBlock = new Block(selectedBlock.x, selectedBlock.y + 1, selectedBlock.z);
							if (chunk.getBlockType(newBlock) != 0)
								newBlock = null;
						}

//						bottomTopDistSquared = distSquared;
						break;
					}
				}
				ray.add(step);
			}
		}
	}
	
	public boolean isBlockSelected()
	{
		return selectedBlock != null;
	}
	
	public Block getSelectedBlock()
	{
		return selectedBlock;
	}
	
	public Camera getPlayerView()
	{
		return player.getCamera();
	}
}
