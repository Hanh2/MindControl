package mindcontrol.managers;

import java.awt.Graphics2D;

import mindcontrol.gameObjects.characters.ControllableCharacter;
import mindcontrol.gameObjects.characters.Player;
import mindcontrol.library.GV;

/**
 * The heads up display.
 * 
 * @author denhanh
 *
 */
public class HUD
{
	// reference to the target character
	ControllableCharacter targetCharacter;
	
	// reference to player
	Player p1;

	// view port size
	private int halfViewPortWidth = GV.VIEWPORTWIDTH / 2;
	private int halfViewPortHeight = GV.VIEWPORTHEIGHT / 2;

	// icon
	private int iconXOffset = 10, yOffset = 10;

	// keys
	private Sprite key;
	private int width, height;
	private int prevNumOfKeys;
	private final int keysXOffset = 52; // iconXOffset + iconWidth + gap = 10 + 32 + 10;
	private int xIncrement;

	// new key
	private int newKeyRatio = 5;
	private int newKeyX, newKeyY, newKeyStartX, newKeyStartY;

	// animation timer
	private int timer;
	private int interval = 1 * GV.FPS;

	public HUD()
	{
		init();
	}

	private void init()
	{
		key = new Sprite("/Sprites/Map_Objects/Holograms/Hologram_Key.png", 0, 0, 30, 15);
		width = key.getWidth();
		height = key.getHeight();
		xIncrement = width + keysXOffset / 2;

		newKeyStartX = halfViewPortWidth - width * newKeyRatio / 2;
		newKeyStartY = halfViewPortHeight - height * newKeyRatio / 2;
	}

	public void setPlayer(Player p1)
	{
		this.p1 = p1;
	}

	public void draw(Graphics2D g)
	{
		if (timer <= 0)
		{
			int numOfKeys = p1.getNumOfKeys();

			if (numOfKeys > prevNumOfKeys)
			{
				timer = interval;
				newKeyX = newKeyStartX;
				newKeyY = newKeyStartY;
			}
			else if (numOfKeys < prevNumOfKeys)
			{
				prevNumOfKeys = numOfKeys;
			}

			// draw keys
			for (int i = 0; i < prevNumOfKeys; i++)
			{
				key.draw(keysXOffset + i * xIncrement, yOffset, g);
			}
		}
		else if (timer > 0)
		{
			// draw previous number of keys
			for (int i = 0; i < prevNumOfKeys; i++)
			{
				key.draw(keysXOffset + i * xIncrement, yOffset, g);
			}

			// draw new key animation
			if (timer > interval / 2)
			{
				key.draw(newKeyX, newKeyY, width * newKeyRatio, height * newKeyRatio, g);
			}
			else
			{
				double factor = ((double) timer / (interval / 2));
				factor = Math.sqrt(Math.sqrt(factor));
				double keyRatio = (1 + (newKeyRatio - 1) * factor);

				key.draw(newKeyX, newKeyY, (int) (width * keyRatio), (int) (height * keyRatio), g);

				newKeyX = (int) (keysXOffset + (newKeyStartX - keysXOffset) * factor);
				newKeyY = (int) (yOffset + (newKeyStartY - yOffset) * factor);
			}

			timer--;
			if (timer == 0)
				prevNumOfKeys = p1.getNumOfKeys();
		}
		
		targetCharacter.drawIcon(iconXOffset, yOffset, g);
	}
	
	public void setTargetCharacter(ControllableCharacter targetCharacter)
	{
		this.targetCharacter = targetCharacter;
	}
}