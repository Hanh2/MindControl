package mindcontrol.managers;

import mindcontrol.gameState.*;
import mindcontrol.library.GV;
import mindcontrol.library.JukeBox;

public class GameStateManager
{
	private GameState[] gameStates;
	private int currentState;
	private int currentLevel;
	private int numberOfLevels = GV.NUMBEROFLEVELS;

	// ENUM?
	public static final int NUMOFGAMESTATES = 3;
	public static final int MAINMENUSTATE = 0;
	public static final int PUZZLESTATE = 1;
	public static final int WINSTATE = 2;

	public GameStateManager()
	{
		gameStates = new GameState[NUMOFGAMESTATES];

		currentState = MAINMENUSTATE;
		currentLevel = GV.STARTLEVEL;

		JukeBox.init();

		loadState(currentState);
	}

	private void loadState(int state)
	{
		if (state == MAINMENUSTATE)
		{
			gameStates[state] = new MainMenuState(this);
		}
		else if (state == PUZZLESTATE)
		{
			// Background ingame music
			JukeBox.load("/Music/Breath.mp3", "bgm1");
			JukeBox.loop("bgm1");
			gameStates[state] = new PuzzleState(this);
		}
		else if (state == WINSTATE)
		{
			gameStates[state] = new WinState(this);
		}
	}

	private void unloadState(int state)
	{
		gameStates[state] = null;
	}

	public int getCurrentLevel()
	{
		return currentLevel;
	}

	public void nextLevel()
	{
		currentLevel++;

		if (currentLevel <= numberOfLevels)
		{
			gameStates[currentState] = new PuzzleState(this);
		}
		else
		{
			setState(WINSTATE);

			currentLevel = 1;
		}
	}

	public void setState(int state)
	{
		if (currentState == PUZZLESTATE)
			JukeBox.stop("bgm1");
		
		unloadState(currentState);
		currentState = state;
		loadState(currentState);
	}

	public void update()
	{
		gameStates[currentState].update();
	}

	public void draw(java.awt.Graphics2D g)
	{
		gameStates[currentState].draw(g);}

}
