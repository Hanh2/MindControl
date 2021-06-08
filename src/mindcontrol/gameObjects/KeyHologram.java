package mindcontrol.gameObjects;

import mindcontrol.library.JukeBox;

public class KeyHologram extends Hologram
{
	public KeyHologram(int baseXPos, int baseBottomYPos)
	{
		super(baseXPos, baseBottomYPos, "Key");
		//SFX
		JukeBox.load("/SFX/Key.mp3", "Key");
	}

	@Override
	protected void action()
	{
		p1.addKey();
		// play SFX
		JukeBox.play("Key");
	}
}
