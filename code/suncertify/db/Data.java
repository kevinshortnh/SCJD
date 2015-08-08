/*
 * @(#)Data.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * The <code>Data</code> class implements the <code>DB</code>
 * interface.
 * <h2>
 * Data file format
 * </h2>
 * <p>
 * The format of data in the database file is as follows:
 * </p>
 * <h3>
 * Start of file
 * </h3>
 * <ul>
 * <li>
 * 4 byte numeric, magic cookie value.
 * Identifies this as a data file.
 * </li>
 * <li>
 * 4 byte numeric, total overall length in bytes of each record.
 * </li>
 * <li>
 * 2 byte numeric, number of fields in each record.
 * </li>
 * </ul>
 * <h3>Schema description section</h3>
 * <p>
 * Repeated for each field in a record:
 * </p>
 * <ul>
 * <li>
 * 2 byte numeric, length in bytes of field name.
 * </li>
 * <li>
 * n bytes (defined by previous entry), field name.
 * </li>
 * <li>
 * 2 byte numeric, field length in bytes.
 * </li>
 * <li>
 * End of repeating block.
 * </li>
 * </ul>
 * <h3>Data section</h3>
 * <p>
 * Repeat to end of file:
 * </p>
 * <ul>
 * <li>
 * 1 byte "deleted" flag.
 * 0 implies valid record, 1 implies deleted record.
 * </li>
 * <li>
 * Record containing fields in order specified in schema section,
 * no separators between fields, each field fixed length at
 * maximum specified in schema information.
 * </li>
 * </ul>
 * <h3>
 * End of file
 * </h3>
 * <p>
 * All numeric values are stored in the header information use
 * the formats of the <code>DataInputStream</code> and
 * <code>DataOutputStream</code> classes.
 * All text values, and all fields (which are text only), contain
 * only 8 bit characters, null terminated if less than the
 * maximum length for the field. The character encoding is 8 bit
 * US ASCII.
 * </p>
 * <h3>
 * Note:
 * </h3>
 * <p>
 * This class does not depend on any specific set of field names
 * and/or field lengths.
 * </p>
 *
 * @version 1.0
 * @author Kevin Short
 */
public final class Data implements DB {

    // class variables ------------------------------------------

    /**
     * The magic cookie in the database file must match this
     * value.
     */
    private static final int MAGIC_COOKIE = 0x00000201;

    /** Indicates a record is deleted. */
    private static final byte DELETED_RECORD = 1;

    /** Indicates a record is valid. */
    private static final byte VALID_RECORD = 0;

    // instance variables ---------------------------------------

    /** Random number generator. */
    private final Random random = new Random();

    /** Number of bytes per record. */
    private final int bytesPerRecord;

    /**
     * This array will be used for reading and writing all data
     * fields, using the discovered maximum data field length.
     */
    private final byte[] dataBytes;

    /** Byte offset for start of data records. */
    private int startOfData;

    /** Maximum data field length, in bytes. */
    private int maxFieldLength;

    /** Use random access for database updates. */
    private RandomAccessFile randomAccessFile;

    /** Array of all field descriptors. */
    private FieldDescriptor[] fieldDescriptors;

    /** List of all data records. */
    private List dataRecords = new ArrayList();

    /**
     * Hide the no-argument constructor.
     * The no argument contructor should never be used.
     */
    private Data() {

        /* Keep the compiler happy by initializing all final
         * instance variables -- even though this constructor is
         * never used.
         */
        bytesPerRecord = -1;
        dataBytes = null;
    }

