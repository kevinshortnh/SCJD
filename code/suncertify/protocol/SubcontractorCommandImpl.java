/*
 * @(#)SubcontractorCommandImpl.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.protocol;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import suncertify.db.DB;
import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.Data.FieldDescriptor;

/**
 * <p>
 * The <code>SubcontractorCommandImpl</code> class implements the
 * <code>SubcontractorCommand</code> interface and uses an
 * instance of the <code>Data</code> class (that implements the
 * <code>DB</code> interface) to access its data store.
 * <h3>
 * Database schema
 * </h3>
 * <p>
 * The methods depend on the "Database field name" values
 * described below.
 * No assumptions are made regarding field sequence.
 * However, the database field names must match and all required
 * fields must be present.
 * </p>
 * <p>
 * The database that Bodgitt and Scarper uses contains the
 * following fields:
 * </p>
 * <table border="1">
 * <tr>
 * <th>Field descriptive name</th>
 * <th>Database field name</th>
 * <th>Field length</th>
 * <th>Detailed description</th>
 * </tr>
 * <tr>
 * <td>Subcontractor Name</td>
 * <td>name</td>
 * <td>32</td>
 * <td>The name of the subcontractor this record relates to.</td>
 * </tr>
 * <tr>
 * <td>City</td>
 * <td>location</td>
 * <td>64</td>
 * <td>The locality in which this contrctor works.</td>
 * </tr>
 * <tr>
 * <td>Types of work performed</td>
 * <td>specialties</td>
 * <td>64</td>
 * <td>Comma separated list of types of work this contractor can
 * perform.</td>
 * </tr>
 * <tr>
 * <td>Number of staff in organization</td>
 * <td>size</td>
 * <td>6</td>
 * <td>The number of workers available when this record is
 * booked.</td>
 * </tr>
 * <tr>
 * <td>Hourly charge</td>
 * <td>rate</td>
 * <td>8</td>
 * <td>Charge per hour for the subcontractor.
 * This field includes the currency symbol.</td>
 * </tr>
 * <tr>
 * <td>Customer holding this record</td>
 * <td>owner</td>
 * <td>8</td>
 * <td>The id value (an 8 digit number) of the customer who has
 * booked this subcontractor. If this field is blank, the record
 * is available for booking.</td>
 * </tr>
 * </table>
 *
 * @version 1.0
 * @author Kevin Short
 */
