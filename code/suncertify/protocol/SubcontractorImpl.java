/*
 * @(#)SubcontractorImpl.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.protocol;

/**
 * The <code>SubcontractorImpl</code> class implements the
 * <code>Subcontractor</code> interface.
 *
 * @version 1.0
 * @author Kevin Short
 */
public final class SubcontractorImpl implements Subcontractor {

    /**
     * This value in the <code>customerId</code> field indicates
     * that the subcontractor is available for sale.
     */
    private static final Long SUBCONTRACTOR_AVAILABLE;

    /** Minimum <code>customerId</code> value. */
    private static final Long MIN_CUSTOMER_ID;

    /** Maximum <code>customerId</code> value. */
    private static final Long MAX_CUSTOMER_ID;

    /** A key for accessing this subcontractor. */
    private final Object key;

    /** The name of the subcontractor. */
    private final String subcontractorName;

    /** The city in which the subcontractor is located. */
    private final String city;

    /**
     * A comma-separated list of the types of work this
     * contractor can perform.
     */
    private final String typesOfWorkPerformed;

    /** The number of workers available. */
    private final Integer numberOfStaffInOrganization;

    /**
     * The hourly charge for this contractor;
     * includes the currency symbol.
     */
    private final String hourlyCharge;

    /**
     * The customer id for the customer who has booked this
     * subcontractor.
     * <ul><li>
     * Value is <code>SUBCONTRACTOR_AVAILABLE</code> if the
     * subcontractor is available for sale.
     * </li><li>
     * Value is from <code>MIN_CUSTOMER_ID</code> to
     * <code>MAX_CUSTOMER_ID</code>, inclusive, if the
     * subcontractor has been booked.
     * </li></ul>
     */
    private Long customerId;

    // initialize constant objects
    static {

        final long maxCustomerId = 99999999;

        MIN_CUSTOMER_ID = new Long(1);
        MAX_CUSTOMER_ID = new Long(maxCustomerId);
        SUBCONTRACTOR_AVAILABLE = new Long(0);
    }

    /**
     * Contructs a <code>SubcontractorImpl</code>.
     *
     * @param newKey
     * the key for accessing this subcontractor.
     *
     * @param newSubcontractorName
     * the name of the subcontractor.
     *
     * @param newCity
     * the city in which the subcontractor is located.
     *
     * @param newTypesOfWorkPerformed
     * a comma-separated list of the types of work this
     * contractor can perform.
     *
     * @param newNumberOfStaffInOrganization
     * the number of workers available.
     *
     * @param newHourlyCharge
     * the hourly charge for this contractor;
     * includes the currency symbol.
     *
     * @param newCustomerId
     * the customer id for the customer who has booked this
     * subcontractor.
     */
    public SubcontractorImpl(
        final Object newKey,
        final String newSubcontractorName,
        final String newCity,
        final String newTypesOfWorkPerformed,
        final Integer newNumberOfStaffInOrganization,
        final String newHourlyCharge,
        final Long newCustomerId) {

        key = newKey;
        subcontractorName = newSubcontractorName;
        city = newCity;
        typesOfWorkPerformed = newTypesOfWorkPerformed;
        numberOfStaffInOrganization = newNumberOfStaffInOrganization;
        hourlyCharge = newHourlyCharge;

        // validate and set customer id
        setCustomerId(newCustomerId);
    }

    /**
     * Get the key for accessing this subcontractor.
     *
     * @return
     * the key for accessing this subcontractor.
     */
    public Object getKey() {

        return key;
    }

    /**
     * Get the name of the subcontractor.
     *
     * @return
     * the name of the subcontractor.
     */
    public String getSubcontractorName() {

        return subcontractorName;
    }

    /**
     * Get the city in which the subcontractor is located.
     *
     * @return
     * the city in which the subcontractor is located.
     */
    public String getCity() {

        return city;
    }

    /**
     * Get the types of work this contractor can perform.
     *
     * @return
     * a comma-separated list of the types of work this
     * contractor can perform.
     */
    public String getTypesOfWorkPerformed() {

        return typesOfWorkPerformed;
    }

    /**
     * Get the number of workers available.
     *
     * @return
     * the number of workers available.
     */
    public Integer getNumberOfStaffInOrganization() {

        return numberOfStaffInOrganization;
    }

    /**
     * Get the hourly charge for this contractor.
     *
     * @return
     * the hourly charge for this contractor;
     * includes the currency symbol.
     */
    public String getHourlyCharge() {

        return hourlyCharge;
    }

    /**
     * Get the customer id for the customer who has booked
     * this subcontractor.
     *
     * @return
     * the customer id for the customer who has booked this
     * subcontractor.
     */
    public Long getCustomerId() {

        return customerId;
    }

    /**
     * Get a flag indicating whether the subcontractor is
     * available for booking.
     *
     * @return
     * <code>true</code> if the subcontractor is available,
     * <code>false</code> if not.
     */
    public boolean isAvailable() {

        return SUBCONTRACTOR_AVAILABLE.equals(customerId);
    }

    /**
     * Set the customer id for the customer who has booked
     * this subcontractor.
     *
     * @param value
     * the customer id for the customer who has booked this
     * subcontractor.
     */
    public void setCustomerId(final Long value) {

        // validate
        if (SUBCONTRACTOR_AVAILABLE.equals(value)
            || ((value.compareTo(MIN_CUSTOMER_ID) >= 0) && (value
                .compareTo(MAX_CUSTOMER_ID) <= 0))) {
            // good, so set the customer id
            customerId = value;
        } else {
            // bad value
            throw new IllegalArgumentException();
        }
    }
}

