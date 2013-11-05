package com.brackeen.javagamebook.graphics;

import javax.swing.RepaintManager;
import javax.swing.JComponent;

/**
 * The NullRepaintManager is a RepaintManager that doesn't
 * do any repainting. Useful when all the rendering is done
 * manually by the application.
 */
public class NullRepaintManager extends RepaintManager {

    /**
     * Installs the NullRepaintManager.
     */
    public static void install() {
        RepaintManager repaintManager = new NullRepaintManager();
        repaintManager.setDoubleBufferingEnabled(false);
        RepaintManager.setCurrentManager(repaintManager);
    }
    
    /**
     * Sets a JComponent as invalid.
     * @param c  the JComponent
     */
    public void addInvalidComponent(JComponent c) {
        // do nothing
    }
    
    /**
     * Sets a region as dirty.
     * @param c  the JComponent
     * @param x  the position in x
     * @param y  the position in e y
     * @param w  the width
     * @param h  the height
     */
    public void addDirtyRegion(JComponent c, int x, int y,
        int w, int h)
    {
        // do nothing
    }

    /**
     * Sets a JComponent as completely dirty.
     * @param c  the JComponent
     */
    public void markCompletelyDirty(JComponent c) {
        // do nothing
    }

    /**
     * Paints dirty regions.
     */
    public void paintDirtyRegions() {
        // do nothing
    }

}
