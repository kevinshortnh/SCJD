/*
 * @(#)CreateFailedException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

/**
 * Instances of the <code>CreateFailedException</code> class are
 * thrown to indicate that a data record <code>create()</code>
 * failed.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class CreateFailedException extends RuntimeException {

    /**
     * Constructs a <code>CreateFailedException</code>
     * with no detail message.
     */
    public CreateFailedException() {

        super();
    }

    /**
     * Constructs a <code>CreateFailedException</code>
     * with the specified detail message.
     *
     * @param description
     * the detail message.
     */
    public CreateFailedException(final String description) {

        super(description);
    }

    /**
     * Constructs a <code>CreateFailedException</code>
     * with the specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public CreateFailedException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

