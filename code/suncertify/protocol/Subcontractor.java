/*
 * @(#)Subcontractor.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.protocol;

import java.io.Serializable;

/**
 * The <code>Subcontractor</code> interface defines the methods
 * for operating on a subcontractor.
 *
 * @version 1.0
 * @author Kevin Short
 */
public interface Subcontractor extends Serializable {

    /**
     * Get the name of the subcontractor.
     *
     * @return
     * the name of the subcontractor.
     */
    String getSubcontractorName();

    /**
     * Get the city in which the subcontractor is located.
     *
     * @return
     * the city in which the subcontractor is located.
     */
    String getCity();

    /**
     * Get the types of work this contractor can perform.
     *
     * @return
     * a comma-separated list of the types of work this
     * contractor can perform.
     */
    String getTypesOfWorkPerformed();

    /**
     * Get the number of workers available.
     *
     * @return
     * the number of workers available.
     */
    Integer getNumberOfStaffInOrganization();

    /**
     * Get the hourly charge for this contractor.
     *
     * @return
     * the hourly charge for this contractor;
     * includes the currency symbol.
     */
    String getHourlyCharge();

    /**
     * Get the customer id for the customer who has booked
     * this subcontractor.
     *
     * @return
     * the customer id for the customer who has booked this
     * subcontractor.
     */
    Long getCustomerId();

    /**
     * Get a flag indicating whether the subcontractor is
     * available for booking.
     *
     * @return
     * <code>true</code> if the subcontractor is available,
     * <code>false</code> if not.
     */
    boolean isAvailable();

    /**
     * Set the customer id for the customer who has booked
     * this subcontractor.
     *
     * @param newCustomerId
     * the customer id for the customer who has booked this
     * subcontractor.
     */
    void setCustomerId(Long newCustomerId);
}

