/*
 * @(#)SubcontractorCommandClient.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.protocol.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import suncertify.db.Data;
import suncertify.db.InvalidDataHeaderException;
import suncertify.db.InvalidMagicCookieException;
import suncertify.protocol.SubcontractorCommand;
import suncertify.protocol.SubcontractorCommandImpl;
import suncertify.protocol.server.SubcontractorCommandServer;

/**
 * The <code>SubcontractorCommandClient</code> class implements
 * the code for initializing local and remote
 * <code>SubcontractorCommand</code> clients.
 *
 * @version 1.0
 * @author Kevin Short
 */
public final class SubcontractorCommandClient {

    /**
     * Hide the no-argument constructor.
     */
    private SubcontractorCommandClient() {

        // empty
    }

    /**
     * Create a remote <code>SubcontractorCommand</code> client.
     *
     * @param hostname
     * the name of the host on which the server is running.
     *
     * @return
     * a reference to the new <code>SubcontractorCommand</code>
     * object.
     *
     * @throws RemoteException
     * if a communication-related exceptions occurred during the
     * execution of a remote method call.
     *
     * @throws NotBoundException
     * if an attempt is made to lookup or unbind in the registry
     * a name that has no associated binding.
     *
     * @throws UnknownHostException
     * to indicate that the IP address of a host could not be
     * determined.
     */
    public static SubcontractorCommand remote(
        final String hostname) throws RemoteException,
        NotBoundException, UnknownHostException {

        // validate arguments
        if (null == hostname) {
            throw new NullPointerException();
        }

        // build RMI server URL
        final String url = SubcontractorCommandServer
            .buildURL(hostname);

        // look up the server
        final Registry registry = LocateRegistry
            .getRegistry(hostname);

        return (SubcontractorCommand) registry.lookup(url);
    }

    /**
     * Create a local <code>SubcontractorCommand</code> client.
     *
     * @param database
     * the name of the database file.
     *
     * @return
     * a reference to the new <code>SubcontractorCommand</code>
     * object.
     *
     * @throws InvalidMagicCookieException
     * if a database file magic cookie does not match the
     * expected value.
     *
     * @throws InvalidDataHeaderException
     * if a database header is invalid.
     *
     * @throws IOException
     * for all other I/O exceptions.
     */
    public static SubcontractorCommand local(
        final String database)
        throws InvalidMagicCookieException,
        InvalidDataHeaderException, IOException {

        // validate arguments
        if (null == database) {
            throw new NullPointerException();
        }

        return new SubcontractorCommandImpl(new Data(database));
    }
}

