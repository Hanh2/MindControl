package mindcontrol.managers;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Sprite
{
	private BufferedImage sprite;

	// size
	private int width;
	private int height;

	/**
	 * Creates a sprite.
	 * 
	 * @param spriteLocation
	 *            the location of the image
	 */
	public Sprite(String spriteLocation)
	{
		try
		{
			sprite = ImageIO.read(getClass().getResourceAsStream(spriteLocation));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		this.width = sprite.getWidth();
		this.height = sprite.getHeight();
	}

	/**
	 * Creates a sprite and adds a second image onto the main sprite image at
	 * location (x, y).
	 * 
	 * @param spriteLocation
	 *            the location of the main image
	 * @param spriteLocation2
	 *            the location of the second image
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 */
	public Sprite(String spriteLocation, String spriteLocation2, int x, int y)
	{
		// create sprite
		this(spriteLocation);

		// get second image
		BufferedImage secondImg = null;
		try
		{
			secondImg = ImageIO.read(getClass().getResourceAsStream(spriteLocation2));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		int secImgWidth = secondImg.getWidth();
		int secImgHeight = secondImg.getHeight();

		// check if the second image is entirely contained in the sprite image and if so
		// add the image
		if (x > 0 && x + secImgWidth < width && y > 0 && y + secImgHeight < height)
		{
			addImageToSprite(secondImg, x, y);
		}
		else // otherwise first make a larger image and then add the second image
		{
			BufferedImage tempImg = sprite;
			
			// calculate the new image size and the position of the images
			int spriteX = 0, spriteY = 0;
			if (x < 0)
			{
				spriteX = -x;
				x = 0;
				width += spriteX;
			}
			else if (x + secImgWidth > width)
			{
				width = x + secImgWidth;
			}
			if (y < 0)
			{
				spriteY = -y;
				y = 0;
				height += spriteY;
			}
			else if (y + secImgHeight > height)
			{
				height = y + secImgHeight;
			}
			
			// create new buffered image and graphics for that image
			sprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = sprite.createGraphics();
			
			// clear background
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, width, height);
			g.setComposite(AlphaComposite.Src);
			
			// draw images onto the new sprite
			g.drawImage(tempImg, spriteX, spriteY, null);
			g.drawImage(secondImg, x, y, null);
			
			// dispose graphics
			g.dispose();
		}
	}

	/**
	 * Creates a sprite from a sprite sheet by defining a rectangular region of the
	 * sprite sheet.
	 * 
	 * @param spriteSheetLocation
	 * @param x
	 *            the X coordinate of the upper-left corner of the specified
	 *            rectangular region
	 * @param y
	 *            the Y coordinate of the upper-left corner of the specified
	 *            rectangular region
	 * @param width
	 *            the width of the sprite
	 * @param height
	 *            the height of the sprite
	 */
	public Sprite(String spriteSheetLocation, int x, int y, int width, int height)
	{
		this.width = width;
		this.height = height;
		
		BufferedImage spriteSheet = null;
		try
		{
			spriteSheet = ImageIO.read(getClass().getResourceAsStream(spriteSheetLocation));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		sprite = spriteSheet.getSubimage(x, y, width, height);
	}

	public void draw(int x, int y, boolean facingRight, Graphics2D g)
	{
		if (facingRight)
			g.drawImage(sprite, x, y, null);
		else
			g.drawImage(sprite, x + width, y, -width, height, null);
	}

	public void draw(int x, int y, Graphics2D g)
	{
		g.drawImage(sprite, x, y, null);
	}

	public void draw(int x, int y, int width, int height, Graphics2D g)
	{
		g.drawImage(sprite, x, y, width, height, null);
	}

	/**
	 * Adds an image to this sprite at position (x, y).
	 * 
	 * @param img
	 *            the image to be added
	 * @param x
	 *            the X coordinate of the position to add the image
	 * @param y
	 *            the Y coordinate of the position to add the image
	 */
	public void addImageToSprite(BufferedImage img, int x, int y)
	{
		// create graphics
		Graphics2D g = sprite.createGraphics();

		// draw
		g.drawImage(img, x, y, null);

		// dispose
		g.dispose();
	}

	public void flipSprite()
	{
		// copy sprite
		BufferedImage spriteCopy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = spriteCopy.createGraphics();
		g.drawImage(sprite, 0, 0, null);
		g.dispose();

		// create graphics for sprite
		Graphics2D g2 = sprite.createGraphics();

		// clear img
		g2.setComposite(AlphaComposite.Clear);
		g2.fillRect(0, 0, width, height);
		g2.setComposite(AlphaComposite.Src);

		// draw flipped image onto img
		g2.drawImage(spriteCopy, width, 0, -width, height, null);

		// dispose g2
		g2.dispose();
	}

	public Graphics2D createGraphics()
	{
		return sprite.createGraphics();
	}

	public BufferedImage getBufferedImage()
	{
		// copy of sprite image
		BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// create graphics
		Graphics2D g = copy.createGraphics();

		// clear background
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, width, height);
		g.setComposite(AlphaComposite.Src);

		// draw sprite
		g.drawImage(sprite, 0, 0, null);

		// dispose graphics
		g.dispose();

		return copy;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}
}
