package mindcontrol.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mindcontrol.gameObjects.characters.Player;
import mindcontrol.library.GV;
import platformer2D.library.Debugger;

public class CheckPoint
{	
	// reference to the player
	Player p1;
	
	// hitbox
	private Rectangle hitbox;

	public CheckPoint(int x, int y)
	{
		int threeTileSize = 3*GV.TILESIZE;
		hitbox = new Rectangle(x, y, threeTileSize, threeTileSize);
	}
	
	public void setPlayer(Player p1)
	{
		this.p1 = p1;
	}
	
	public boolean atCheckPoint()
	{
		if (p1.inside(hitbox))
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	public void drawHitbox(Graphics2D g)
	{
		Debugger.drawHitbox(hitbox, g);
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fill(hitbox);
	}
	
}
