package com.jaewan.projecttest3;

import com.jaewan.projecttest3.engine.Engine;

public class Sand_in_a_Box
{
	private static Engine controller;
	public static void main(String[] args)
	{
		try
		{
			controller = new Engine();
			controller.run();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		controller.destroy();
	}
}
