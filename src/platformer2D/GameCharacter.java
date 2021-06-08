package platformer2D;

import platformer2D.library.CharacterState;

public abstract class GameCharacter extends MovableGameObject
{
	/**
	 * The name of the character type.
	 */
	private String characterType;

	/**
	 * The state of the character.
	 */
	protected CharacterState state;

	/**
	 * True if the character is facing right, false otherwise.
	 */
	protected boolean facingRight;

	/**
	 * The state of facingRight at the start of the level.
	 */
	private boolean facingRightAtStart;

	/**
	 * The value dy is set to when initializing jump.
	 */
	protected double jump;

	/**
	 * Creates a controllable character of the type characterType.
	 * 
	 * @param characterType
	 *            the name of the character type
	 */
	protected GameCharacter(String characterType)
	{
		this.characterType = characterType;
	}

	/**
	 * The object position is set to its spawn location, the object momentum is set
	 * to (0, 0), facingRight is set to facingRightAtStart, state is set to
	 * CharacterState.IDLE.
	 */
	public void reset()
	{
		super.reset();

		facingRight = facingRightAtStart;
		state = CharacterState.IDLE;
	}

	/**
	 * Handles how the character is controlled.
	 */
	protected abstract void control();

	/**
	 * Updates momentum and handles control then updates sprite position and hitbox
	 * position.
	 */
	public void update()
	{
		updateMomentum();
		control();
		updateSpritePosition();
		updateHbPosition();
	}

	/**
	 * @return the name of the character type
	 */
	public String getCharacterType()
	{
		return characterType;
	}

	/**
	 * Sets facingRightAtStart.
	 * 
	 * @param facingRightAtStart
	 *            the state of facingRight at the start of the level.
	 */
	public void setFacingRightAtStart(boolean facingRightAtStart)
	{
		this.facingRightAtStart = facingRightAtStart;
	}

	/**
	 * 
	 * @return the x coordinate of the center of the sprite.
	 */
	public double getCenterX()
	{
		return x + (double) sWidth/2;
	}

	/**
	 * 
	 * @return the y coordinate of the center of the sprite.
	 */
	public double getCenterY()
	{
		return y + (double) sHeight/2;
	}
}
