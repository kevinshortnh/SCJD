/*
 * @(#)SubcontratorTableModel.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import suncertify.protocol.Subcontractor;

/**
 * The <code>SubcontractorTableModel</code> class extends
 * the <code>AbstractTableModel</code> abstract class,
 * providing the information that is displayed in our JTable.
 */
public final class SubcontratorTableModel extends
    AbstractTableModel {

    /** The <code>CSRGUI</code> object. */
    private CSRGUI csrgui;

    /** The column headers. */
    private final String[] headers = new String[] {
        "Subcontractor Name",
        "City",
        "Types of Work Performed",
        "Staff",
        "Hourly",
        "Customer"
    };

    /** The data types. */
    private final Class[] types = new Class[] {
        String.class,
        String.class,
        String.class,
        Integer.class,
        String.class,
        Long.class
    };

    /** The data. */
    private Object[][] rows;

    /**
     * Constructor.
     *
     * @param o
     * the <code>CSRGUI</code> object.
     */
    SubcontratorTableModel(final CSRGUI o) {

        if (null == o) {
            throw new NullPointerException();
        }

        csrgui = o;
        rows = new Object[0][];
    }

    /**
     * Returns the number of columns.
     *
     * @return the number of columns.
     */
    public int getColumnCount() {

        return headers.length;
    }

    /**
     * Returns the column header for the specified column.
     *
     * @param column
     * a column index.
     *
     * @return the column header for the specified column.
     */
    public String getColumnName(final int column) {

        return headers[column];
    }

    /**
     * Returns the <code>Class</code> for the data in the
     * specified column.
     *
     * @param column
     * a column index.
     *
     * @return the <code>Class</code> for the data in the
     * specified column.
     */
    public Class getColumnClass(final int column) {

        return types[column];
    }

    /**
     * Returns the number of rows.
     *
     * @return the number of rows.
     */
    public int getRowCount() {

        return rows.length;
    }

    /**
     * Returns the value at the specified row and column.
     *
     * @param row
     * a row index.
     *
     * @param column
     * a column index.
     *
     * @return the value at the specified row and column.
     */
    public Object getValueAt(final int row, final int column) {

        return rows[row][column];
    }

    /**
     * Override method, to prevent editing of cells.
     *
     * @param row
     * the row index.
     *
     * @param column
     * the column index.
     *
     * @return <code>false</code>.
     */
    public boolean isCellEditable(final int row, final int column) {

        assert row >= 0;
        assert column >= 0;

        return false;
    }

    /**
     * Returns the <code>Subcontractor</code> object for the
     * specified row.
     *
     * @param row
     * a row index.
     *
     * @return the <code>Subcontractor</code> object for the
     * specified row.
     */
    public Object getSubcontractor(final int row) {

        return rows[row][rows[row].length - 1];
    }

    /**
     * Sets the Customer Id for the specified row.
     *
     * @param row
     * a row index.
     *
     * @param customerId
     * the Customer Id.
     */
    public void setCustomerId(
        final int row,
        final long customerId) {

        rows[row][rows[row].length - 2] = new Long(customerId);
    }

    /**
     * Update the JTable with new values.
     *
     * @param subcontractors
     * an array of <code>Subcontractor</code> objects.
     */
    void updateValues(final Subcontractor[] subcontractors) {

        if (null == subcontractors) {
            throw new NullPointerException();
        }

        // get maximum width of 'HourlyCharge' data values
        int maxHourlyChargeWidth = 0;
        for (int row = 0; row < subcontractors.length; row++) {

            maxHourlyChargeWidth = Math.max(
                maxHourlyChargeWidth,
                subcontractors[row].getHourlyCharge().length());
        }
        final StringBuffer stringBuffer = new StringBuffer(
            maxHourlyChargeWidth);

        // all new values
        rows = new Object[subcontractors.length][];

        // copy new values for the JTable
        for (int row = 0; row < subcontractors.length; row++) {

            /* Create a right-aligned copy of the
             * 'HourlyCharge' data by left-padding with
             * spaces.
             *
             * The column will still be rendeSred with left
             * alignment, but it will sort properly.
             *
             * (Suggested long-term solution: support locale
             * specific sort methods that address the
             * currency symbols that are embedded inS the
             * data.)
             */
            stringBuffer.delete(
                0,
                stringBuffer.length());
            for (int i = 0; i < (maxHourlyChargeWidth - subcontractors[row]
                .getHourlyCharge()
                .length()); i++) {
                stringBuffer.append(' ');
            }
            stringBuffer.append(subcontractors[row]
                .getHourlyCharge());

            rows[row] = new Object[headers.length + 1];
            int column = 0;

            // the visible columns
            rows[row][column++] = subcontractors[row]
                .getSubcontractorName();
            rows[row][column++] = subcontractors[row].getCity();
            rows[row][column++] = subcontractors[row]
                .getTypesOfWorkPerformed();
            rows[row][column++] = subcontractors[row]
                .getNumberOfStaffInOrganization();
            rows[row][column++] = stringBuffer.toString();
            rows[row][column++] = subcontractors[row]
                .getCustomerId();

            // append the hidden object
            rows[row][column++] = subcontractors[row];
        }

        // update the table
        csrgui.updateTable(
            0,
            true);
    }

    /**
     * Sort the table row data by the specified column.
     *
     * @param column
     * the column index.
     *
     * @param isAscending
     * <code>true</code> for ascending, <code>false</code>
     * for descending.
     */
    void sortTable(final int column, final boolean isAscending) {

        // convert the array to a List
        final List data = Arrays.asList(rows);

        // sort the List
        Collections.sort(
            data,
            new ColumnSorter(column, isAscending));

        // convert the List back to an array
        rows = (Object[][]) data.toArray();

        // fire any listeners
        fireTableStructureChanged();
    }

    /**
     * The <code>ColumnSorter</code> class implements the
     * <code>ColumnSorter</code> interface, to provide
     * sorting for the JTable data.
     */
    private final class ColumnSorter implements Comparator {

        /** The column index. */
        private final int column;

        /** Ascending (true) or descending (false). */
        private final boolean isAscending;

        /**
         * Constructor.
         *
         * @param newColumn
         * the column index.
         *
         * @param newIsAscending
         * <code>true</code> for ascending,
         * <code>false</code> for descending.
         */
        ColumnSorter(
            final int newColumn,
            final boolean newIsAscending) {

            column = newColumn;
            isAscending = newIsAscending;
        }

        /**
         * Compare two objects.
         *
         * @param a
         * the first <code>Object</code>.
         *
         * @param b
         * the second <code>Object</code>.
         *
         * @return -1 if a < b, 0 if a == b, 1 if a > b.
         */
        public int compare(final Object a, final Object b) {

            if ((null == a) || (null == b)) {
                throw new NullPointerException();
            }

            final Object[] objectArrayA = (Object[]) a;
            final Object[] objectArrayB = (Object[]) b;

            Object objectA = objectArrayA[column];
            Object objectB = objectArrayB[column];

            // nulls and empty Strings are equivalent
            if (objectA instanceof String
                && (0 == ((String) objectA).length())) {
                objectA = null;
            }
            if (objectB instanceof String
                && (0 == ((String) objectB).length())) {
                objectB = null;
            }

            // make nulls appear last
            if ((null == objectA) && (null == objectB)) {
                return 0;
            } else if (null == objectA) {
                return 1;
            } else if (null == objectB) {
                return -1;
            } else if (objectA instanceof Comparable) {
                if (isAscending) {
                    return ((Comparable) objectA)
                        .compareTo(objectB);
                } else {
                    return ((Comparable) objectB)
                        .compareTo(objectA);
                }
            } else {
                if (isAscending) {
                    return objectA.toString().compareTo(
                        objectB.toString());
                } else {
                    return objectB.toString().compareTo(
                        objectA.toString());
                }
            }
        }
    }
}