    /**
     * <p>
     * Constructs a <code>Data</code>.
     * </p>
     * <p>
     * Reads the entire database into memory and initializes all
     * instance variables.
     * </p>
     *
     * @param filename
     * relative or absolute pathname for the database file.
     *
     * @throws InvalidMagicCookieException
     * if the magic cookie does not match the expected value.
     *
     * @throws InvalidDataHeaderException
     * if the database header is invalid.
     *
     * @throws IOException
     * for all other database I/O errors.
     */
    public Data(final String filename)
        throws InvalidMagicCookieException,
        InvalidDataHeaderException, IOException {

        // validate arguments
        if (null == filename) {
            throw new NullPointerException("filename");
        }

        final int bytesPerShort = 2;
        final int bytesPerInt = 4;

        // use a DataInputStream to read the database into memory
        final File file = new File(filename);
        final DataInputStream dataInputStream = new DataInputStream(
            new FileInputStream(file));

        // use a RandomAccessFile later, for database updates
        randomAccessFile = new RandomAccessFile(file, "rws");

        // Start of file ----------------------------------------

        // read magic cookie
        final int magicCookie = dataInputStream.readInt();
        startOfData += bytesPerInt;

        // validate magic cookie
        if (magicCookie != MAGIC_COOKIE) {
            throw new InvalidMagicCookieException(
                "cookie was 0x"
                    + Integer.toHexString(magicCookie)
                    + ", expected 0x"
                    + Integer.toHexString(MAGIC_COOKIE));
        }

        // read number of bytes per record
        bytesPerRecord = dataInputStream.readInt();
        startOfData += bytesPerInt;

        // read number of fields per record
        final short fieldsPerRecord = dataInputStream
            .readShort();
        startOfData += bytesPerShort;

        // Schema -----------------------------------------------

        // now we know how many fields per record
        fieldDescriptors = new FieldDescriptor[fieldsPerRecord];

        // read the field descriptors
        for (int i = 0; i < fieldsPerRecord; i++) {

            // read length in bytes of field name
            final short fieldNameLength = dataInputStream
                .readShort();
            startOfData += bytesPerShort;

            // read field name
            final byte[] fieldNameBytes = new byte[fieldNameLength];
            final int count = dataInputStream.read(
                fieldNameBytes,
                0,
                fieldNameLength);
            startOfData += fieldNameLength;
            final String fieldName = new String(fieldNameBytes);

            // make sure the counts match
            if (count != fieldNameLength) {
                throw new InvalidDataHeaderException(
                    "field name length was "
                        + count
                        + ", expected "
                        + fieldNameLength);
            }

            // read length in bytes of field
            final short fieldLength = dataInputStream
                .readShort();
            startOfData += bytesPerShort;

            // create a new FieldDescriptor for this field
            fieldDescriptors[i] = new FieldDescriptor(
                fieldName,
                fieldLength);

            // keep track of max field length, for later
            if (fieldLength > maxFieldLength) {
                maxFieldLength = fieldLength;
            }
        }

        // Data -------------------------------------------------

        // Create the data field buffer
        dataBytes = new byte[maxFieldLength];

        // read to end of file
        for (;;) {

            // read the 'deleted' byte
            final byte deletedByte;
            try {
                deletedByte = dataInputStream.readByte();
            } catch (EOFException e) {
                /* reached end of file, so we are done */
                break;
            }

            // create a String[] for the data record
            final String[] fieldValues = new String[fieldsPerRecord];

            // collect all fields in the record
            for (int i = 0; i < fieldsPerRecord; i++) {

                // read data value for one field
                final int count = dataInputStream.read(
                    dataBytes,
                    0,
                    fieldDescriptors[i].getDataLength());

                /* check that the counts match; there is probably
                 * no harm if they do not match, so just use an
                 * assertion as we learn more about the specific
                 * behavior
                 */
                assert count == fieldDescriptors[i]
                    .getDataLength() : "Read "
                    + count
                    + " bytes, expected "
                    + fieldDescriptors[i].getDataLength();

                /* convert bytes to String;
                 * enforce maximum field length, per schema
                 */
                fieldValues[i] = new String(
                    dataBytes,
                    0,
                    fieldDescriptors[i].getDataLength());
            }

            // append new data record
            dataRecords.add(new DataRecord(
                (0 != deletedByte),
                fieldValues));
        }
    }

