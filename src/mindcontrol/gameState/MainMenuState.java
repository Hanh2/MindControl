package mindcontrol.gameState;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import mindcontrol.library.GV;
import mindcontrol.library.JukeBox;
import mindcontrol.library.Keys;
import mindcontrol.managers.GameStateManager;

public class MainMenuState extends GameState
{
	// background
	// private Background bg;

	private int viewPortWidth = GV.VIEWPORTWIDTH;
	private int viewPortHeight = GV.VIEWPORTHEIGHT;

	private int currentChoice = 0;
	private String[] options = { "Start", "Controls", "Quit" };
	// private String[] options = { "Start", "Levels", "Controls", "Quit" };// enum?

	// title
	private Title title;

	// menu items
	private MenuItems mi;

	// controls img
	private BufferedImage controlsImg;
	private int cYOffset;
	private float cAlpha;
	private float dA;
	private float fadeSpeed = 0.1f;

	public MainMenuState(GameStateManager gsm)
	{
		this.gsm = gsm;

		init();
	}

	public void init()
	{
		// background
		// try
		// {
		// bg = new Background("/MenuBackground.png");
		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// }

		// title
		title = new Title();

		// menu items
		mi = new MenuItems();

		// controls
		try
		{
			controlsImg = ImageIO.read(getClass().getResourceAsStream("/MenuImages/Controls.png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		cAlpha = 0;
		dA = 0;
		cYOffset = title.getFontSize() + 32;
		
		// SFX
		JukeBox.load("/SFX/ChangeButton.mp3", "ChangeButton");
	}

	public void update()
	{
		if (cAlpha == 0)
		{
			if (Keys.keyState[Keys.ENTER] && !Keys.prevKeyState[Keys.ENTER])
			{
				select();
			}
			if (Keys.keyState[Keys.UP] && !Keys.prevKeyState[Keys.UP])
			{
				// play SFX
				JukeBox.play("ChangeButton");
				
				currentChoice--;
				if (currentChoice == -1)
					currentChoice = options.length - 1;
			}
			if (Keys.keyState[Keys.DOWN] && !Keys.prevKeyState[Keys.DOWN])
			{
				// play SFX
				JukeBox.play("ChangeButton");
				
				currentChoice++;
				if (currentChoice == options.length)
					currentChoice = 0;
			}
		}
		else if (cAlpha == 1)
		{
			// return if enter or esc is pressed
			if ((Keys.keyState[Keys.ENTER] && !Keys.prevKeyState[Keys.ENTER])
					|| (Keys.keyState[Keys.PAUSE] && !Keys.prevKeyState[Keys.PAUSE]))
			{
				dA = -fadeSpeed;
				cAlpha += dA;
			}
		}
		else
		{
			cAlpha += dA;

			if (cAlpha < 0)
			{
				cAlpha = 0;
				dA = 0;
			}
			else if (cAlpha > 1)
			{
				cAlpha = 1;
				dA = 0;
			}
		}
	}

	public void draw(Graphics2D g)
	{
		// draw background
		// bg.draw(g);

		// these two lines should later be replaced with a custom background.
		g.setColor(new Color(24, 24, 16));
		g.fillRect(0, 0, viewPortWidth, viewPortHeight);
		//

		// draw title
		title.draw(g);

		// draw menu options
		mi.draw(g);

		// draw controls
		if (cAlpha != 0)
		{
			Graphics2D gCopy = (Graphics2D) g.create();
			gCopy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cAlpha));
			gCopy.drawImage(controlsImg, viewPortWidth / 2, cYOffset, viewPortWidth / 2, viewPortHeight - cYOffset, null);
		}
	}

	private void select()
	{		
		switch (options[currentChoice])
		{
		case "Start":
			
			gsm.setState(GameStateManager.PUZZLESTATE);
			// start
			break;
		// case "Levels":
		// break;
		case "Controls":						
			dA = fadeSpeed;
			cAlpha += dA;							
			break;
		case "Quit":			
			System.exit(0);
			break;
		default:
		}
	}

	///////////////////
	// inner classes //
	///////////////////
	/**
	 * Handles the title.
	 * 
	 * @author denhanh
	 *
	 */
	private class Title
	{
		private String title = GV.GAME_NAME;
		private Color titleColor = new Color(0, 40, 20);;
		private Font titleFont = new Font("monospace", Font.BOLD, (int) (viewPortHeight / 5.5));
		private int titleXPos;
		private int titleYPos;

		private Title()
		{
			init();
		}

		private void init()
		{
			AffineTransform affinetransform = new AffineTransform();
			FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
			int titleDisplayWidth = (int) titleFont.getStringBounds(title, frc).getWidth();

			// title position
			int titleFontSize = titleFont.getSize();
			titleXPos = (viewPortWidth - titleDisplayWidth) / 2;
			titleYPos = titleFontSize;
		}

		private void draw(Graphics2D g)
		{
			g.setColor(titleColor);
			g.setFont(titleFont);
			g.drawString(title, titleXPos, titleYPos);
		}

		private int getFontSize()
		{
			return titleFont.getSize();
		}
	}

	/**
	 * Handles the menu items.
	 * 
	 * @author denhanh
	 *
	 */
	private class MenuItems
	{
		// font and positions
		private Font font;
		private int fontSize;
		private int[] menuItemXPos;
		private int menuItemsYOffset;

		// To make the current choice a bit bigger.
		private float choiceScaleFactor = 1.6f;
		private Font choiceFont;
		private int chFontSize;
		private Color choiceColor = Color.RED;
		private int[] choiceItemXPos;
		private int choiceItemsYOffset;

		private MenuItems()
		{
			init();
		}

		private void init()
		{
			AffineTransform affinetransform = new AffineTransform();
			FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

			// menu items
			font = new Font("Arial", Font.PLAIN, viewPortHeight / 10);
			fontSize = font.getSize();
			menuItemXPos = new int[options.length];
			for (int i = 0; i < options.length; i++)
			{
				menuItemXPos[i] = (int) ((viewPortWidth - font.getStringBounds(options[i], frc).getWidth()) / 2);
			}
			menuItemsYOffset = (viewPortHeight + title.getFontSize() - fontSize * (options.length - 1)) / 2;

			// choice item
			choiceFont = font.deriveFont(fontSize * choiceScaleFactor);
			chFontSize = choiceFont.getSize();
			choiceItemXPos = new int[options.length];
			for (int i = 0; i < options.length; i++)
			{
				choiceItemXPos[i] = (int) ((viewPortWidth - choiceFont.getStringBounds(options[i], frc).getWidth())
						/ 2);
			}
			choiceItemsYOffset = menuItemsYOffset + (chFontSize - fontSize) / 4;
		}

		private void draw(Graphics2D g)
		{
			g.setFont(font);

			for (int i = 0; i < options.length; i++)
			{
				if (i == currentChoice)
				{
					g.setFont(choiceFont);
					g.setColor(choiceColor);
					g.drawString(options[i], choiceItemXPos[i] - cAlpha * viewPortWidth / 4,
							choiceItemsYOffset + i * fontSize);
					g.setFont(font);
				}
				else
				{
					g.setColor(Color.DARK_GRAY);
					g.drawString(options[i], menuItemXPos[i] - cAlpha * viewPortWidth / 4,
							menuItemsYOffset + i * fontSize);
				}
			}
		}
	}
}
