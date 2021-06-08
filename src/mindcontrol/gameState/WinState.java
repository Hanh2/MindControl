package mindcontrol.gameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import mindcontrol.library.GV;
import mindcontrol.library.Keys;
import mindcontrol.managers.GameStateManager;

public class WinState extends GameState
{
	GameStateManager gsm;

	private int viewPortWidth = GV.VIEWPORTWIDTH;
	private int viewPortHeight = GV.VIEWPORTHEIGHT;

	private String[] messages = {
		"Credits",
		"",
		"Game Design:",
		"Denhanh Huynh, Jerry Huynh",
		"",
		"Programmer:",
		"Denhanh Huynh",
		"",
		"Music:",
		"Shadowlands 4 - Breath Kevin MacLeod",
		"(incompetech.com)",
		"Licensed under Creative Commons: By Attribution 3.0",
		"License http://creativecommons.org/licenses/by/3.0/"
	};

	Font font;

	public WinState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
		init();
	}

	@Override
	public void init()
	{
		font = new Font("Arial", Font.PLAIN, viewPortHeight / 18);
	}

	@Override
	public void update()
	{
		if (Keys.keyState[Keys.ENTER] && !Keys.prevKeyState[Keys.ENTER])
		{
			gsm.setState(GameStateManager.MAINMENUSTATE);
		}
	}

	private void drawWinningScreen(Graphics2D g)
	{	
		g.clearRect(0, 0, viewPortWidth, viewPortHeight);
		g.setColor(Color.GREEN);
		g.setFont(font);

		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
		int numberOfRows = messages.length;
		int messagesYPos = (viewPortHeight - numberOfRows * font.getSize()) / 2;

		for (int i = 0; i < numberOfRows; i++) {
			String message = messages[i];
			int messageXPos = (viewPortWidth - (int) font.getStringBounds(message, frc).getWidth()) / 2;
			int messageYPos = messagesYPos + i * font.getSize();

			g.drawString(message, messageXPos, messageYPos);
		}
	}

	@Override
	public void draw(Graphics2D g)
	{
		drawWinningScreen(g);
	}

}
