//
// ImageException.java
//
// Author: Walter Brameld
// Date: 3/30/2001
//
// Purpose: Thrown when an exception involving an image occurs.
//

package com.igormaznitsa.MIDPTools.ImagePacker;

/**
 * Thrown when an exception involving an image occurs.
 */
public class ImageException extends Exception {

    /**
     * Creates an <tt>ImageException</tt> with no detail message.
     */
    public ImageException() {
        super();
    }  // end constructor()


    /**
     * Creates an <tt>ImageException</tt> with the given detail message.
     */
    public ImageException( String detail ) {
        super( detail );
    }  // end constructor

}  // end class ImageException
