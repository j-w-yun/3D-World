package com.jaewan.projecttest3.engine;

import com.jaewan.projecttest3.props.Block;
import com.jaewan.projecttest3.util.Vector;

public class Chunk
{
	private Vector position = null;
	
	private byte[][][] data = new byte[128][128][128];
	
	{
		for(int x = 0; x < 128; x++)
		{
			for(int z = 0; z < 128; z++)
			{
				for (int y = 0; y < (int)(10* (Math.pow((Math.E), ((x + z) / 120.0)))) + 11; y++)
				{
					data[x][y][z] = 1;
				}
			}
		}
	}
	
	public byte[][][] getData()
	{
		return data;
	}
	
	public void setBlockType(Block block, byte type)
	{
		data[block.x][block.y][block.z] = type;
	}
	
	public byte getBlockType(Block block)
	{
		return data[block.x][block.y][block.z];
	}
	
	public void movePosition(Vector vector)
	{
		position.add(vector);
	}
}
