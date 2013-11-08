package com.brackeen.javagamebook.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * The LoopingByteInputStream is a ByteArrayInputStream that
 * loops indefinitly. The looping stops when the close() method
 * is called.
 * <p>Possible ideas to extend this class:<ul>
 * <li>Add an option to only loop a certain number of times.
 * </ul>
 */
public class LoopingByteInputStream extends ByteArrayInputStream {

    private boolean closed;

    /**
     * Creates a new LoopingByteInputStream with the specified
     * byte array. The array is not copied. 
     * @param buffer 
     */
    public LoopingByteInputStream(byte[] buffer) {
        super(buffer);
        closed = false;
    }


    /**
     * Reads <code>length</code> bytes from the array. If the
     * end of the array is reached, the reading starts over from
     * the beginning of the array. Returns -1 if the array has
     * been closed.
     * @param buffer
     * @param offset
     * @param length
     * @return int
     */
    public int read(byte[] buffer, int offset, int length) {
        if (closed) {
            return -1;
        }
        int totalBytesRead = 0;

        while (totalBytesRead < length) {
            int numBytesRead = super.read(buffer,
                offset + totalBytesRead,
                length - totalBytesRead);

            if (numBytesRead > 0) {
                totalBytesRead += numBytesRead;
            }
            else {
                reset();
            }
        }
        return totalBytesRead;
    }


    /**
     * Closes the stream. Future calls to the read() methods
     * will return 1.
     * @throws IOException 
     */
    public void close() throws IOException {
        super.close();
        closed = true;
    }

}
