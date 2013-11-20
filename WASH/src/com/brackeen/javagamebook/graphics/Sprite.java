package com.brackeen.javagamebook.graphics;

import java.awt.Image;

/**
 * The Sprite class is used for every movable object or single entity on screen. 
 */
public class Sprite {

    protected Animation anim;
    // position (pixels)
    private float x;
    private float y;
    // velocity (pixels per millisecond)
    private float dx;
    private float dy;

    /**
     * Creates a new Sprite object with the specified Animation.
     * @param anim the Animation of the Sprite
     */
    public Sprite(Animation anim) {
        this.anim = anim;
    }
    
    /**
     * Creates a new Sprite object with the specified Animation.
     * @param anim the Animation of the Sprite
     */
    public Sprite(Animation anim, float x, float y) {
        this.anim = anim;
        this.x = x;
        this.y = y;
    }

    /**
     * Updates this Sprite's Animation and its position based
     * on the velocity.
     * @param elapsedTime the time elapsed since the animation began
     */
    public void update(long elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
    }

    /**
     * Gets this Sprite's current x position.
     * @return x position
     */
    public float getX() {
        return x;
    }

    /**
     * Gets this Sprite's current y position.
     * @return y position
     */
    public float getY() {
        return y;
    }

    /**
     * Sets this Sprite's current x position.
     * @param x  new x position
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Sets this Sprite's current y position.
     * @param y  new y position
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets this Sprite's width, based on the size of the
     * current image.
     * @return width of Sprite's animation
     */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
     * Gets this Sprite's height, based on the size of the
     * current image.
     * @return height of Sprite's animation
     */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
     * Gets the horizontal velocity of this Sprite in pixels
     * per millisecond.
     * @return x velocity
     */
    public float getVelocityX() {
        return dx;
    }

    /**
     * Gets the vertical velocity of this Sprite in pixels
     * per millisecond.
     * @return y velocity
     */
    public float getVelocityY() {
        return dy;
    }

    /**
     * Sets the horizontal velocity of this Sprite in pixels
     * per millisecond.
     * @param dx  new x velocity
     */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
     * Sets the vertical velocity of this Sprite in pixels
     * per millisecond.
     * @param dy  new y velocity
     */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
     * Gets this Sprite's current image.
     * @return image of Sprite
     */
    public Image getImage() {
        return anim.getImage();
    }

    /**
     * Clones this Sprite. Does not clone position or velocity
     * info.
     * @return new identical Sprite
     */
    public Object clone() {
        return new Sprite(anim);
    }
}
