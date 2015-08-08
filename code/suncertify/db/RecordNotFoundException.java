/*
 * @(#)RecordNotFoundException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

/**
 * Instances of the <code>RecordNotFoundException</code> class
 * are thrown to indicate that the software has specified a
 * record that does not exist or is marked as deleted in a
 * database file.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class RecordNotFoundException extends Exception {

    /**
     * Constructs a <code>RecordNotFoundException</code>
     * with no detail message.
     */
    public RecordNotFoundException() {

        super();
    }

    /**
     * Constructs a <code>RecordNotFoundException</code>
     * with the specified detail message.
     *
     * @param description
     * the detail message.
     */
    public RecordNotFoundException(final String description) {

        super(description);
    }

    /**
     * Constructs a <code>RecordNotFoundException</code> with the
     * specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public RecordNotFoundException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

