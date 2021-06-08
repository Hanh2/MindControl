package mindcontrol.gameObjects.characters;

import java.awt.Graphics2D;
import java.util.ArrayList;

import mindcontrol.gameObjects.Elevator;
import mindcontrol.managers.PuzzleManager;
import mindcontrol.managers.Sprite;
import platformer2D.GameCharacter;

public abstract class ControllableCharacter extends GameCharacter
{
	// the puzzle manager handles all game objects in the map
	protected static PuzzleManager pm;

	// reference to elevators
	protected static ArrayList<Elevator> elevators;
	protected boolean inElevator;
	
	// Icon
	private Sprite icon;
	
	protected ControllableCharacter(String characterType)
	{
		super(characterType);
		
		icon = new Sprite("/Sprites/Characters/Icons/" + characterType + "_Icon.png");
	}
	
	public static void setPm(PuzzleManager pm)
	{
		ControllableCharacter.pm = pm;
		
		elevators = pm.getElevators();
	}

	public void drawIcon(int x, int y, Graphics2D g)
	{
		icon.draw(x, y, g);
	}

	public boolean isFacingRight()
	{
		return facingRight;
	}

	public void setInElevator(boolean b)
	{
		inElevator = b;
	}

}