    /**
     * Reads a record from the file.
     * Returns an array where each element is a record value.
     *
     * @param recNo
     * the record number; 0-based.
     *
     * @return
     * an array where each element is a record value.
     *
     * @throws RecordNotFoundException
     * if the specified record does not exist or is marked as
     * deleted in the database file.
     */
    public String[] read(final int recNo)
        throws RecordNotFoundException {

        // validate arguments
        if ((recNo < 0) || (recNo >= dataRecords.size())) {
            throw new RecordNotFoundException(
                "invalid record number " + recNo);
        }

        // get specified record
        DataRecord dataRecord = (DataRecord) dataRecords
            .get(recNo);

        // make sure record was not deleted
        if (dataRecord.isDeleted()) {
            throw new RecordNotFoundException(
                "deleted record number " + recNo);
        }

        /* return a new copy of the array of values;
         * we return a copy rather than the original, so the
         * caller can not change our copy
         */
        return (String[]) dataRecord.getValues().clone();
    }

    /**
     * Modifies the fields of a record.
     * The new value for field <code>n</code> appears in
     * <code>data[n]</code>.
     * Throws <code>SecurityException</code> if the record is
     * locked with a cookie other than <code>lockCookie</code>.
     *
     * @param recNo
     * the record number; 0-based.
     *
     * @param data
     * an array where each element is a record value;
     * the new value for field <code>n</code> appears in
     * <code>data[n]</code>.
     *
     * @param lockCookie
     * the cookie that uniquely identifies a lock.
     *
     * @throws RecordNotFoundException
     * if the specified record does not exist or is marked as
     * deleted in the database file.
     */
    public void update(
        final int recNo,
        final String[] data,
        final long lockCookie) throws RecordNotFoundException {

        // validate arguments
        if ((recNo < 0) || (recNo >= dataRecords.size())) {
            throw new RecordNotFoundException(
                "invalid record number " + recNo);
        }

        // validate arguments
        if (null == data) {
            throw new NullPointerException("data");
        }

        // get specified record
        final DataRecord dataRecord = (DataRecord) dataRecords
            .get(recNo);

        // make sure record was not deleted
        if (dataRecord.isDeleted()) {
            throw new RecordNotFoundException(
                "deleted record number " + recNo);
        }

        /* Ensure that the updated record would not create a
         * duplicate key condition.
         */
        for (int i = 0; i < dataRecords.size(); i++) {

            // do not compare with self
            if (recNo == i) {
                continue;
            }

            // get the record
            final DataRecord checkForDup = (DataRecord) dataRecords
                .get(i);

            // ignore if deleted
            if (checkForDup.isDeleted()) {
                continue;
            }

            final String[] values = checkForDup.getValues();

            /* For this implementation, the first two fields
             * in the record must be unique.
             */
            if ((data[0] == values[0]) && (data[1] == values[1])) {

                throw new DuplicateKeyOnUpdateException();
            }
        }

        // fetch the Lock identified by this cookie
        final Lock lock = dataRecord.findLock(lockCookie);
        assert null != lock;

        // update the file
        try {
            writeRecord(
                recNo,
                new DataRecord(false, data));
        } catch (final IOException e) {
            throw new IOExceptionOnUpdateException(
                "record number " + recNo,
                e);
        }

        /* Now that the data has been written to the
         * file, update the in-memory object. Use a
         * new copy of the updated data, as the
         * caller may later change the object passed
         * to us.
         */
        dataRecord.setValues((String[]) data.clone());
    }

    /**
     * Deletes a record, making the record number and associated
     * disk storage available for reuse.
     * Throws <code>SecurityException</code> if the record is
     * locked with a cookie other than <code>lockCookie</code>.
     *
     * @param recNo
     * the record number; 0-based.
     *
     * @param lockCookie
     * the cookie that uniquely identifies a lock.
     *
     * @throws RecordNotFoundException
     * if the specified record does not exist or is marked as
     * deleted in the database file.
     */
    public void delete(final int recNo, final long lockCookie)
        throws RecordNotFoundException {

        // validate arguments
        if ((recNo < 0) || (recNo >= dataRecords.size())) {
            throw new RecordNotFoundException(
                "invalid record number " + recNo);
        }

        // get specified record
        final DataRecord dataRecord = (DataRecord) dataRecords
            .get(recNo);

        // make sure record was not deleted
        if (dataRecord.isDeleted()) {
            throw new RecordNotFoundException(
                "deleted record number " + recNo);
        }

        // fetch the lock for this cookie
        final Lock lock = dataRecord.findLock(lockCookie);
        assert null != lock;

        // flag the record as deleted
        dataRecord.setDeleted(true);

        // update the file
        try {
            writeRecord(
                recNo,
                dataRecord);
        } catch (final IOException e) {

            /* The in-memory object is marked as deleted, which
             * should be fine even if we catch an exception here.
             */
            throw new IOExceptionOnDeleteException(
                "record number " + recNo,
                e);
        }
    }

