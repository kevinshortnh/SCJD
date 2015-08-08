/*
 * @(#)SubcontractorNotAvailableException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.protocol;

/**
 * Instances of the <code>SubcontractorNotAvailableException</code>
 * class are thrown to indicate that the Subcontractor is not
 * available for booking.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class SubcontractorNotAvailableException extends
    Exception {

    /**
     * Constructs a <code>SubcontractorNotAvailableException</code>
     * with no detail message.
     */
    public SubcontractorNotAvailableException() {

        super();
    }

    /**
     * Constructs a <code>SubcontractorNotAvailableException</code>
     * with the specified detail message.
     *
     * @param desc
     * the detail message.
     */
    public SubcontractorNotAvailableException(final String desc) {

        super(desc);
    }

    /**
     * Constructs a <code>SubcontractorNotAvailableException</code>
     * with the specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public SubcontractorNotAvailableException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

