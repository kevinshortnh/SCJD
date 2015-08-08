/*
 * @(#)InvalidMagicCookieException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

/**
 * Instances of the <code>InvalidMagicCookieException</code>
 * class are thrown to indicate that a database file magic cookie
 * does not match the expected value.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class InvalidMagicCookieException extends Exception {

    /**
     * Constructs a <code>InvalidMagicCookieException</code>
     * with no detail message.
     */
    public InvalidMagicCookieException() {

        super();
    }

    /**
     * Constructs a <code>InvalidMagicCookieException</code>
     * with the specified detail message.
     *
     * @param description
     * the detail message.
     */
    public InvalidMagicCookieException(final String description) {

        super(description);
    }

    /**
     * Constructs a <code>InvalidMagicCookieException</code> with
     * the specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public InvalidMagicCookieException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

