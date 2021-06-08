package mindcontrol.view;

import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import mindcontrol.library.GV;

public class Game
{

	public static void main(String[] args)
	{
		JFrame window = new JFrame(GV.GAME_NAME);


		// transparent cursor
		setCursorTransparent(window);
		
		// set the game panel
		GamePanel gp = new GamePanel();
		window.setContentPane(gp);
		
		// full screen
		setFullScreen(window);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	private static void setFullScreen(JFrame window)
	{
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		if (gd.isFullScreenSupported()) {
			window.setUndecorated(true);
	        gd.setFullScreenWindow(window);
	    } else {
	        System.err.println("Full screen not supported");
	        window.setSize(100, 100); // just something to let you see the window
	        window.setVisible(true);
	    }
		
		window.setResizable(false);
	}

	private static void setCursorTransparent(JFrame window)
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Point hotSpot = new Point(0, 0);
		BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
		Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");
		window.setCursor(invisibleCursor);
	}

}
