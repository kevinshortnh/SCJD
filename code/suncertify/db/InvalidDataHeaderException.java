/*
 * @(#)InvalidDataHeaderException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

/**
 * Instances of the <code>InvalidDataHeaderException</code> class
 * are thrown to indicate that a database header is invalid.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class InvalidDataHeaderException extends Exception {

    /**
     * Constructs a <code>InvalidDataHeaderException</code>
     * with no detail message.
     */
    public InvalidDataHeaderException() {

        super();
    }

    /**
     * Constructs a <code>InvalidDataHeaderException</code>
     * with the specified detail message.
     *
     * @param description
     * the detail message.
     */
    public InvalidDataHeaderException(final String description) {

        super(description);
    }

    /**
     * Constructs a <code>InvalidDataHeaderException</code> with
     * the specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public InvalidDataHeaderException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

