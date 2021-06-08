package platformer2D;

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import platformer2D.library.Debugger;

public class Background
{
	private BufferedImage bgImg;
	
	// platform hitboxes
	private ArrayList<Rectangle> hitboxes;
	
	public Background(String s)
	{
		try
		{
			bgImg = ImageIO.read(getClass().getResourceAsStream(s));
			// moveScale = ms;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		hitboxes = new ArrayList<Rectangle>();
	}

	public Background(int width, int height, Color c)
	{
		bgImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g2 = bgImg.getGraphics();
		g2.setColor(c);
		g2.fillRect(0, 0, width, height);
		g2.dispose();
		
		hitboxes = new ArrayList<Rectangle>();
	}

	public void drawHitboxes(Graphics2D g)
	{
		for (Rectangle hitbox : hitboxes)
		{
			Debugger.drawHitbox(hitbox, g);
		}
	}
	
	public void draw(Graphics2D g)
	{
		g.drawImage(bgImg, 0, 0, null);
	}

	public void addHitbox(int x, int y, int width, int height)
	{
		hitboxes.add(new Rectangle(x, y, width, height));
	}
	
	public void addHitbox(Rectangle hb)
	{
		hitboxes.add(hb);
	}
	
	public void addImage(BufferedImage img, int x, int y)
	{
		Graphics2D g = bgImg.createGraphics();
		
		g.drawImage(img, x, y, null);
		
		g.dispose();
	}
	
	/**
	 * 
	 * @param hb the hitbox of the object to check collision with
	 * @return the platform hitbox that hb collided with, returns null if no collision happens
	 */
	public Rectangle collides(Rectangle hb)
	{
		for (Rectangle hitbox : hitboxes)
		{
			if (hb.intersects(hitbox))
			{
				return new Rectangle(hitbox);
			}
		}
		
		return null;
	}
}
