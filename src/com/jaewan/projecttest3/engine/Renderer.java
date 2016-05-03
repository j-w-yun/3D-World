package com.jaewan.projecttest3.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.*;
import org.newdawn.slick.util.ResourceLoader;

import com.jaewan.projecttest3.props.Block;
import com.jaewan.projecttest3.util.Vector;

public class Renderer
{
	private static final int DEPTH_BUFFER_BITS = 24;
	private static final int DESIRED_SAMPLES = 8;
	
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	private static final String TITLE = "Project Test";

	private float renderDistance = 500;
	
//	private Texture dirtTexture;
//	private Texture grayTexture;
	private Texture blackTexture;
	
	private int numVerts;
	
	public Renderer()
	{
		try
		{
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setFullscreen(false);
			Display.setTitle(TITLE);
			Display.create(new PixelFormat(0, 24 /* DEPTH_BUFFER_BITS */, 0, 8 /* DESIRED_SAMPLES */));
//			Display.create();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		// Prepare OpenGL
//		glCullFace(GL_CCW);
		glEnable(GL_CULL_FACE); // back face culling
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST); // z-buffer
		glEnable(GL_TEXTURE_2D); // textures
		// Cross hair and selected block highlighting
		glLineWidth(2.f);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		// Background color
//		glClearColor(0.8f, 0.9f, 1.0f, 0.0f);
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		// Resize OpenGL
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(70, (float) Display.getWidth() / (float) Display.getHeight(), 0.25f, renderDistance);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		// Initialize data
		int bufferObjectID = ARBVertexBufferObject.glGenBuffersARB();
		// Vertex Data interleaved format: XYZST
		final int position = 3;
		final int texcoords = 2;
		final int sizeOfInt = 4; // 4 bytes in an int
		// Bind it
		final int vertexDataSize = (position + texcoords) * sizeOfInt;
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, bufferObjectID);
		// Interleaved vertex and texture pointers
		// (Number of positions [x,y,z], GL_TYPE, Stride, Start of stride)
		glVertexPointer(3, GL_INT, vertexDataSize, 0);
		glTexCoordPointer(2, GL_INT, vertexDataSize, position * sizeOfInt);
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		
		// Load textures
		try
		{
//			dirtTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/dirt.png"));
//			grayTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/gray.png"));
			blackTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/black.png"));
		} catch (IOException ioe)
		{
			// Blockworld.LOGGER.log(Level.WARNING, ioe.toString(), ioe);
		}
		// Texture parameters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); //GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// It will stay bound
		blackTexture.bind();
		
		// Light properties
		glShadeModel(GL_SMOOTH);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		FloatBuffer ambientLight = BufferUtils.createFloatBuffer(4);
		ambientLight.put(0.8f).put(0.8f).put(0.8f).put(1).flip();
		glLightModel(GL_LIGHT_MODEL_AMBIENT, ambientLight);
		glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
		glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_RESCALE_NORMAL);
		
		// Fog
		glEnable(GL_FOG);
        FloatBuffer fogColours = BufferUtils.createFloatBuffer(4);
        fogColours.put(new float[]{0.8f, 0.9f, 1.0f, 0.0f});
