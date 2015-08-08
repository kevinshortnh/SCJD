/*
 * @(#)DB.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.db;

/**
 * This is the <code>DB</code> interface for Bodgitt and Scarper,
 * LLC.
 *
 * @version 1.0
 * @author Kevin Short
 */
public interface DB {

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
    public String[] read(int recNo)
        throws RecordNotFoundException;

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
     *
     * @throws SecurityException
     * if the record is locked with a cookie other than
     * <code>lockCookie</code>.
     */
    public void update(int recNo, String[] data, long lockCookie)
        throws RecordNotFoundException, SecurityException;

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
     *
     * @throws SecurityException
     * if the record is locked with a cookie other than
     * <code>lockCookie</code>.
     */
    public void delete(int recNo, long lockCookie)
        throws RecordNotFoundException, SecurityException;

    /**
     * Returns an array of record numbers that match the
     * specified criteria.
     * Field <code>n</code> in the database file is described by
     * <code>criteria[n]</code>.
     * A <code>null</code> value in <code>criteria[n]</code>
     * matches any field value.
     * A non-<code>null</code> value in <code>criteria[n]</code>
     * matches any field value that begins with
     * <code>criteria[n]</code>.
     * (For example, <i>"Fred"</i> matches <i>"Fred"</i> or
     * <i>"Freddy"</i>.)
     *
     * @param criteria
     * match criteria; <code>null</code> matches any value.
     *
     * @return
     * an array of record numbers that match the specified
     * criteria.
     */
    public int[] find(String[] criteria);

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
     * record number of the new record.
     *
     * @throws DuplicateKeyException
     * if the fields in the given data would create a duplicate
     * key condition;
     * this is implementation - specific behavior.
     */
    public int create(String[] data)
        throws DuplicateKeyException;

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
    public long lock(int recNo) throws RecordNotFoundException;

    /**
     * Releases the lock on a record. Cookie must be the cookie
     * returned when the record was locked; otherwise throws
     * <code>SecurityException</code>.
     *
     * @param recNo
     * the record number; 0-based.
     *
     * @param cookie
     * the cookie that uniquely identifies a lock.
     *
     * @throws RecordNotFoundException
     * if the specified record does not exist or is marked as
     * deleted in the database file.
     *
     * @throws SecurityException
     * if the record is locked with a cookie other than
     * <code>lockCookie</code>.
     */
    public void unlock(int recNo, long cookie)
        throws RecordNotFoundException, SecurityException;
}

