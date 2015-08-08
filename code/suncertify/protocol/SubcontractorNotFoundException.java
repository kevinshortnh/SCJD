/*
 * @(#)SubcontractorNotFoundException.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.protocol;

/**
 * Instances of the <code>SubcontractorNotFoundException</code>
 * class are thrown to indicate that the requested
 * <code>Subcontractor</code> does not exist.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class SubcontractorNotFoundException extends Exception {

    /**
     * Constructs a <code>SubcontractorNotFoundException</code>
     * with no detail message.
     */
    public SubcontractorNotFoundException() {

        super();
    }

    /**
     * Constructs a <code>SubcontractorNotFoundException</code>
     * with the specified detail message.
     *
     * @param description
     * the detail message.
     */
    public SubcontractorNotFoundException(
        final String description) {

        super(description);
    }

    /**
     * Constructs a <code>SubcontractorNotFoundException</code>
     * with the specified detail message and cause.
     *
     * @param description
     * the detail message.
     *
     * @param  cause
     * the cause.
     */
    public SubcontractorNotFoundException(
        final String description,
        final Throwable cause) {

        super(description, cause);
    }
}

