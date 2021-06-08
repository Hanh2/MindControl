package mindcontrol.gameState;

import mindcontrol.managers.GameStateManager;

public abstract class GameState
{
	protected GameStateManager gsm;
	
	public abstract void init();
	public abstract void update();
	public abstract void draw(java.awt.Graphics2D g);	
}
