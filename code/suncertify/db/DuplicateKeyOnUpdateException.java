/*
 * @(#)DuplicateKeyOnUpdateException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

/**
 * Instances of the <code>DuplicateKeyOnUpdateException</code>
 * class are thrown to indicate that a duplicate key condition
 * would exist if the record was updated.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class DuplicateKeyOnUpdateException extends
    RuntimeException {

    /**
     * Constructs a <code>DuplicateKeyOnUpdateException</code>
     * with no detail message.
     */
    public DuplicateKeyOnUpdateException() {

        super();
    }

    /**
     * Constructs a <code>DuplicateKeyOnUpdateException</code>
     * with the specified detail message.
     *
     * @param desc
     * the detail message.
     */
    public DuplicateKeyOnUpdateException(final String desc) {

        super(desc);
    }

    /**
     * Constructs a <code>DuplicateKeyOnUpdateException</code>
     * with the specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public DuplicateKeyOnUpdateException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

