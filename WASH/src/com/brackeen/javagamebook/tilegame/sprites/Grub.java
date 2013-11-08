package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
 * A Grub is a Creature that moves slowly on the ground.
 */
public class Grub extends Creature {

    /**
     * Constructor
     * @param left
     * @param right
     * @param deadLeft
     * @param deadRight 
     */
    public Grub(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }

    /**
     * Get Max Speed
     * @return float
     */
    
    public float getMaxSpeed() {
        return 0.05f;
    }

}
