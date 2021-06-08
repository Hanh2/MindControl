package mindcontrol.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import javax.swing.JPanel;

import mindcontrol.library.GV;
import mindcontrol.library.Keys;
import mindcontrol.managers.GameStateManager;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener
{
	// game thread
	private Thread thread;
	private boolean running;
	private long targetTime = 1000 / GV.FPS;

	// image
	private BufferedImage image;
	private Graphics2D g;

	// game state manager
	private GameStateManager gsm;

	// scale factor
	private int panelWidth;
	private int panelHeight;

	public GamePanel()
	{
		super();

		setFocusable(true);
		requestFocus();
	}

	public void addNotify()
	{
		super.addNotify();
		if (thread == null)
		{
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}

	public void init()
	{
		image = new BufferedImage(GV.VIEWPORTWIDTH, GV.VIEWPORTHEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();

		running = true;

		gsm = new GameStateManager();
		
		// size
		panelWidth = this.getWidth();
		panelHeight = this.getHeight();
	}

	public void run()
	{
		init();

		long start;
		long elapsed;
		long wait;

		// game loop.
		// Why not true instead of running?
		while (running)
		{
			start = System.nanoTime();

			update();
			draw();
			drawToScreen();

			elapsed = System.nanoTime() - start;

			wait = targetTime - elapsed / 1000000;

			// temporary?
			if (wait < 0)
				wait = 0;

			try
			{
				Thread.sleep(wait);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void update()
	{
		gsm.update();
		Keys.update();
	}

	private void draw()
	{
		gsm.draw(g);
	}

	private void drawToScreen()
	{
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, panelWidth, panelHeight, null);
		g2.dispose();
	}

	@Override
	public void keyPressed(KeyEvent key)
	{
		Keys.keySet(key.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent key)
	{
		Keys.keySet(key.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent key)
	{
		// TODO Auto-generated method stub

	}

}
