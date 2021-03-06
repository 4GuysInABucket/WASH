package com.brackeen.javagamebook.sound;

import java.io.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;
import com.brackeen.javagamebook.util.ThreadPool;
import com.brackeen.javagamebook.util.LoopingByteInputStream;
import java.net.URL;


/**
 * The SoundManager class manages sound playback. The
 * SoundManager is a ThreadPool, with each thread playing back
 * one sound at a time. This allows the SoundManager to
 * easily limit the number of simultaneous sounds being played.
 * <p>Possible ideas to extend this class:<ul>
 * <li>add a setMasterVolume() method, which uses Controls to
 *    set the volume for each line.
 * <li>don't play a sound if more than, say, 500ms has passed
 *    since the request to play
 * </ul>
 */
public class SoundManager extends ThreadPool {

    private AudioFormat playbackFormat;
    private ThreadLocal localLine;
    private ThreadLocal localBuffer;
    private Object pausedLock;
    private boolean paused;

    /**
     * Creates a new SoundManager using the maximum number of
     * simultaneous sounds.
     * @param playbackFormat 
     */
    public SoundManager(AudioFormat playbackFormat) {
        this(playbackFormat,
            getMaxSimultaneousSounds(playbackFormat));
    }


    /**
     * Creates a new SoundManager with the specified maximum
     * number of simultaneous sounds.
     * @param playbackFormat  the format of the audio
     * @param maxSimultaneousSounds  the maximum number of simultaneous sounds
     */
    public SoundManager(AudioFormat playbackFormat,
        int maxSimultaneousSounds)
    {
        super(Math.min(maxSimultaneousSounds,
            getMaxSimultaneousSounds(playbackFormat)));
        this.playbackFormat = playbackFormat;
        localLine = new ThreadLocal();
        localBuffer = new ThreadLocal();
        pausedLock = new Object();
        // notify threads in pool it's ok to start
        synchronized (this) {
            notifyAll();
        }
    }


    /**
     * Gets the maximum number of simultaneous sounds with the
     * specified AudioFormat that the default mixer can play.
     * @param playbackFormat  the audio format
     * @return the maximum number of simultaneous sounds
     */
    public static int getMaxSimultaneousSounds(
        AudioFormat playbackFormat)
    {
        DataLine.Info lineInfo = new DataLine.Info(
            SourceDataLine.class, playbackFormat);
        Mixer mixer = AudioSystem.getMixer(null);
        return mixer.getMaxLines(lineInfo);
    }


    /**
     * Does any clean up before closing.
     */
    protected void cleanUp() {
        // signal to unpause
        setPaused(false);

        // close the mixer (stops any running sounds)
        Mixer mixer = AudioSystem.getMixer(null);
        if (mixer.isOpen()) {
            mixer.close();
        }
    }

    /**
     * Closes after cleaning up.
     */
    public void close() {
        cleanUp();
        super.close();
    }

    /**
     * Joins after cleaning up.
     */
    public void join() {
        cleanUp();
        super.join();
    }


    /**
     * Sets the paused state. Sounds may not pause immediately.
     * @param paused  the current paused state
     */
    public void setPaused(boolean paused) {
        if (this.paused != paused) {
            synchronized (pausedLock) {
                this.paused = paused;
                if (!paused) {
                    // restart sounds
                    pausedLock.notifyAll();
                }
            }
        }
    }


    /**
     * Returns the paused state.
     * @return true if paused, or false if not
     */
    public boolean isPaused() {
        return paused;
    }


    /**
     * Loads a Sound from the file system. Returns null if an
     * error occurs.
     * @param filename  the name of the sound file
     * @return the loaded sound
     */
    public Sound getSound(String filename) {
        return getSound(getAudioInputStream(filename));
    }


    /**
     * Loads a Sound from an input stream. Returns null if an
     * error occurs.
     * @param is  the input stream
     * @return the loaded sound
     */
    public Sound getSound(InputStream is) {
        return getSound(getAudioInputStream(is));
    }


