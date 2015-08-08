/*
 * @(#)IOExceptionOnUpdateException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

/**
 * Instances of the <code>IOExceptionOnUpdateException</code>
 * class are thrown to indicate that an IOException was thrown
 * during an <code>update()</code>.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class IOExceptionOnUpdateException extends
    RuntimeException {

    /**
     * Constructs a <code>DatabaseUpdateFailedException</code>
     * with no detail message.
     */
    public IOExceptionOnUpdateException() {

        super();
    }

    /**
     * Constructs a <code>DatabaseUpdateFailedException</code>
     * with the specified detail message.
     *
     * @param description
     * the detail message.
     */
    public IOExceptionOnUpdateException(final String description) {

        super(description);
    }

    /**
     * Constructs a <code>DatabaseUpdateFailedException</code>
     * with the specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public IOExceptionOnUpdateException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

