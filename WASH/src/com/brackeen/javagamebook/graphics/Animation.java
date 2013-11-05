package com.brackeen.javagamebook.graphics;

import java.awt.Image;
import java.util.ArrayList;

/**
 * The Animation class manages a series of images (frames) and
 * the amount of time to display each frame.
 */
public class Animation {

    private ArrayList frames;
    private int currFrameIndex;
    private long animTime;
    private long totalDuration;


    /**
     * Creates a new, empty Animation.
     */
    public Animation() {
        this(new ArrayList(), 0);
    }

    /**
     * Creates a new Animation with a list of frames and a total duration time.
     * @param frames  the list of the frames of the animation
     * @param totalDuration  the time duration of the animation
     */
    private Animation(ArrayList frames, long totalDuration) {
        this.frames = frames;
        this.totalDuration = totalDuration;
        start();
    }


    /**
     * Creates a duplicate of this animation. The list of frames
     * are shared between the two Animations, but each Animation
     * can be animated independently.
     * @return new Animation  new copy of frames and duration of the animation
     */
    public Object clone() {
        return new Animation(frames, totalDuration);
    }


    /**
     * Adds an image to the animation with the specified
     * duration (time to display the image).
     * @param image  the image of the added frame
     * @param duration  the duration of the added frame
     */
    public synchronized void addFrame(Image image,
        long duration)
    {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }


    /**
     * Starts this animation over from the beginning.
     */
    public synchronized void start() {
        animTime = 0;
        currFrameIndex = 0;
    }


    /**
     * Updates this animation's current image (frame), if
     * neccesary.
     * @param elapsedTime  the time elapsed since the animation started
     */
    public synchronized void update(long elapsedTime) {
        if (frames.size() > 1) {
            animTime += elapsedTime;

            if (animTime >= totalDuration) {
                animTime = animTime % totalDuration;
                currFrameIndex = 0;
            }

            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;
            }
        }
    }


    /**
     * Gets this Animation's current image. Returns null if this
     * animation has no images.
     * 
     * @return the image of current animation's frame, or <code>null</code>
     * if the animation has no images
     */
    public synchronized Image getImage() {
        if (frames.size() == 0) {
            return null;
        }
        else {
            return getFrame(currFrameIndex).image;
        }
    }
    
    /**
     * Gets this Animation's current frame.
     * @param i the index of the frame
     * @return the frame located at index i
     */
    private AnimFrame getFrame(int i) {
        return (AnimFrame)frames.get(i);
    }

    /**
     * The AnimFrame class manages the images of frames and
     * the amount of time to display each image.
     */
    private class AnimFrame {

        Image image;
        long endTime;
        
        /**
         * Creates a new AnimFrame with the images and an end time.
         * @param image the image of the frame
         * @param endTime the end time of the frame
         */
        public AnimFrame(Image image, long endTime) {
            this.image = image;
            this.endTime = endTime;
        }
    }
}
