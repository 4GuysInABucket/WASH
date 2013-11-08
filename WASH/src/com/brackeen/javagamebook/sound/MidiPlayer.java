package com.brackeen.javagamebook.sound;

import java.io.*;
import javax.sound.midi.*;

/**
 * The MidiPlayer class manages the sequences of sound.
 */
public class MidiPlayer implements MetaEventListener {

    // Midi meta event
    public static final int END_OF_TRACK_MESSAGE = 47;

    private Sequencer sequencer;
    private boolean loop;
    private boolean paused;

    /**
     * Creates a new MidiPlayer object.
     */
    public MidiPlayer() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.addMetaEventListener(this);
        }
        catch ( MidiUnavailableException ex) {
            sequencer = null;
        }
    }


    /**
     * Loads a sequence from the file system. Returns null if
     * an error occurs.
     * @param filename  the name from the file system
     * @return the sequence, or <code>null</code> if an error occurs
     */
    public Sequence getSequence(String filename) {
        try {
            return getSequence(new FileInputStream(filename));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Loads a sequence from an input stream. Returns null if
     * an error occurs.
     * @param is  the sequence from the input stream
     * @return the sequence, or <code>null</code> if an error occurs
     */
    public Sequence getSequence(InputStream is) {
        try {
            if (!is.markSupported()) {
                is = new BufferedInputStream(is);
            }
            Sequence s = MidiSystem.getSequence(is);
            is.close();
            return s;
        }
        catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Plays a sequence, optionally looping. This method returns
     * immediately. The sequence is not played if it is invalid.
     * @param sequence  the sequence
     * @param loop  if the sequence should loop or not
     */
    public void play(Sequence sequence, boolean loop) {
        if (sequencer != null && sequence != null && sequencer.isOpen()) {
            try {
                sequencer.setSequence(sequence);
                sequencer.start();
                this.loop = loop;
            }
            catch (InvalidMidiDataException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * This method is called by the sound system when a meta
     * event occurs. In this case, when the end-of-track meta
     * event is received, the sequence is restarted if
     * looping is on.
     * @param event  the meta event
     */
    public void meta(MetaMessage event) {
        if (event.getType() == END_OF_TRACK_MESSAGE) {
            if (sequencer != null && sequencer.isOpen() && loop) {
                sequencer.start();
            }
        }
    }


    /**
     * Stops the sequencer and resets its position to 0.
     */
    public void stop() {
         if (sequencer != null && sequencer.isOpen()) {
             sequencer.stop();
             sequencer.setMicrosecondPosition(0);
         }
    }


    /**
     * Closes the sequencer.
     */
    public void close() {
         if (sequencer != null && sequencer.isOpen()) {
             sequencer.close();
         }
    }


    /**
     * Gets the sequencer.
     * @return the sequencer
     */
    public Sequencer getSequencer() {
        return sequencer;
    }


    /**
     * Sets the paused state. Music may not immediately pause.
     * @param paused  if the music is paused or not
     */
    public void setPaused(boolean paused) {
        if (this.paused != paused && sequencer != null && sequencer.isOpen()) {
            this.paused = paused;
            if (paused) {
                sequencer.stop();
            }
            else {
                sequencer.start();
            }
        }
    }


    /**
     * Returns the paused state.
     * @return paused (if the music is paused or not)
     */
    public boolean isPaused() {
        return paused;
    }

}
