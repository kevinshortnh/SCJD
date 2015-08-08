/*
 * @(#)CSR.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.app;

import suncertify.protocol.server.SubcontractorCommandServer;

/**
 * The <code>CSR</code> class is the Customer Service
 * Representative (CSR) application for Bodgitt and Scarper, LLC.
 *
 * @version 1.0
 * @author Kevin Short
 */
public final class CSR {

    /**
     * Hide the no-argument constructor.
     */
    private CSR() {

        super();
    }

    /**
     * This is the main() method for the application (server,
     * local and remote clients) of the Customer Service
     * Representative (CSR) Graphical User Interface (GUI) for
     * Bodgitt and Scarper, LLC.
     *
     * <h4>Command line:</h4>
     *
     * <pre>java -jar &lt;path_and_filename> [&lt;mode>]</pre>
     *
     * <p>where <i>&lt;mode></i> values can be:</p>
     *
     * <dl>
     * <dt><pre>server</pre></dt>
     * <dd>run the server.</dd>
     * <dt><pre>alone</pre></dt>
     * <dd>run the client in standalone mode
     * (no networking).</dd>
     * <dt><pre>&lt;omitted></pre></dt>
     * <dd>run the client in networked mode.</dd>
     * </dl>
     *
     * @param args
     * the command line arguments.
     */
    public static void main(final String[] args) {

        // validate arguments
        if (null == args) {
            throw new NullPointerException();
        }

        if (0 == args.length) {
            new CSRGUI(CSRGUI.NETWORKED_CLIENT);
        } else if (1 == args.length) {
            if ("server".equals(args[0])) {
                SubcontractorCommandServer.server();
            } else if ("alone".equals(args[0])) {
                new CSRGUI(CSRGUI.STANDALONE_CLIENT);
            } else {
                showUsage();
                System.exit(1);
            }
        } else {
            showUsage();
            System.exit(1);
        }
    }

    /**
     * Show command line usage.
     */
    private static void showUsage() {

        System.err
            .println("usage: java -jar <jarfile> [server|alone]");
    }
}

