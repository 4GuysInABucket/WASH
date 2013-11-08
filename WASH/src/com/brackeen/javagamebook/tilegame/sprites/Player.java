package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
 * The Player.
 */
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private boolean onGround;

    /**
     * Constructor
     * @param left
     * @param right
     * @param deadLeft
     * @param deadRight 
     */
    
    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }

    /**
     * Set Velocity after collide horizontal
     */
    
    public void collideHorizontal() {
        setVelocityX(0);
    }

    /**
     * Collide Vertical if velocity>0 set onGround to true
     */
    
    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }

    /**
     * Set Y coordinate
     * @param y 
     */

    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }

    /**
     * Start player
     */
    
    public void wakeUp() {
        // do nothing
    }


    /**
     * Makes the player jump if the player is on the ground or
     * if forceJump is true. 
     * @param forceJump 
     */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }

    /**
     * Max Speed
     * @return float
     */
    
    public float getMaxSpeed() {
        return 0.5f;
    }

}
