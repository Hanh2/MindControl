package platformer2D.library;

import java.awt.*;

public final class Debugger
{
	private static float hitboxThickness = 1;
	private static Color hitboxColor = Color.RED;
	private Debugger()
	{
	}
	
	public static void drawHitbox(Rectangle hitbox, Graphics2D g)
	{
		g.setColor(hitboxColor);
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(hitboxThickness));
		g.draw(hitbox);
		g.setStroke(oldStroke);
	}

}