    /**
     * Loads a Sound from an AudioInputStream.
     * @param audioStream  the audio stream
     * @return the loaded sound
     */
    public Sound getSound(AudioInputStream audioStream) {
        if (audioStream == null) {
            return null;
        }

        // get the number of bytes to read
        int length = (int)(audioStream.getFrameLength() *
            audioStream.getFormat().getFrameSize());

        // read the entire stream
        byte[] samples = new byte[length];
        DataInputStream is = new DataInputStream(audioStream);
        try {
            is.readFully(samples);
            is.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        // return the samples
        return new Sound(samples);
    }


    /**
     * Creates an AudioInputStream from a sound from the file
     * system.
     * @param filename  the name of the sound file
     * @return the audio input stream
     */
    public AudioInputStream getAudioInputStream(String filename) {
        try {
            URL urlSound = SoundManager.class.getResource(filename);
            return getAudioInputStream(
                urlSound.openStream());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Creates an AudioInputStream from a sound from an input
     * stream.
     * @param is  the input stream
     * @return the audio input stream
     */
    public AudioInputStream getAudioInputStream(InputStream is) {

        try {
            if (!is.markSupported()) {
                is = new BufferedInputStream(is);
            }
            // open the source stream
            AudioInputStream source =
                AudioSystem.getAudioInputStream(is);

            // convert to playback format
            return AudioSystem.getAudioInputStream(
                playbackFormat, source);
        }
        catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        return null;
    }


    /**
     * Plays a sound. This method returns immediately.
     * @param sound  the sound to be played
     * @return the played sound
     */
    public InputStream play(Sound sound) {
        return play(sound, null, false);
    }


    /**
     * Plays a sound with an optional SoundFilter, and optionally
     * looping. This method returns immediately.
     * @param sound  the sound to be played
     * @param filter  the sound filter
     * @param loop  if the sound will be looped or not
     * @return the played sound
     */
    public InputStream play(Sound sound, SoundFilter filter,
        boolean loop)
    {
        InputStream is;
        if (sound != null) {
            if (loop) {
                is = new LoopingByteInputStream(
                    sound.getSamples());
            }
            else {
                is = new ByteArrayInputStream(sound.getSamples());
            }

            return play(is, filter);
        }
        return null;
    }


    /**
     * Plays a sound from an InputStream. This method
     * returns immediately.
     * @param is  the input stream
     * @return the played sound
     */
    public InputStream play(InputStream is) {
        return play(is, null);
    }


    /**
     * Plays a sound from an InputStream with an optional
     * sound filter. This method returns immediately.
     * @param is  the input stream of the sound to be played
     * @param filter  the sound filter
     * @return the played sound
     */
    public InputStream play(InputStream is, SoundFilter filter) {
        if (is != null) {
            if (filter != null) {
                is = new FilteredSoundStream(is, filter);
            }
            runTask(new SoundPlayer(is));
        }
        return is;
    }


    /**
     * Signals that a PooledThread has started. Creates the
     * Thread's line and buffer.
     */
    protected void threadStarted() {
        // wait for the SoundManager constructor to finish
        synchronized (this) {
            try {
                wait();
            }
            catch (InterruptedException ex) { }
        }

        // use a short, 100ms (1/10th sec) buffer for filters that
        // change in real-time
        int bufferSize = playbackFormat.getFrameSize() *
            Math.round(playbackFormat.getSampleRate() / 10);

        // create, open, and start the line
        SourceDataLine line;
        DataLine.Info lineInfo = new DataLine.Info(
            SourceDataLine.class, playbackFormat);
        try {
            line = (SourceDataLine)AudioSystem.getLine(lineInfo);
            line.open(playbackFormat, bufferSize);
        }
        catch (LineUnavailableException ex) {
            // the line is unavailable - signal to end this thread
            Thread.currentThread().interrupt();
            return;
        }

        line.start();

        // create the buffer
        byte[] buffer = new byte[bufferSize];

        // set this thread's locals
        localLine.set(line);
        localBuffer.set(buffer);
    }


    /**
     * Signals that a PooledThread has stopped. Drains and
     * closes the Thread's Line.
     */
    protected void threadStopped() {
        SourceDataLine line = (SourceDataLine)localLine.get();
        if (line != null) {
            line.drain();
            line.close();
        }
    }


    /**
     * The SoundPlayer class is a task for the PooledThreads to
     * run. It receives the threads's Line and byte buffer from
     * the ThreadLocal variables and plays a sound from an
     * InputStream.
     * <p>This class only works when called from a PooledThread.
     */
    protected class SoundPlayer implements Runnable {

        private InputStream source;

        public SoundPlayer(InputStream source) {
            this.source = source;
        }

        public void run() {
            // get line and buffer from ThreadLocals
            SourceDataLine line = (SourceDataLine)localLine.get();
            byte[] buffer = (byte[])localBuffer.get();
            if (line == null || buffer == null) {
                // the line is unavailable
                return;
            }

            // copy data to the line
            try {
                int numBytesRead = 0;
                while (numBytesRead != -1) {
                    // if paused, wait until unpaused
                    synchronized (pausedLock) {
                        if (paused) {
                            try {
                                pausedLock.wait();
                            }
                            catch (InterruptedException ex) {
                                return;
                            }
                        }
                    }
                    // copy data
                    numBytesRead =
                        source.read(buffer, 0, buffer.length);
                    if (numBytesRead != -1) {
                        line.write(buffer, 0, numBytesRead);
                    }
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

}