public final class SubcontractorCommandImpl extends
    UnicastRemoteObject implements SubcontractorCommand {

    /** Identifier for 'name' field. */
    private static final String DB_NAME = "name";

    /** Identifier for 'location' field. */
    private static final String DB_LOCATION = "location";

    /** Identifier for 'specialties' field. */
    private static final String DB_SPECIALTIES = "specialties";

    /** Identifier for 'size' field. */
    private static final String DB_SIZE = "size";

    /** Identifier for 'rate' field. */
    private static final String DB_RATE = "rate";

    /** Identifier for 'owner' field. */
    private static final String DB_OWNER = "owner";

    /** The <code>DB</code> interface for this
     * <code>Subcontractor</code>.
     */
    private final DB db;

    /** Array index of 'name' field. */
    private int dbNameIndex;

    /** Array index of 'location' field. */
    private int dbLocationIndex;

    /** Array index of 'specialties' field. */
    private int dbSpecialtiesIndex;

    /** Array index of 'size' field. */
    private int dbSizeIndex;

    /** Array index of 'rate' field. */
    private int dbRateIndex;

    /** Array index of 'owner' field. */
    private int dbOwnerIndex;

    /**
     * Creates new <code>SubcontractorCommandImpl</code> object.
     *
     * @param newDB
     * the <code>DB</code> interface for the server.
     *
     * @throws RemoteException
     * if an attempt to export a remote object fails.
     */
    public SubcontractorCommandImpl(final DB newDB)
        throws RemoteException {

        // validate arguments

        if (null == newDB) {
            throw new NullPointerException();
        }

        if (!(newDB instanceof Data)) {
            throw new IllegalArgumentException();
        }

        db = newDB;

        // discover the database field array indices

        final FieldDescriptor[] fieldDescriptors = ((Data) db)
            .getFieldDescriptors();

        for (int i = 0; i < fieldDescriptors.length; i++) {

            final String fieldName = fieldDescriptors[i]
                .getFieldName();

            /* Look for the field names we are interested.
             * It is safe to ignore any fields we do not use.
             */
            if (DB_NAME.equals(fieldName)) {
                dbNameIndex = i;
            } else if (DB_LOCATION.equals(fieldName)) {
                dbLocationIndex = i;
            } else if (DB_SPECIALTIES.equals(fieldName)) {
                dbSpecialtiesIndex = i;
            } else if (DB_SIZE.equals(fieldName)) {
                dbSizeIndex = i;
            } else if (DB_RATE.equals(fieldName)) {
                dbRateIndex = i;
            } else if (DB_OWNER.equals(fieldName)) {
                dbOwnerIndex = i;
            }
        }

        // make sure all fields were found
        assert (-1 != dbNameIndex)
            && (-1 != dbLocationIndex)
            && (-1 != dbSpecialtiesIndex)
            && (-1 != dbSizeIndex)
            && (-1 != dbRateIndex)
            && (-1 != dbOwnerIndex);
    }

    /**
     * Find <code>Subcontractor</code> objects that match the
     * specified criteria.
     *
     * @param subcontractorName
     * match this subcontractor name;
     * <code>null</code> matches any value.
     *
     * @param city
     * match this city;
     * <code>null</code> matches any value.
     *
     * @return
     * an array of <code>Subcontractor</code> objects that match
     * the specified criteria.
     *
     * @throws RemoteException
     * if an attempt to export a remote object fails.
     */
    public Subcontractor[] find(
        final String subcontractorName,
        final String city) throws RemoteException {

        // build up 'criteria' as expected by the DB interface
        final String[] criteria = new String[] {
            subcontractorName,
            city,
            null,
            null,
            null,
            null
        };

        // find all  records, per criteria
        final int[] recNos = db.find(criteria);

        // we will return an array of Subcontrator object refs
        final Subcontractor[] subcontractors;
        subcontractors = new Subcontractor[recNos.length];

        // read each matched data record
        for (int i = 0, j = 0; i < recNos.length; i++) {

            // read the record
            String[] values = null;
            try {
                values = db.read(recNos[i]);
            } catch (final RecordNotFoundException e) {
                /* The record must have been deleted after we did
                 * the find(), so just ignore it.
                 */
                continue;
            }

            // convert number of staff from a String to an int
            int size;
            try {
                size = Integer.parseInt(values[dbSizeIndex]
                    .trim());
            } catch (final NumberFormatException e) {
                size = 0;
            }

            // parse the customer id
            long customerId;
            try {
                customerId = Long.parseLong(values[dbOwnerIndex]
                    .trim());
            } catch (final NumberFormatException e) {
                customerId = 0;
            }

            /* Create a matching Subcontractor object.
             *
             * Use the record number as the lookup key.
             *
             * Note: We use hard-coded array indices here. To
             * make the code more extensible (but less efficient)
             * we could call Data.getFieldDescriptors() to match
             * field names with array indices. Of course, the
             * field names could change too. Common practice
             * would be to add additional fields to the end of
             * the records, so this will most likely be okay.
             */
            final SubcontractorImpl subcontractor = new SubcontractorImpl(
                new Integer(recNos[i]),
                values[dbNameIndex].trim(),
                values[dbLocationIndex].trim(),
                values[dbSpecialtiesIndex].trim(),
                new Integer(size),
                values[dbRateIndex].trim(),
                new Long(customerId));

            // add the new object to the result
            subcontractors[j++] = subcontractor;
        }

        return subcontractors;
    }

    /**
     * Book the specified <code>Subcontractor</code>.
     *
     * This method assumes the <code>Subcontractor</code>
     * object's <code>customerId</code> field has been set to the
     * desired value.
     *
     * @param subcontractor
     * the <code>Subcontractor</code> to update.
     *
     * @throws SubcontractorNotAvailableException
     * if the customer id has already been set.
     *
     * @throws SubcontractorNotFoundException
     * if a requested <code>Subcontractor</code> does not exist.
     */
    public void book(final Subcontractor subcontractor)
        throws SubcontractorNotAvailableException,
        SubcontractorNotFoundException {

        // validate arguments
        if (null == subcontractor) {
            throw new NullPointerException();
        }

        // validate the implementation class
        if (!(subcontractor instanceof SubcontractorImpl)) {
            throw new IllegalArgumentException();
        }

        final SubcontractorImpl o = (SubcontractorImpl) subcontractor;

        // get the record number
        final int recNo = ((Integer) o.getKey()).intValue();

        // lock the record
        long cookie = 0;
        try {
            cookie = db.lock(recNo);
        } catch (final RecordNotFoundException e) {
            throw new SubcontractorNotFoundException("lock()");
        }

        // read the record
        String[] values = null;
        try {
            values = db.read(recNo);
        } catch (final RecordNotFoundException e) {

            // release the lock and throw an exception
            unlock(
                recNo,
                cookie);

            throw new SubcontractorNotFoundException("read()");
        }

        // parse the existing 'owner' value
        long owner;
        try {
            owner = Long.parseLong(values[dbOwnerIndex]);
        } catch (final NumberFormatException e) {
            owner = 0;
        }

        // make sure the subcontractor is available
        if (0 != owner) {

            // release the lock and throw an exception
            unlock(
                recNo,
                cookie);

            throw new SubcontractorNotAvailableException();
        }

        // update the customer id
        values[dbOwnerIndex] = subcontractor
            .getCustomerId()
            .toString();

        // update the record
        try {
            db.update(
                recNo,
                values,
                cookie);
        } catch (final RecordNotFoundException e) {

            // release the lock and throw an exception
            unlock(
                recNo,
                cookie);

            throw new SubcontractorNotFoundException("update()");
        }

        // unlock the record
        unlock(
            recNo,
            cookie);
    }

    /**
     * Unlock a record.
     *
     * @param recNo
     * the record number; 0-based.
     *
     * @param cookie
     * the cookie that uniquely identifies a lock.
     *
     * @throws SubcontractorNotFoundException
     * if a requested <code>Subcontractor</code> does not exist.
     */
    private void unlock(final int recNo, final long cookie)
        throws SubcontractorNotFoundException {

        try {
            db.unlock(
                recNo,
                cookie);
        } catch (final RecordNotFoundException e) {
            throw new SubcontractorNotFoundException("unlock()");
        }
    }
}

