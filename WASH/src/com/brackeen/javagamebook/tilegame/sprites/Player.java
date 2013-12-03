package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
 * The Player.
 */
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;
    
    private boolean firing;
    private long bulletTimer;
    private long bulletDelay;

    /**
     * Constructor
     * @param left
     * @param right
     * @param deadLeft
     * @param deadRight 
     * @param standingLeft
     * @param standingRight
     * @param jumpingLeft
     * @param jumpingRight
     */
    
    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation standingLeft, 
        Animation standingRight, Animation jumpingLeft, Animation jumpingRight)
    {
        super(left, right, deadLeft, deadRight, standingLeft, standingRight, jumpingLeft, jumpingRight);
        firing = false;
        bulletTimer = System.nanoTime();
        bulletDelay = 500;
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
        if (this.onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }
    
    /**
     * Sets bullet Timer 
     * @param timer 
     */
    public void setBulletTimer(long timer){
        this.bulletTimer = timer;
    }
    /**
     * Get bullet Timer
     * @return bullet timer
     */
    public long getBulletTimer(){
        return this.bulletTimer;
    }
    
    /**
     * Get bullet Delay
     * @return bullet timer
     */
    public long getBulletDelay(){
        return this.bulletDelay;
    }
    
    /**
     * Is player firing?
     * @return firing
     */
    public boolean isFiring(){
        return this.firing;
    }
    
    /**
     * Makes the player fire 
     * @param fire 
     */
    public void fire(boolean fire) {
        this.firing = fire;
    }

    /**
     * Max Speed
     * @return float
     */
    
    public float getMaxSpeed() {
        return 0.5f;
    }

}
