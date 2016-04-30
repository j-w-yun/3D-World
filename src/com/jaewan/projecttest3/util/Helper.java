package com.jaewan.projecttest3.util;

public class Helper
{
	private static final float SENSITIVITY = 1.0f / 10.0f;
	private static float lookSensitivity = SENSITIVITY;
	
	public final boolean forward;
	public final boolean backward;
	public final boolean left;
	public final boolean right;
	
	public final boolean jump;
	
	public final float lookDeltaX;
	public final float lookDeltaY;
	
	public final int cycleBlock;
	
	public final boolean breakBlock;
	public final boolean placeBlock;
	
	public Helper(boolean forward, boolean backward, boolean left, boolean right, boolean jump, float lookDeltaX, float lookDeltaY, int cycleBlock, boolean breakBlock, boolean placeBlock)
	{
		this.forward = forward;
		this.backward = backward;
		this.left = left;
		this.right = right;
		this.jump = jump;
		this.lookDeltaX = lookDeltaX * lookSensitivity;
		this.lookDeltaY = lookDeltaY * lookSensitivity;
		this.cycleBlock = cycleBlock;
		this.breakBlock = breakBlock;
		this.placeBlock = placeBlock;
	}
	
	// Settings
	static void setLookSensitivity(float lookSensitivity)
	{
		Helper.lookSensitivity = lookSensitivity * SENSITIVITY;
	}
}