    /**
     * Returns an array of record numbers that match the
     * specified criteria.
     * Field <code>n</code> in the database file is described by
     * <code>criteria[n]</code>.
     * A <code>null</code> value in <code>criteria[n]</code>
     * matches any field value.
     * A non-<code>null</code> value in <code>criteria[n]</code>
     * matches any field value that begins with
     * <code>criteria[n]</code>.\
     * The searches ignore upper and lower case.
     * (For example, <i>"Fred"</i> matches <i>"Fred"</i> or
     * <i>"Freddy"</i> or <i>"fred"</i>.)
     *
     * @param criteria
     * match criteria; <code>null</code> matches any value.
     *
     * @return
     * an array of record numbers that match the specified
     * criteria;
     * <code>null</code> if the were no matches.
     */
    public int[] find(final String[] criteria) {

        // validate arguments
        if (null == criteria) {
            throw new NullPointerException();
        }

        final List arrayList = new ArrayList();

        /* build a set of match strings that will ignore case and
         * match string prefixes.
         */
        final String[] patterns = new String[criteria.length];
        for (int i = 0; i < patterns.length; i++) {

            if (null == criteria[i]) {
                patterns[i] = null;
            } else {
                patterns[i] = "(?i)" + criteria[i] + ".*";
            }
        }

        // examine all records
        nextRecord: for (int recNo = 0; recNo < dataRecords
            .size(); recNo++) {

            // get values for current record
            final DataRecord dataRecord = (DataRecord) dataRecords
                .get(recNo);

            // ignore if deleted
            if (dataRecord.isDeleted()) {
                continue;
            }

            final String[] values = dataRecord.getValues();

            /* there should never be more 'patterns' than data
             * 'values'
             */
            assert values.length >= patterns.length;

            // test each value
            for (int i = 0; i < patterns.length; i++) {

                if ((null != patterns[i])
                    && !values[i].matches(patterns[i])) {

                    // match failed, so try next record
                    continue nextRecord;
                }
            }

            // this record matched
            arrayList.add(new Integer(recNo));
        }

        // copy values to an int[]
        final int[] intArray = new int[arrayList.size()];

        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = ((Integer) arrayList.get(i))
                .intValue();
        }

