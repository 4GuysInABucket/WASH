package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
 * A Fly is a Creature that fly slowly in the air.
*/
public class Fly extends Creature {
    /**
     * Constructor
     * @param left
     * @param right
     * @param deadLeft
     * @param deadRight 
     */
    public Fly(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }

    /**
     * Get Max Speed
     * @return float
     */
    
    public float getMaxSpeed() {
        return 0.2f;
    }

    /**
     * Is it alive/flying
     * @return boolean
     */

    public boolean isFlying() {
        return isAlive();
    }

}
