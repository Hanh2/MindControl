package mindcontrol.library;

import java.awt.event.KeyEvent;

public class Keys
{
	public static final int NUM_KEYS = 12;

	public static boolean keyState[] = new boolean[NUM_KEYS];
	public static boolean prevKeyState[] = new boolean[NUM_KEYS];

	// ENUM?
	public static int UP = 0;
	public static int DOWN = 1;
	public static int LEFT = 2;
	public static int RIGHT = 3;
	public static int JUMP = 4;

	public static int MINDCONTROL = 5;
	public static int ACTION = 6;
	
	public static int PAUSE = 7;
	public static int ENTER = 8;

	public static void keySet(int key, boolean state)
	{	
		switch (key)
		{
		case KeyEvent.VK_UP:
			keyState[UP] = state;
			break;
		case KeyEvent.VK_DOWN:
			keyState[DOWN] = state;
			break;
		case KeyEvent.VK_LEFT:
			keyState[LEFT] = state;
			break;
		case KeyEvent.VK_RIGHT:
			keyState[RIGHT] = state;
			break;
		case KeyEvent.VK_SPACE:
			keyState[JUMP] = state;
			break;
		case KeyEvent.VK_CONTROL:
			keyState[MINDCONTROL] = state;
			break;
		case KeyEvent.VK_SHIFT:
			keyState[ACTION] = state;
			break;
		case KeyEvent.VK_ESCAPE:
			keyState[PAUSE] = state;
			break;
		case KeyEvent.VK_ENTER:
			keyState[ENTER] = state;
			break;
		}
	}

	public static void update()
	{
		prevKeyState = keyState.clone();
	}
}
