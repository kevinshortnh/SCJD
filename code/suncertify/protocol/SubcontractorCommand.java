/*
 * @(#)SubcontractorCommand.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The <code>SubcontractorCommand</code> interface defines
 * methods for invoking the commands that operate on
 * subcontractors.
 *
 * @version 1.0
 * @author Kevin Short
 */
public interface SubcontractorCommand extends Remote {

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
    Subcontractor[] find(String subcontractorName, String city)
        throws RemoteException;

    /**
     * Book the specified <code>Subcontractor</code>.
     *
     * This method assumes the <code>Subcontractor</code>
     * object's <code>customerId</code> field has been set to the
     * desired value.
     *
     * @param subcontractor
     * the <code>Subcontractor</code> to book.
     *
     * @throws RemoteException
     * if an attempt to export a remote object fails.
     *
     * @throws SubcontractorNotAvailableException
     * if the subcontractor is not available.
     *
     * @throws SubcontractorNotFoundException
     * if a requested <code>Subcontractor</code> does not exist.
     */
    void book(Subcontractor subcontractor)
        throws RemoteException,
        SubcontractorNotAvailableException,
        SubcontractorNotFoundException;
}

