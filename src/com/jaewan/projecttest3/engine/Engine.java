package com.jaewan.projecttest3.engine;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.jaewan.projecttest3.util.Helper;

enum MouseB
{
	LEFT, RIGHT
}

public class Engine
{
	private static final float MAX_DELTA_TIME = 50.0f;
	
	private Renderer renderer;
	
	private Game state;
	
	public boolean SBD = false;
	
	private boolean LMBD = false;
	
	private boolean RMBD = false;
	
	private double previousTime = 0.0;
	
	public Engine()
	{
		renderer = new Renderer();
		state = new Game(renderer);
		
		try
		{
			Keyboard.create();
			Mouse.setGrabbed(true);
			Mouse.create();			
		}
		catch(Exception e) {}
	}
	
	public void destroy()
	{
		Mouse.destroy();
		Keyboard.destroy();
		Display.destroy();
	}
	
	// Game Loop
	private float getDeltaTime()
	{
		double newTime = (Sys.getTime() * 1000.0) / Sys.getTimerResolution();
		float delta = (float) (newTime - previousTime);
		previousTime = newTime;
		if (delta < MAX_DELTA_TIME)
			return delta;
		else
			return MAX_DELTA_TIME;
	}
	
	private boolean wasMouseClicked(MouseB button)
	{
		boolean buttonDown = Mouse.isButtonDown(button.ordinal());
		boolean clicked = false;
		
		if(button == MouseB.LEFT)
		{
			clicked = !LMBD && buttonDown;
			LMBD = buttonDown;
		}
		else if(button == MouseB.RIGHT)
		{
			clicked = !RMBD && buttonDown;
			RMBD = buttonDown;
		}
		
		return clicked;
	}
	
	private boolean wasSpaceBarPressed()
	{
		boolean spaceBarDown = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		boolean wasPressed = !SBD && spaceBarDown;
		SBD = spaceBarDown;
		return wasPressed;
	}
	
	public void run()
	{
		// FPS counter
		long currentTime = 0;
		long lastTime = 0;
		int frames = 0;
		
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			// FPS counter
			currentTime = (long)(System.nanoTime() / 1000000000);
			if(currentTime != lastTime)
			{
				System.out.println(frames);
				frames = 0;
			}
			lastTime = currentTime;
			
			if (Display.isVisible())
			{
				// Update the state with the required input
				state.update(
						new Helper(
								Keyboard.isKeyDown(Keyboard.KEY_W),
								Keyboard.isKeyDown(Keyboard.KEY_S),
								Keyboard.isKeyDown(Keyboard.KEY_A),
								Keyboard.isKeyDown(Keyboard.KEY_D),
								wasSpaceBarPressed(),
								Mouse.getDX(),
								Mouse.getDY(),
								Mouse.getDWheel() / -120,
								wasMouseClicked(MouseB.LEFT),
								wasMouseClicked(MouseB.RIGHT)),
						getDeltaTime());
				// Render it
				renderer.render(state);
				
				frames++;
			}
			else
			{
				// Only render if it needs rendering
				if (Display.isDirty())
				{
					renderer.render(state);
				}
//				try
//				{
//					// If the window isn't visible, sleep a bit so that we're
//					// not wasting resources by checking nonstop.
//					Thread.sleep(10);
//				}
//				catch (InterruptedException e)
//				{
//				}
			}
		}
	}
}
