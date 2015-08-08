/*
 * @(#)DataTest.java 1.0 04/04/01
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
 * This is a collection tests that should be performed on a
 * writable database.
 *
 * @version 1.0
 * @author Kevin Short
 */
public class DataTest extends TestCase {

    // use this object for most tests
    static Data o = null;

    /**
     * Bogus javadoc comment.
     * 
     * @return
     * the new <code>TestSetup</code> object.
     */
    public static TestSetup suite() {

        final TestSetup setup = new TestSetup(new TestSuite(
            DataTest.class)) {

            protected void setUp() {

                // empty
            }
        };

        return setup;
    }

    // ----------------------------------------------------------

    /**
     * Bogus javadoc comment.
     */
    public void testData() {

        try {
            o = new Data("C:/tmp/db-2x1.db");
        } catch (final InvalidMagicCookieException e) {
            e.printStackTrace();
            fail();
        } catch (final InvalidDataHeaderException e) {
            e.printStackTrace();
            fail();
        } catch (final IOException e) {
            e.printStackTrace();
            fail();
        }
        assertNotNull(o);
    }

    /**
     * Bogus javadoc comment.
     */
    public void testDataNullPointerException() {

        System.err
            .println("Expect a java.lang.NullPointerException");

        try {
            new Data(null);
            fail();
        } catch (final InvalidMagicCookieException e) {
            e.printStackTrace();
            fail();
        } catch (final InvalidDataHeaderException e) {
            e.printStackTrace();
            fail();
        } catch (final IOException e) {
            e.printStackTrace();
            fail();
        } catch (final NullPointerException e) {
            // this is what we expect
            e.printStackTrace();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testDataFileNotFoundException() {

        System.err
            .println("Expect a java.io.FileNotFoundException");

        try {
            new Data("foobar");
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

    /**
     * Bogus javadoc comment.
     */
    public void testDataInvalidMagicCookieException() {

        System.err
            .println("Expect a suncertify.db.InvalidMagicCookieException");

        try {
            new Data("C:/tmp/db-2x1.db-badCookie");
            fail();
        } catch (final InvalidMagicCookieException e) {
            // this is what we expect
            e.printStackTrace();
        } catch (final InvalidDataHeaderException e) {
            e.printStackTrace();
            fail();
        } catch (final IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testDataInvalidDataHeaderException() {

        System.err
            .println("Expect a suncertify.db.InvalidDataHeaderException");

        try {
            new Data("C:/tmp/db-2x1.db-badHeader");
            fail();
        } catch (final InvalidMagicCookieException e) {
            e.printStackTrace();
            fail();
        } catch (final InvalidDataHeaderException e) {
            // this is what we expect
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testDataEOFException() {

        System.err.println("Expect a java.io.EOFException");

        try {
            new Data("C:/tmp/db-2x1.db-eof");
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

    /**
     * Bogus javadoc comment.
     */
    public void testDataNoData() {

        Data dbImpl = null;
        try {
            dbImpl = new Data("C:/tmp/db-2x1.db-noData");
        } catch (final InvalidMagicCookieException e) {
            e.printStackTrace();
            fail();
        } catch (final InvalidDataHeaderException e) {
            e.printStackTrace();
            fail();
        } catch (final IOException e) {
            e.printStackTrace();
            fail();
        }
        assertNotNull(dbImpl);
    }

    // ----------------------------------------------------------

    /**
     * Bogus javadoc comment.
     */
    public void testReadRecordNotFoundException() {

        System.err
            .println("Expect a suncertify.db.RecordNotFoundException");

        try {
            o.read(-1);
            fail();
        } catch (final RecordNotFoundException e) {
            // this is what we expect
            e.printStackTrace();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testRead() {

        final int[] recNos = o.find(new String[] {
            null,
            null
        });

        for (int i = 0; i < recNos.length; i++) {

            String[] values = null;
            try {
                values = o.read(recNos[i]);
            } catch (final RecordNotFoundException e) {
                e.printStackTrace();
                fail();
            }

            String line = "Record " + recNos[i] + ": ";
            for (int j = 0; j < values.length; j++) {
                line += values[j].trim() + "|";
            }
            System.err.println(line);
        }
    }

    // ----------------------------------------------------------

    /**
     * Bogus javadoc comment.
     */
    public void testLockRecordNotFound() {

        System.err
            .println("Expect a suncertify.db.RecordNotFoundException");

        try {
            o.lock(-1);
            fail();
        } catch (final RecordNotFoundException e) {
            // this is what we expect
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------

    /**
     * Bogus javadoc comment.
     */
    public void testUnlockSecurityException() {

        // lock a record
        int recNo = 0;
        long cookie = 0;
        try {
            cookie = o.lock(recNo);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // unlock the wrong record
        System.err
            .println("Expect a java.lang.SecurityException");
        try {
            o.unlock(
                recNo + 1,
                cookie);
            fail();
        } catch (final SecurityException e) {
            // this is what we expect
            e.printStackTrace();
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // unlock the correct record
        try {
            o.unlock(
                recNo,
                cookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testUnlockLockCookieNotFound() {

        // lock a record
        int recNo = 0;
        long cookie = 0;
        try {
            cookie = o.lock(recNo);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // unlock the wrong cookie
        System.err
            .println("Expect a java.lang.SecurityException");
        try {
            o.unlock(
                recNo,
                cookie + 1);
            fail();
        } catch (final SecurityException e) {
            // this is what we expect
            e.printStackTrace();
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // unlock the correct record
        try {
            o.unlock(
                recNo,
                cookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }

    // ----------------------------------------------------------

    /**
     * Bogus javadoc comment.
     */
    public void testCreateNullPointerException() {

        System.err
            .println("Expect a java.lang.NullPointerException");

        try {
            o.create(null);
            fail();
        } catch (final NullPointerException e) {
            // this is what we expect
            e.printStackTrace();
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testCreateTooFewValues() {

        System.err
            .println("Expect a java.lang.IllegalArgumentException");

        try {
            o.create(new String[] {
                "field1",
                "field2",
                "field3",
                "field4",
                "field5"
            });
            fail();
        } catch (final IllegalArgumentException e) {
            // this is what we expect
            e.printStackTrace();
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testCreateTooManyValues() {

        System.err
            .println("Expect a java.lang.IllegalArgumentException");

        try {
            o.create(new String[] {
                "field1",
                "field2",
                "field3",
                "field4",
                "field5",
                "field6",
                "field7"
            });
            fail();
        } catch (final IllegalArgumentException e) {
            // this is what we expect
            e.printStackTrace();
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testCreateDuplicateKey() {

        // create a record
        int recNo = Integer.MIN_VALUE;

        try {
            recNo = o.create(new String[] {
                "field1",
                "field2",
                "field3",
                "field4",
                "field5",
                "field6"
            });
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            fail();
        }
        assert (recNo >= 0);

        // try to create a record with a duplicate key
        System.err
            .println("Expect a suncertify.db.DuplicateKeyException");

        try {
            o.create(new String[] {
                "field1",
                "field2",
                "field3",
                "field4",
                "field5",
                "field6"
            });
            fail();
        } catch (DuplicateKeyException e) {
            // this is what we expect
            e.printStackTrace();
        }

        // lock the first record
        long cookie = 0;

        try {
            cookie = o.lock(recNo);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // delete the first record
        try {
            o.delete(
                recNo,
                cookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // unlock the first record
        try {
            o.unlock(
                recNo,
                cookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }

    // ----------------------------------------------------------

    /**
     * Bogus javadoc comment.
     */
    public void testUpdateRecordNotFoundException() {

        System.err
            .println("Expect a suncertify.db.RecordNotFoundException");

        try {
            o.update(
                -1,
                new String[] {
                    "hello"
                },
                0x12345678L);
            fail();
        } catch (final RecordNotFoundException e) {
            // this is what we expect
            e.printStackTrace();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testUpdateNullPointerException() {

        System.err
            .println("Expect a java.lang.NullPointerException");

        try {
            o.update(
                0,
                null,
                -1);
            fail();
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        } catch (final NullPointerException e) {
            // this is what we expect
            e.printStackTrace();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testUpdateDuplicateKeyOnUpdateException() {

        // create 'unique' record
        int unique = Integer.MIN_VALUE;
        try {
            unique = o.create(new String[] {
                "unique1",
                "unique2",
                "unique3",
                "unique4",
                "unique5",
                "unique6"
            });
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            fail();
        }
        assert (unique >= 0);

        // create 'another' record
        int another = Integer.MIN_VALUE;
        try {
            another = o.create(new String[] {
                "another1",
                "another2",
                "another3",
                "another4",
                "another5",
                "another6"
            });
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            fail();
        }
        assert (another >= 0);

        long cookie = 0;

        // lock 'another' record
        try {
            cookie = o.lock(another);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // update 'another' record, with a duplicate key
        System.err
            .println("Expect a suncertify.db.DuplicateKeyOnUpdateException");
        try {
            o.update(
                another,
                new String[] {
                    "unique1",
                    "unique2",
                    "new3",
                    "new4",
                    "new5",
                    "new6"
                },
                cookie);
            fail();
        } catch (final DuplicateKeyOnUpdateException e) {
            // this is what we expect
            e.printStackTrace();
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // delete 'another' record
        try {
            o.delete(
                another,
                cookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // unlock 'another' record
        try {
            o.unlock(
                another,
                cookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // lock 'unique' record
        try {
            cookie = o.lock(unique);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // delete 'unique' record
        try {
            o.delete(
                unique,
                cookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // unlock 'unique' record
        try {
            o.unlock(
                unique,
                cookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testUpdateSecurityException() {

        System.err
            .println("Expect a java.lang.SecurityException");

        final int recNo = 28;
        final String[] data = new String[] {
            "Moore Power Tool Ya",
            "Lendmarch",
            "Electrical, Heating, Glass",
            "8",
            "$95.00",
            ""
        };
        final long lockCookie = -1;

        try {
            o.update(
                recNo,
                data,
                lockCookie);
            fail();
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        } catch (final SecurityException e) {
            // this is what we expect
            e.printStackTrace();
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testUpdate() {

        final int recNo = 28;
        final String[] data = new String[] {
            "Moore Power Tool Ya",
            "Lendmarch",
            "Electrical, Heating, Glass",
            "8",
            "$95.00",
            ""
        };
        long lockCookie = -1;

        try {
            lockCookie = o.lock(recNo);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        try {
            o.update(
                recNo,
                data,
                lockCookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        try {
            o.unlock(
                recNo,
                lockCookie);
        } catch (final RecordNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }

    // ----------------------------------------------------------

    /**
     * Bogus javadoc comment.
     */
    public void testFindStringString() {

        final String[] criteria = new String[] {
            "Swan",
            "Atl"
        };
        final int[] expected = new int[] {
            12
        };

        final int[] recNos = o.find(criteria);
        assertEquals(
            expected.length,
            recNos.length);

        for (int i = 0; i < recNos.length; i++) {
            assertEquals(
                expected[i],
                recNos[i]);
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testFindNullString() {

        final String[] criteria = new String[] {
            null,
            "Atl"
        };
        final int[] expected = new int[] {
            11,
            12,
            13
        };

        final int[] recNos = o.find(criteria);
        assertEquals(
            expected.length,
            recNos.length);

        for (int i = 0; i < recNos.length; i++) {
            assertEquals(
                expected[i],
                recNos[i]);
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testFindStringNull() {

        final String[] criteria = new String[] {
            "Swan",
            null
        };
        final int[] expected = new int[] {
            1,
            12,
            22
        };

        final int[] recNos = o.find(criteria);
        assertEquals(
            expected.length,
            recNos.length);

        for (int i = 0; i < recNos.length; i++) {
            assertEquals(
                expected[i],
                recNos[i]);
        }
    }

    /**
     * Bogus javadoc comment.
     */
    public void testFindNullNull() {

        final String[] criteria = new String[] {
            null,
            null
        };
        final int[] expected = new int[] {
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24,
            25,
            26,
            27,
            28
        };

        final int[] recNos = o.find(criteria);
        assertEquals(
            expected.length,
            recNos.length);

        for (int i = 0; i < recNos.length; i++) {
            assertEquals(
                expected[i],
                recNos[i]);
        }
    }

    // ----------------------------------------------------------

    /**
     * Bogus javadoc comment.
     */
    public void testThreads() {

        final int recNo = 5;

        final Thread[] x = new Thread[100];

        Thread a = new Thread("a") {

            public void run() {

                synchronized (System.err) {
                    System.err.println("'a' started");
                }

                long cookie = 0;
                try {
                    cookie = o.lock(recNo);
                } catch (RecordNotFoundException e) {
                    e.printStackTrace();
                    fail();
                }
                synchronized (System.err) {
                    System.err.println("'a'  locked " + recNo);
                }

                try {
                    sleep(5000);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    o.unlock(
                        recNo,
                        cookie);
                } catch (final RecordNotFoundException e) {
                    e.printStackTrace();
                    fail();
                }
                synchronized (System.err) {
                    System.err.println("'a' unlocks " + recNo);
                }

                synchronized (System.err) {
                    System.err.println("'a' is done");
                }
            }
        };

        Thread b = new Thread("b") {

            public void run() {

                synchronized (System.err) {
                    System.err.println("'b' started");
                }

                int[] recNos = new int[] {
                    1,
                    3,
                    5,
                    7,
                    9,
                    11
                };
                long[] cookies = new long[recNos.length];

                for (int i = 0; i < recNos.length; i++) {

                    try {
                        cookies[i] = o.lock(recNos[i]);
                    } catch (RecordNotFoundException e) {
                        e.printStackTrace();
                        fail();
                    }
                    synchronized (System.err) {
                        System.err.println("'b'  locked "
                            + recNos[i]);
                    }
                }

                try {
                    sleep(1000);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < recNos.length; i++) {
                    try {
                        o.unlock(
                            recNos[i],
                            cookies[i]);
                    } catch (final RecordNotFoundException e) {
                        e.printStackTrace();
                        fail();
                    }
                    synchronized (System.err) {
                        System.err.println("'b' unlocks "
                            + recNos[i]);
                    }
                }

                synchronized (System.err) {
                    System.err.println("'b' is done");
                }
            }
        };

        Thread c = new Thread("c") {

            public void run() {

                synchronized (System.err) {
                    System.err.println("'c' started");
                }

                int[] recNos = new int[] {
                    1,
                    3,
                    5,
                    7,
                    9,
                    11
                };
                long[] cookies = new long[recNos.length];

                for (int i = 0; i < recNos.length; i++) {

                    try {
                        cookies[i] = o.lock(recNos[i]);
                    } catch (RecordNotFoundException e) {
                        e.printStackTrace();
                        fail();
                    }
                    synchronized (System.err) {
                        System.err.println("'c'  locked "
                            + recNos[i]);
                    }
                }

                for (int i = 0; i < recNos.length; i++) {
                    try {
                        o.unlock(
                            recNos[i],
                            cookies[i]);
                    } catch (final RecordNotFoundException e) {
                        e.printStackTrace();
                        fail();
                    }
                    synchronized (System.err) {
                        System.err.println("'c' unlocks "
                            + recNos[i]);
                    }
                }

                synchronized (System.err) {
                    System.err.println("'c' is done");
                }
            }
        };

        for (int i = 0; i < x.length; i++) {

            x[i] = new Thread("" + i) {

                public void run() {

                    synchronized (System.err) {
                        System.err.println("'"
                            + Thread.currentThread().getName()
                            + "' started");
                    }

                    long cookie = 0;

                    try {
                        cookie = o.lock(recNo);
                    } catch (RecordNotFoundException e) {
                        e.printStackTrace();
                        fail();
                    }
                    synchronized (System.err) {
                        System.err.println("'"
                            + Thread.currentThread().getName()
                            + "'  locked "
                            + recNo);
                    }

                    try {
                        o.unlock(
                            recNo,
                            cookie);
                    } catch (final RecordNotFoundException e) {
                        e.printStackTrace();
                        fail();
                    }
                    synchronized (System.err) {
                        System.err.println("'"
                            + Thread.currentThread().getName()
                            + "' unlocks "
                            + recNo);
                    }

                    synchronized (System.err) {
                        System.err.println("'"
                            + Thread.currentThread().getName()
                            + "' is done");
                    }
                }
            };
        }

        a.start();
        b.start();
        c.start();

        for (int i = 0; i < x.length; i++) {
            x[i].start();
        }

        try {
            a.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        try {
            b.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        try {
            c.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < x.length; i++) {
            try {
                x[i].join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

