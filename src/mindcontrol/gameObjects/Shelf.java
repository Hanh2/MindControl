package mindcontrol.gameObjects;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import mindcontrol.managers.Sprite;
import platformer2D.GameObject;

public class Shelf extends GameObject
{
	// sprite
	private Sprite shelf;

	public Shelf(int x, int y)
	{
		this.x = x;
		this.y = y;

		init();
	}

	private void init()
	{
		shelf = new Sprite("/Sprites/Map_Objects/Shelf/Shelf.png");

		int hbYOffset = 2;
		int hbWidth = 4 * tileSize;
		int hbHeight = 3 * tileSize - hbYOffset;

		setHitbox((int) x, (int) y + hbYOffset, hbWidth, hbHeight);
	}

	@Override
	public void draw(Graphics2D g)
	{
		shelf.draw((int) x, (int) y, g);
	}

	public void addContent(String content, int position)
	{
		// only positions 0-3 exists and are defined as:
		// 01
		// 23
		if (position < 0 || position > 3)
			System.out.println(
					"Invalid position (position: " + position + "); Method: Shelf.addContent(content, position)");

		BufferedImage contentImg = null;

		try
		{
			contentImg = ImageIO.read(getClass().getResourceAsStream("/Sprites/Map_Objects/Shelf/" + content + ".png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		int sWidth = shelf.getWidth();

		// offsets
		int xOffset = (sWidth - 2 * contentImg.getWidth()) / 4;
		int yOffset = 43 - contentImg.getHeight();
		if (position / 2 == 1) // if position 2 or 3
		{
			int yIncrement = 40;
			yOffset += yIncrement;
		}
		if (position % 2 == 1) // if position 1 or 3
		{
			int xIncrement = sWidth / 2;
			xOffset += xIncrement;
		}

		shelf.addImageToSprite(contentImg, xOffset, yOffset);
	}

	public BufferedImage getImage()
	{
		return shelf.getBufferedImage();
	}
	
	public boolean landsOnRoof(Rectangle hb, int dy)
	{
		return JumpThroughPlatform.landsOn(getHbLeftX(), getHbTopY(), getHbWidth(), hb, dy);
	}
}
