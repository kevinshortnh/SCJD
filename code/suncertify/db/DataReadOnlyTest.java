/*
 * @(#)DataReadOnlyTest.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

import java.io.IOException;
import junit.extensions.TestSetup;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This is a collection of tests that should fail when the
 * database file is read-only.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class DataReadOnlyTest extends TestCase {

    // use this object for most tests
    static DB o = null;

    /**
     * 
     * @return
     * the new <code>TestSetup</code> object.
     */
    public static TestSetup suite() {

        final TestSetup setup = new TestSetup(new TestSuite(
            DataReadOnlyTest.class)) {

            protected void setUp() {

                // empty
            }
        };

        return setup;
    }

    // ----------------------------------------------------------

    /**
     * 
     */
    public void testData() {

        System.err
            .println("Expect a java.io.FileNotFoundException");

        try {
            o = new Data("C:/tmp/db-2x1.db");
            fail();
        } catch (final InvalidMagicCookieException e) {
            e.printStackTrace();
            fail();
        } catch (final InvalidDataHeaderException e) {
            e.printStackTrace();
            fail();
        } catch (final IOException e) {
            // this is what we expect
            e.printStackTrace();
        }
    }
}