        return intArray;
    }

    /**
     * Creates a new record in the database (possibly reusing a
     * deleted entry).
     * Inserts the given data, and returns the record number of
     * the new record.
     *
     * @param data
     * an array where each element is a record value;
     * the new value for field <code>n</code> appears in
     * <code>data[n]</code>.
     *
     * @return
     * record number of the new record,
     * or -1 if there is an IOException.
     *
     * @throws DuplicateKeyException
     * if the fields in the given data would create a duplicate
     * key condition.
     */
    public int create(final String[] data)
        throws DuplicateKeyException {

        // validate arguments
        if (null == data) {
            throw new NullPointerException("data");
        }

        // make sure the correct number of fields were supplied
        if (data.length != fieldDescriptors.length) {
            throw new IllegalArgumentException(
                "field count was "
                    + data.length
                    + ", expected "
                    + fieldDescriptors.length);
        }

        int recNo;
        DataRecord dataRecord;

        /* Synchronize this block, to prevent concurrent
         * 'create' operations.
         *
         * This is necessary, to guarantee that the check for
         * duplicate keys and the updating of an old record or
         * the adding of a new record, is treated as a single,
         * atomic operation.
         */
        synchronized (dataRecords) {

            /* Ensure that the new record would not create a
             * duplicate key condition.
             *
             * Note: We use an Iterator for this while loop, as
             * we do not need to track the record number. This
             * should be more efficient that using the get()
             * method of the List interface.
             */
            final Iterator iterator = dataRecords.iterator();
            while (iterator.hasNext()) {

                dataRecord = (DataRecord) iterator.next();

                // ignore if deleted
                if (dataRecord.isDeleted()) {
                    continue;
                }

                final String[] values = dataRecord.getValues();

                /* For this implementation, the first two fields
                 * in the record must be unique.
                 */
                if ((data[0] == values[0])
                    && (data[1] == values[1])) {

                    throw new DuplicateKeyException();
                }
            }

            // the new record has a unique key, so add it
            for (recNo = 0; recNo < dataRecords.size(); recNo++) {

                dataRecord = (DataRecord) dataRecords.get(recNo);

                // re-use a deleted slot if possible
                if (dataRecord.isDeleted()) {

                    // update the copy on disk
                    try {
                        writeRecord(
                            recNo,
                            new DataRecord(false, data));
                    } catch (final IOException e) {
                        throw new CreateFailedException(
                            "unable to re-use deleted record number "
                                + recNo,
                            e);
                    }

                    /* Now that the data has been written to the
                     * file, update the in-memory object. Use a
                     * new copy of the updated data, as the
                     * caller may later change the object passed
                     * to us.
                     */
                    dataRecord
                        .setValues((String[]) data.clone());

                    // the new record is not deleted
                    dataRecord.setDeleted(false);

                    return recNo;
                }
            }

            // append a new data record to the file
            dataRecord = new DataRecord(false, (String[]) data
                .clone());

            try {
                writeRecord(
                    recNo,
                    dataRecord);
            } catch (final IOException e) {
                throw new CreateFailedException(
                    "unable to create new record number "
                        + recNo,
                    e);
            }

            // append to the list in memory
            dataRecords.add(dataRecord);

            return recNo;
        }
    }

    /**
     * Locks a record so that it can only be updated or deleted
     * by this client.
     * Returned value is a cookie that must be used when the
     * record is unlocked, updated, or deleted. If the specified
     * record is already locked by a different client, the
     * current thread gives up the CPU and consumes no CPU cycles
     * until the record is unlocked.
     *
     * @param recNo
     * the record number; 0-based.
     *
     * @return
     * a cookie that must be used when the record is unlocked,
     * updated, or deleted
     *
     * @throws RecordNotFoundException
     * if the specified record does not exist or is marked as
     * deleted in the database file.
     */
    public long lock(final int recNo)
        throws RecordNotFoundException {

        // validate arguments
        if ((recNo < 0) || (recNo >= dataRecords.size())) {
            throw new RecordNotFoundException(
                "invalid record number " + recNo);
        }

        // get specified record
        final DataRecord dataRecord = (DataRecord) dataRecords
            .get(recNo);

        // make sure record was not deleted
        if (dataRecord.isDeleted()) {
            throw new RecordNotFoundException(
                "deleted record number " + recNo);
        }

        // create a new 'Lock' object for this record
        final Lock newLock = new Lock(random.nextLong());

        // synchronize access to this data record
        synchronized (dataRecord) {

            // add new lock for this data record
            dataRecord.addLock(newLock);

            // keep trying until we get the lock
            tryAgain: while (true) {

                // see if another thread has the lock
                final Iterator iterator = dataRecord
                    .getLockIterator();
                while (iterator.hasNext()) {

                    final Lock lock = (Lock) iterator.next();
                    if (null != lock.getOwner()) {

                        // another thread owns the lock
                        try {
                            // wait until notified
                            dataRecord.wait();
                        } catch (InterruptedException e) {
                            // nothing to do here
                            e.printStackTrace();
                        }

                        continue tryAgain;
                    }
                }

                // now we own the lock
                newLock.setOwner(Thread.currentThread());

                // return the cookie
                return newLock.getCookie();
            }
        }
    }

    /**
     * Releases the lock on a record. <code>lockCookie</code>
     * must be the cookie returned when the record was locked;
     * otherwise throws <code>SecurityException</code>.
     *
     * @param recNo
     * the record number; 0-based.
     *
     * @param lockCookie
     * the cookie that uniquely identifies a lock.
     *
     * @throws RecordNotFoundException
     * if the specified record does not exist,
     * or is marked as deleted in the database file.
     */
    public void unlock(final int recNo, final long lockCookie)
        throws RecordNotFoundException {

        // validate arguments
        if ((recNo < 0) || (recNo >= dataRecords.size())) {
            throw new RecordNotFoundException(
                "invalid record number " + recNo);
        }

        final DataRecord dataRecord = (DataRecord) dataRecords
            .get(recNo);

        synchronized (dataRecord) {

            // find the lock for this cookie
            final Lock lock = dataRecord.findLock(lockCookie);

            /* remove this lock from the list of locks for this
             * data record; we ignore the returned value
             */
            final boolean removed = dataRecord.removeLock(lock);
            assert removed;

            // notify exactly one (or zero) blocked threads
            dataRecord.notify();
        }
    }

    /**
     * Returns the FieldDescriptors for the database.
     * This method is provided for applications that need to
     * retrieve the field names.
     *
     * @return
     * an array of FieldDescriptors.
     */
    public FieldDescriptor[] getFieldDescriptors() {

        // return a copy of the array object
        return (FieldDescriptor[]) fieldDescriptors.clone();
    }

    /**
     * Write a data record to disk.
     *
     * @param recNo
     * the record number; 0-based.
     *
     * @param dataRecord
     * reference to a DataRecord object.
     *
     * @throws IOException
     * if a RandomAccessFile operation fails.
     */
    private void writeRecord(
        final int recNo,
        final DataRecord dataRecord) throws IOException {

        /* calculate the record position; the 'deleted' byte is
         * not included in the 'bytesPerRecord' value
         */
        final long position = startOfData
            + (recNo * (1 + bytesPerRecord));

        // seek to the record position
        randomAccessFile.seek(position);

        // write the 'deleted' byte
        randomAccessFile.writeByte(dataRecord.isDeleted()
            ? DELETED_RECORD
            : VALID_RECORD);

        // write the data values

        final String[] values = dataRecord.getValues();

        for (int i = 0; i < values.length; i++) {

            // fill data byte array with spaces
            Arrays.fill(
                dataBytes,
                (byte) ' ');

            // copy String value to data byte array
            System.arraycopy(
                values[i].getBytes(),
                0,
                dataBytes,
                0,
                values[i].length());

            // write data byte array to file
            randomAccessFile.write(
                dataBytes,
                0,
                fieldDescriptors[i].getDataLength());
        }
    }

    /**
     * The <code>FieldDescriptor</code> class encapsulates the
     * descriptions of individual database fields.
     */
    public final class FieldDescriptor {

        /** The field name. */
        private final String fieldName;

        /** The field length, in bytes. */
        private final short dataLength;

        /**
         * Constructs an immutable <code>FieldDescriptor</code>.
         *
         * @param newName
         * field name.
         *
         * @param newLength
         * field length, in bytes.
         */
        FieldDescriptor(
            final String newName,
            final short newLength) {

            fieldName = newName;
            dataLength = newLength;
        }

        /**
         * Returns the field name.
         *
         * @return
         * field name.
         */
        public String getFieldName() {

            return fieldName;
        }

        /**
         * Returns the field length, in bytes.
         *
         * @return
         * field length, in bytes.
         */
        public short getDataLength() {

            return dataLength;
        }
    }

    /**
     * The <code>DataRecord</code> class encapsulates the
     * state of data records.
     */
    private final class DataRecord {

        /** The 'deleted' status for a record. */
        private boolean deleted;

        /** The array of data values for a record. */
        private String[] values;

        /** List of locks for this record. */
        private List locks = new ArrayList();

        /**
         * Constructs a <code>DataRecord</code>.
         *
         * @param newDeleted
         * 'true' if the data record was deleted, else 'false'.
         *
         * @param newValues
         * the array of data values for a record.
         */
        DataRecord(
            final boolean newDeleted,
            final String[] newValues) {

            deleted = newDeleted;
            values = newValues;
        }

        /**
         * Sets the 'deleted' status for a record.
         *
         * @param newDeleted
         * 'true' if the record was deleted, else 'false'.
         */
        void setDeleted(final boolean newDeleted) {

            deleted = newDeleted;
        }

        /**
         * Returns the 'deleted' status for a record.
         *
         * @return
         * 'true' if the record was deleted, else 'false'.
         */
        boolean isDeleted() {

            return deleted;
        }

        /**
         * Sets the array of data values for a record.
         *
         * @param newValues
         * the array of data values for a record.
         */
        void setValues(final String[] newValues) {

            values = newValues;
        }

        /**
         * Returns the array of data values for a record.
         *
         * @return values
         * the array of data values for a record.
         */
        String[] getValues() {

            return values;
        }

        /**
         * Adds a <code>Lock</code> to the list of locks for this
         * data record.
         *
         * @param lock
         * the <code>Lock</code> to add.
         */
        void addLock(final Lock lock) {

            locks.add(lock);
        }

        /**
         * Removes a <code>Lock</code> from the list of locks for
         * this record.
         *
         * @param lock
         * the <code>Lock</code> to remove.
         *
         * @return
         * <code>true</code> if the collection contained the
         * specified element,
         * else <code>false</code>.
         */
        boolean removeLock(final Lock lock) {

            return locks.remove(lock);
        }

        /**
         * Return an <code>Iterator</code> for
         * <code>lockers</code>.
         *
         * @return
         * an <code>Iterator</code> for <code>lockers</code>.
         */
        Iterator getLockIterator() {

            return locks.iterator();
        }

        /**
         * Return the <code>Lock</code> associated with the
         * specified lock cookie.
         *
         * @param lockCookie
         * the lock cookie.
         *
         * @return
         * the <code>Lock</code> associated with the specified
         * lock cookie.
         */
        synchronized Lock findLock(final long lockCookie) {

            // find the lock for this cookie
            final Iterator iterator = locks.iterator();
            while (iterator.hasNext()) {

                final Lock lock = (Lock) iterator.next();

                // check the cookie
                if (lockCookie == lock.getCookie()) {

                    /* validate the thread; this is a sanity
                     * check that could be removed to improve
                     * performance
                     */
                    if (Thread.currentThread() != lock
                        .getOwner()) {
                        throw new SecurityException(
                            "expected thread "
                                + Thread
                                    .currentThread()
                                    .getName()
                                + ", found "
                                + lock.getOwner().getName());
                    }

                    // matched and validated; return the 'Lock'
                    return lock;
                }
            }
            throw new SecurityException("lock cookie not found");
        }
    }

    /**
     * The <code>Lock</code> class encapsulates the record
     * locking object.
     */
    private final class Lock {

        /**
         * The <code>Thread</code> that owns the lock.
         *
         * This will be <code>null</code> until a thread acquires
         * the lock.
         */
        private Thread owner;

        /** The lock cookie. */
        private final long cookie;

        /**
         * Hide the no-argument contructor.
         * The no argument contructor should never be used.
         */
        private Lock() {

            /* Keep the compiler happy by initializing all final
             * instance variables -- even though this constructor
             * is never used.
             */
            cookie = -1;
        }

        /**
         * Constructs a <code>Lock</code>.
         *
         * @param newCookie
         * the lock cookie.
         */
        Lock(final long newCookie) {

            cookie = newCookie;
        }

        /**
         * Returns the lock cookie value.
         * The cookie value is immutable.
         *
         * @return
         * the lock cookie value.
         */
        long getCookie() {

            return cookie;
        }

        /**
         * Returns the <code>Thread</code> that owns the lock.
         *
         * @return
         * the <code>Thread</code> that owns the lock.
         */
        Thread getOwner() {

            return owner;
        }

        /**
         * Sets the <code>Thread</code> that owns the lock.
         *
         * @param newOwner
         * the <code>Thread</code> that owns the lock.
         */
        void setOwner(final Thread newOwner) {

            owner = newOwner;
        }
    }
}

