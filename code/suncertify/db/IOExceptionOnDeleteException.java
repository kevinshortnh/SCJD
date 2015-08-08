/*
 * @(#)IOExceptionOnDeleteException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

/**
 * Instances of the <code>IOExceptionOnDeleteException</code>
 * class are thrown to indicate that an IOException was thrown
 * during a <code>delete()</code>.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class IOExceptionOnDeleteException extends
    RuntimeException {

    /**
     * Constructs a <code>DatabaseDeleteFailedException</code>
     * with no detail message.
     */
    public IOExceptionOnDeleteException() {

        super();
    }

    /**
     * Constructs a <code>DatabaseDeleteFailedException</code>
     * with the specified detail message.
     *
     * @param description
     * the detail message.
     */
    public IOExceptionOnDeleteException(final String description) {

        super(description);
    }

    /**
     * Constructs a <code>DatabaseDeleteFailedException</code>
     * with the specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public IOExceptionOnDeleteException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

