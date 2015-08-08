/*
 * @(#)DuplicateKeyException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

/**
 * Instances of the <code>DuplicateKeyException</code> class are
 * thrown to indicate that the software has attempted to create a
 * record that would create a duplicate key condition.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class DuplicateKeyException extends Exception {

    /**
     * Constructs a <code>DuplicateKeyException</code>
     * with no detail message.
     */
    public DuplicateKeyException() {

        super();
    }

    /**
     * Constructs a <code>DuplicateKeyException</code>
     * with the specified detail message.
     *
     * @param description
     * the detail message.
     */
    public DuplicateKeyException(final String description) {

        super(description);
    }

    /**
     * Constructs a <code>DuplicateKeyException</code> with the
     * specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public DuplicateKeyException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