//        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        fogColours.flip();
        glFog(GL_FOG_COLOR, fogColours);
        glFogi(GL_FOG_MODE, GL_LINEAR);
        glHint(GL_FOG_HINT, GL_NICEST);
        glFogf(GL_FOG_START, 6);
        glFogf(GL_FOG_END, 300);
        glFogf(GL_FOG_DENSITY, 0.005f);
	}
	
	public void render(Game state)
	{
		// Clear color and z buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Load the identity matrix
		glLoadIdentity();
		// Let the Camera calculate the view matrix
		state.getPlayerView().updateMatrix();
		// Full brightness for textures
		glColor3b((byte) 127, (byte) 127, (byte) 127);

		// Start at 1 to avoid drawing 1st degenerate vertex and messing
		// everything else up
		glDrawArrays(GL_TRIANGLE_STRIP, 1, numVerts); //(GL_TRIANGLE_STRIP, 1, numVerts);

		// Black lines
		glColor3b((byte) -127, (byte) -127, (byte) -127);

		// Draw selected block outline highlight
		if (state.isBlockSelected())
		{
			Vector selectedBlock = openGLCoordinatesForBlock(state.getSelectedBlock());

			// Just use immediate mode/fixed function pipeline
			glBegin(GL_LINE_STRIP);
			
			glVertex3f(selectedBlock.x,		selectedBlock.y,		selectedBlock.z);
			glVertex3f(selectedBlock.x + 1, selectedBlock.y,		selectedBlock.z);
			glVertex3f(selectedBlock.x + 1, selectedBlock.y + 1,	selectedBlock.z);
			glVertex3f(selectedBlock.x,		selectedBlock.y + 1,	selectedBlock.z);
			glVertex3f(selectedBlock.x,		selectedBlock.y,		selectedBlock.z);
			glVertex3f(selectedBlock.x,		selectedBlock.y,		selectedBlock.z - 1);
			glVertex3f(selectedBlock.x + 1, selectedBlock.y,		selectedBlock.z - 1);
			glVertex3f(selectedBlock.x + 1, selectedBlock.y + 1,	selectedBlock.z - 1);
			glVertex3f(selectedBlock.x,		selectedBlock.y + 1,	selectedBlock.z - 1);
			glVertex3f(selectedBlock.x,		selectedBlock.y,		selectedBlock.z - 1);
			
			glEnd();
			
			// Next draw
			
			glBegin(GL_LINES);
			
			glVertex3f(selectedBlock.x,		selectedBlock.y + 1,	selectedBlock.z);
			glVertex3f(selectedBlock.x,		selectedBlock.y + 1,	selectedBlock.z - 1);

			glVertex3f(selectedBlock.x + 1, selectedBlock.y + 1,	selectedBlock.z);
			glVertex3f(selectedBlock.x + 1, selectedBlock.y + 1,	selectedBlock.z - 1);

			glVertex3f(selectedBlock.x + 1, selectedBlock.y,		selectedBlock.z);
			glVertex3f(selectedBlock.x + 1, selectedBlock.y,		selectedBlock.z - 1);
			
			glEnd();
		}

//		// Reload identity matrix
//		glLoadIdentity();
//		// Draw crosshair
//		glBegin(GL_LINES);
//		glVertex3f(-CROSSHAIR_SIZE / 2, 0, -0.25f);
//		glVertex3f(CROSSHAIR_SIZE / 2, 0, -0.25f);
//		glVertex3f(0, -CROSSHAIR_SIZE / 2, -0.25f);
//		glVertex3f(0, CROSSHAIR_SIZE / 2, -0.25f);
//		glEnd();

		// Update
		Display.update();
//		Display.sync(60);
	}
	
	public void gameStateChunkChanged(Chunk chunk)
	{
		byte[][][] data = chunk.getData();
		IntBuffer vertexData = BufferUtils.createIntBuffer((data.length) * (data[0].length) * (data[0][0].length) * 50);

		try
		{
			for (int x = 0; x < data.length; x++)
			{
				for (int y = 0; y < data[x].length; y++)
				{
					for (int z = 0; z < data[x][y].length; z++)
					{
						if (data[x][y][z] == 1 && checkExposedTop(data, x, y, z))// && checkExposedSide(data, x, y, z))
						{
							vertexData.put(cubeData(x, y, -z));
						}
						else if(data[x][y][z] == 2 && checkExposedTop(data, x, y, z))
						{
							vertexData.put(cubeData(x, y, -z));
						}
						else if(data[x][y][z] == 3 && checkExposedTop(data, x, y, z))
						{
							vertexData.put(cubeData(x, y, -z));
						}
					}
				}
			}
		}
		catch (BufferOverflowException boe1)
		{
			boe1.printStackTrace();
			System.exit(1);
		}

		numVerts = vertexData.position() / 5;
		vertexData.flip();
		
		// Upload data
		ARBVertexBufferObject.glBufferDataARB(
				ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
				vertexData,
				ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB);
	}
	
	// Vertex Data interleaved format: XYZST
	private int[] cubeData(int x, int y, int z)
	{
		return new int[]
			{ // 23*5 ints
				x,		y,		z,		0, 0, // degenerate
				x,		y,		z,		1, 0,
				x + 1,	y,		z,		1, 1,
				x,		y + 1,	z,		0, 0,	// *degenerate
				x + 1,	y + 1,	z,		0, 1,
				x,		y + 1,	z - 1,	1, 0,
				x + 1,	y + 1,	z - 1,	1, 1,
				x,		y,		z - 1,	0, 0,
				x + 1,	y,		z - 1,	0, 1,
				x,		y,		z,		1, 0,
				x + 1,	y,		z,		1, 1,
				x + 1,	y,		z,		0, 0, // degenerate
				x + 1,	y,		z,		0, 0, // degenerate
				x + 1,	y,		z,		0, 1,
				x + 1,	y,		z - 1,	1, 1,
				x + 1,	y + 1,	z,		0, 0,	// *degenerate
				x + 1,	y + 1,	z - 1,	1, 0,
				x + 1,	y + 1,	z - 1,	0, 0, // degenerate
				x,		y + 1,	z - 1,	0, 0, // degenerate
				x,		y + 1,	z - 1,	0, 0,
				x,		y,		z - 1,	0, 1,
				x,		y + 1,	z,		1, 0,
				x,		y,		z,		1, 1,
				x,		y,		z,		0, 0, // degenerate
			};
	}
	
	public static Vector openGLCoordinatesForBlock(Block block)
	{
		return new Vector(block.x, block.y, -block.z);
	}
	
	// Return true if exposed 
	private synchronized boolean checkExposedTop(byte[][][] data, int x, int y, int z)
	{	
		try
		{
			if(data[x + 1][y][z] == 0)
			{
				return true;
			}
			if(data[x - 1][y][z] == 0)
			{
				return true;
			}
			if(data[x][y][z + 1] == 0)
			{
				return true;
			}
			if(data[x][y][z - 1] == 0)
			{
				return true;
			}
			if(data[x][y + 1][z] == 0)
			{
				return true;
			}
			if(data[x][y - 1][z] == 0)
			{
				return true;
			}
		}
		catch(Exception e)
		{
			return true;
		}
		return false;
	}
}
