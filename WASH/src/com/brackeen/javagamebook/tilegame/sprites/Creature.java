package com.brackeen.javagamebook.tilegame.sprites;

import java.lang.reflect.Constructor;
import com.brackeen.javagamebook.graphics.*;

/**
 *  A Creature is a Sprite that is affected by gravity and can
 *  die. It has four Animations: moving left, moving right,
 *  dying on the left, and dying on the right.
 */
public abstract class Creature extends Sprite {

    /**
        Amount of time to go from STATE_DYING to STATE_DEAD.
    */
    private static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;

    private Animation left;
    private Animation right;
    private Animation deadLeft;
    private Animation deadRight;
    private Animation standingLeft;
    private Animation standingRight;
    private Animation jumpingLeft;
    private Animation jumpingRight;
    public static int standing;
    private int state;
    private long stateTime;

    /**
     * Creates a new Creature with the specified Animations.
     * @param left
     * @param right
     * @param deadLeft
     * @param deadRight 
     */
    /**
    public Creature(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        state = STATE_NORMAL;
    }
    //*/ 
    
    /**
     * Creates a new Creature with the specified Animations.
     * @param left
     * @param right
     * @param deadLeft
     * @param deadRight 
     * @param standingLeft
     * @param standingRight
     * @param jumpingLeft
     * @param jumpingRight
     */
    public Creature(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation standingLeft, Animation standingRight,
        Animation jumpingLeft, Animation jumpingRight)
    {
        super(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        this.standingLeft = standingLeft;
        this.standingRight = standingRight;
        this.jumpingLeft = jumpingLeft;
        this.jumpingRight = jumpingRight;
        state = STATE_NORMAL;
        standing = 1;
    }

    /**
     * Clone Creature
     * @return Object
     */
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)left.clone(),
                (Animation)right.clone(),
                (Animation)deadLeft.clone(),
                (Animation)deadRight.clone(),
                (Animation)standingLeft.clone(),
                (Animation)standingRight.clone(),
                (Animation)jumpingLeft.clone(),
                (Animation)jumpingRight.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Gets the maximum speed of this Creature.
     * @return float
     */
    public float getMaxSpeed() {
        return 0;
    }


    /**
     * Wakes up the creature when the Creature first appears
     * on screen. Normally, the creature starts moving left.
     */
    public void wakeUp() {
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }


    /**
     * Gets the state of this Creature. The state is either
     * STATE_NORMAL, STATE_DYING, or STATE_DEAD.
     * @return 
     */
    public int getState() {
        return state;
    }


    /**
     * Sets the state of this Creature to STATE_NORMAL,
     * STATE_DYING, or STATE_DEAD.
     * @param state 
     */
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }


    /**
     * Checks if this creature is alive.
     * @return boolean
     */
    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }


    /**
     * Checks if this creature is flying.
     * @return boolean
     */
    public boolean isFlying() {
        return false;
    }


    /**
     * Called before update() if the creature collided with a
     * tile horizontally.
     */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }


    /**
     * Called before update() if the creature collided with a
     * tile vertically.
     */
    public void collideVertical() {
        setVelocityY(0);
    }


    /**
     * Updates the animaton for this creature.
     * @param elapsedTime 
     */
    public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        if(Player.onGround == false && getVelocityX() < 0 ){
            newAnim = jumpingLeft;
            standing = 0;
        }else if(Player.onGround == false && getVelocityX() > 0){
            newAnim = jumpingRight;
            standing = 1;
        }else if(Player.onGround == false && getVelocityX() == 0 && standing == 0 ){
            newAnim = jumpingLeft;
        }else if(Player.onGround == false && getVelocityX() == 0 && standing == 1){
            newAnim = jumpingRight;
        }else if (getVelocityX() < 0) {
            newAnim = left;
            standing = 0;
        }else if (getVelocityX() > 0) {
            newAnim = right;
            standing = 1;
        }else if (getVelocityX() == 0 && standing == 0){
            newAnim = standingLeft;
        }else if (getVelocityX() == 0 && standing == 1){
            newAnim = standingRight;
        }
        
        if (state == STATE_DYING && newAnim == left) {
            newAnim = deadLeft;
        }
        else if (state == STATE_DYING && newAnim == right) {
            newAnim = deadRight;
        }

        // update the Animation
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }

}
