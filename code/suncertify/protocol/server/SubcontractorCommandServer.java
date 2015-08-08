/*
 * @(#)SubcontractorCommandServer.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.protocol.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

import suncertify.db.DB;
import suncertify.db.Data;
import suncertify.db.InvalidDataHeaderException;
import suncertify.db.InvalidMagicCookieException;
import suncertify.protocol.SubcontractorCommand;
import suncertify.protocol.SubcontractorCommandImpl;

/**
 * The <code>SubcontractorCommandServer</code> class implements
 * the server code for accessing <code>Subcontractor</code>
 * objects via the <code>SubcontractorCommand</code> interface.
 *
 * @version 1.0
 * @author Kevin Short
 */
public final class SubcontractorCommandServer {

    /** System.exit() codes for UnknownHostException. */
    private static final int UNKNOWN_HOST_EXCEPTION = 1;

    /** System.exit() codes for RemoteException. */
    private static final int REMOTE_EXCEPTION = 2;

    /** System.exit() codes for IOException. */
    private static final int IO_EXCEPTION = 3;

    /** System.exit() codes for InvalidMagicCookieException. */
    private static final int INVALID_MAGIC_COOKIE_EXCEPTION = 4;

    /** System.exit() codes for InvalidDataHeaderException. */
    private static final int INVALID_DATA_HEADER_EXCEPTION = 5;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger
        .getLogger(SubcontractorCommandServer.class.getName());

    /** Database filename. */
    private static final String DATABASE_NAME = "db-2x1.db";

    /** Service name. */
    private static final String SERVICE_NAME = "SCJD-Subcontractor";

    /**
     * Hide the no-argument constructor.
     */
    private SubcontractorCommandServer() {

        // empty
    }

    /**
     * Run the server.
     */
    public static void server() {

        // get local hostname
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (final UnknownHostException e) {
            LOGGER.severe("Exiting --" + e.toString());
            System.exit(UNKNOWN_HOST_EXCEPTION);
        }
        final String hostname = inetAddress.getHostName();

        // build RMI server URL
        String url = null;
        try {
            url = buildURL(hostname);
        } catch (final UnknownHostException e) {
            LOGGER.severe("Exiting --" + e.toString());
            System.exit(UNKNOWN_HOST_EXCEPTION);
        }

        // open the database
        DB db = null;
        try {
            db = new Data(DATABASE_NAME);
        } catch (final InvalidMagicCookieException e) {
            LOGGER.severe("Exiting -- '"
                + DATABASE_NAME
                + "', "
                + e.toString());
            System.exit(INVALID_MAGIC_COOKIE_EXCEPTION);
        } catch (final InvalidDataHeaderException e) {
            LOGGER.severe("Exiting -- '"
                + DATABASE_NAME
                + "', "
                + e.toString());
            System.exit(INVALID_DATA_HEADER_EXCEPTION);
        } catch (final IOException e) {
            LOGGER.severe("Exiting -- '"
                + DATABASE_NAME
                + "', "
                + e.toString());
            System.exit(IO_EXCEPTION);
        }

        // create the server object
        SubcontractorCommand server = null;
        try {
            server = new SubcontractorCommandImpl(db);
        } catch (final RemoteException e) {
            LOGGER.severe("Exiting -- " + e.toString());
            System.exit(REMOTE_EXCEPTION);
        }

        /* Create and export a Registry on the local
         * host that accepts requests on the standard port.
         */
        Registry registry = null;
        try {
            registry = LocateRegistry
                .createRegistry(Registry.REGISTRY_PORT);
        } catch (final RemoteException e) {
            /* could not create the registry;
             * we will try another way (below) so ignore
             */
            assert null != e;
        }

        /* If we were not able to create a new registry, maybe
         * one already exists.
         */
        if (null == registry) {
            try {
                registry = LocateRegistry.getRegistry();
            } catch (final RemoteException e) {
                LOGGER.severe("Exiting -- " + e.toString());
                System.exit(REMOTE_EXCEPTION);
            }
        }

        // bind the name to the remote object
        try {
            registry.bind(
                url,
                server);
        } catch (final RemoteException e) {
            LOGGER.severe("Exiting -- " + e.toString());
            System.exit(REMOTE_EXCEPTION);
        } catch (AlreadyBoundException e) {
            LOGGER.severe("Exiting -- " + e.toString());
            System.exit(REMOTE_EXCEPTION);
        }

        // success
        LOGGER.info(url + " registered.");
    }

    /**
     * Build a server URL.
     *
     * The server URL is expressed using the numeric IP address
     * of the specified host, as the IP address for the host is
     * unique but the IP hostname may be the default name or an
     * alias. This should make lookups more successful.
     *
     * @param hostname
     * the host name where the server is installed.
     *
     * @return
     * the server URL for the specified hostname.
     *
     * @throws UnknownHostException
     * to indicate that the IP address of a host could not be
     * determined.
     */
    public static String buildURL(final String hostname)
        throws UnknownHostException {

        // validate arguments
        if (null == hostname) {
            throw new NullPointerException();
        }

        final InetAddress inetAddress = InetAddress
            .getByName(hostname);

        final String address = inetAddress.getHostAddress();

        return "rmi://"
            + address
            + ":"
            + Registry.REGISTRY_PORT
            + "/"
            + SERVICE_NAME;
    }
}

